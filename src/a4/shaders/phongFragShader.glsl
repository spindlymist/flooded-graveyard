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
uniform bool use_norm_map;
uniform bool use_tex_unit_idx;
uniform bool alpha_cutout;
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

layout (binding = 0) uniform sampler2D sampler0;
layout (binding = 1) uniform sampler2D sampler1;
layout (binding = 2) uniform sampler2D sampler2;
layout (binding = 3) uniform sampler2D sampler3;
layout (binding = 4) uniform samplerCube shadowSampler;
layout (binding = 5) uniform sampler2D normMap;
layout (binding = 9) uniform sampler3D noiseMap;

in vec3 worldPosition;
in vec3 varyingPosition;
in vec2 varyingTexCoord;
in vec3 varyingNormal;
in vec3 varyingTangent;
in vec4 varyingColor;
in vec3 varyingLightDir;
flat in int fragTexUnitIndex;

out vec4 color;

vec4 getTextureColor(vec2 texCoord) {
	if(use_tex_unit_idx) {
		if (fragTexUnitIndex == 1) return texture(sampler1, texCoord);
		else if (fragTexUnitIndex == 2) return texture(sampler2, texCoord);
		else if (fragTexUnitIndex == 3) return texture(sampler3, texCoord);
	}

	return texture(sampler0, texCoord);
}

void getBaseColor() {
	if(use_texture && use_color) {
		color = varyingColor * getTextureColor(varyingTexCoord);
	}
	else if(use_texture) {
		color = getTextureColor(varyingTexCoord);
	}
	else {
		color = varyingColor;
	}
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

	if(use_norm_map) {
		// Ensure tangent vector is unit length and orthogonal to normal vector
		vec3 tangent = normalize(varyingTangent);
		tangent = normalize(tangent - dot(tangent, normal) * normal);

		// Calculate bitangent and construct TBN matrix
		vec3 bitangent = cross(tangent, normal);
		mat3 tbnMat = mat3(tangent, bitangent, normal);

		// Retrieve tangent-space normal from normal map and convert from RGB
		vec3 normMapNormal = texture(normMap, varyingTexCoord).xyz;
		normMapNormal = normMapNormal * 2.0 - 1.0;

		// Calculate new normal
		normal = normalize(tbnMat * normMapNormal);
	}

	return normal;
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

//	color = vec4(N, 1.0);
//	color = texture(normMap, varyingTexCoord);
}

void applyFog() {
	float distToCamera = length(varyingPosition);
	float fogTransitionDist = fogEnd - fogStart;
	float fogFactor = clamp((distToCamera - fogStart) / fogTransitionDist, 0.0, 1.0);
	float alpha = color.a;
	color = mix(color, fogColor, fogFactor);
	color.a = alpha;
}

void main(void) {
	if(discard_underwater && worldPosition.y < water_level) {
		discard;
	}

	getBaseColor();
	if(alpha_cutout && color.a < .5) discard;

	if(is_underwater) {
		color = mix(color, vec4(0.0, 0.0, 0.5, 1.0), .5);
	}

	applyLighting();
	applyFog();
}
