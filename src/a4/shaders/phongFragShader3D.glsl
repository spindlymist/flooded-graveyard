#version 430

struct PointLight {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	vec3 position;
	vec3 worldPosition;
};
struct Material {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	float shininess;
};

uniform float time;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;
uniform mat4 norm_matrix;
uniform bool use_color;
uniform bool use_texture;
// Lighting
uniform vec4 globalAmbient;
uniform PointLight pointLight;
uniform Material material;
// Fog
uniform vec4 fogColor;
uniform float fogStart;
uniform float fogEnd;
// Water
uniform bool is_underwater;
uniform bool discard_underwater;
uniform float water_level;
// Shadows
uniform float shadowFarPlane;

layout (binding = 8) uniform sampler3D sampler;
layout (binding = 9) uniform sampler3D noiseMap;
layout (binding = 4) uniform samplerCube shadowSampler;

in vec3 originalPos;
in vec3 worldPosition;
in vec3 varyingPosition;
in vec3 varyingNormal;
in vec4 varyingColor;
in vec3 varyingLightDir;

out vec4 color;

void getBaseColor() {
	if(use_texture && use_color) {
		color = varyingColor * texture(sampler, originalPos / 2.0 + 0.5);
	}
	else if(use_texture) {
		color = texture(sampler, originalPos / 2.0 + 0.5);
	}
	else {
		color = varyingColor;
	}
}

vec3 estimateWaveNormal(float offset, float mapScale, float hScale) {
	float height = mod(time / 8.0, 256.0);

	vec2 tc = vec2(
		(worldPosition.x - 128.0) / 256.0,
		(worldPosition.z - 128.0) / 256.0
	);

	vec3 lookup1 = vec3((tc.s         ) * mapScale, height, (tc.t + offset) * mapScale);
	vec3 lookup2 = vec3((tc.s + offset) * mapScale, height, (tc.t - offset) * mapScale);
	vec3 lookup3 = vec3((tc.s - offset) * mapScale, height, (tc.t - offset) * mapScale);
	float h1 = texture(noiseMap, lookup1).r * hScale;
	float h2 = texture(noiseMap, lookup2).r * hScale;
	float h3 = texture(noiseMap, lookup3).r * hScale;

	vec3 v1 = vec3(0, h1, -1);
	vec3 v2 = vec3(-1, h2, 1);
	vec3 v3 = vec3(1, h3, 1);
	vec3 normal = cross(v2 - v1, v3 - v1);

	return normalize(normal);
}

vec3 getNormal() {
	vec3 normal;

	if(worldPosition.y <= water_level) {
		normal = estimateWaveNormal(.001, 48.0, 16.0);
	}
	else {
		normal = normalize(gl_FrontFacing ? varyingNormal : -varyingNormal);
	}

	return normal;
}

bool isInShadow() {
	// Get the normalized distance from the light to the closest surface in the direction of this fragment
	vec3 posRelativeToLight = worldPosition - pointLight.worldPosition;
	float depth = length(posRelativeToLight);
	float closestDepth = texture(shadowSampler, posRelativeToLight).r;
	// "Unnormalize" the distance
	closestDepth *= shadowFarPlane;

	// Compare with the distance to this fragment
	float bias = 0.05; // Reduce shadow acne
	bool inShadow = depth - bias > closestDepth;

	return inShadow;
}

void applyLighting() {
	vec3 L = normalize(varyingLightDir);
	vec3 N = getNormal();
	vec3 V = normalize(-varyingPosition);
	vec3 R = normalize(reflect(-L, N));

	float distToLight = length(varyingLightDir);
	float attenuation = exp(-distToLight / 12.0);

	vec3 ambient = (globalAmbient * material.ambient).xyz;
	ambient += (attenuation * pointLight.ambient * material.ambient).xyz;

	if(isInShadow()) {
		color = vec4(color.xyz * ambient, color.a);
	}
	else {
		vec3 diffuse = (attenuation * pointLight.diffuse * material.diffuse).xyz * max(dot(L, N), 0);
		vec3 specular = (attenuation * pointLight.specular * material.specular).xyz * pow(max(dot(V, R), 0), material.shininess);
		color = vec4(color.xyz * (ambient + diffuse) + specular, color.a);
	}
}

void applyFog() {
	float distToCamera = length(varyingPosition);
	float fogTransitionDist = fogEnd - fogStart;
	float fogFactor = clamp((distToCamera - fogStart) / fogTransitionDist, 0.0, 1.0);
	color = mix(color, fogColor, fogFactor);
}

void main(void) {
	if(discard_underwater && worldPosition.y < water_level) {
		discard;
	}

	getBaseColor();

	if(is_underwater) {
		color = mix(color, vec4(0.0, 0.0, 0.5, 1.0), .5);
	}

	if(color.a > .5) {
		applyLighting();
		applyFog();
	}
	else {
		discard;
	}
}
