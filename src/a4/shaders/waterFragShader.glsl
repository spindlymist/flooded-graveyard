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
// Lighting
uniform vec4 globalAmbient;
uniform PointLight pointLight;
uniform Material material;
// Fog
uniform vec4 fogColor;
uniform float fogStart;
uniform float fogEnd;
// Shadows
uniform float shadowFarPlane;

layout (binding = 0) uniform sampler2D sampler0;
layout (binding = 1) uniform sampler2D sampler1;
layout (binding = 2) uniform sampler2D sampler2;
layout (binding = 3) uniform sampler2D sampler3;
layout (binding = 4) uniform samplerCube shadowSampler;
layout (binding = 5) uniform sampler2D normMap;
layout (binding = 6) uniform sampler2D refractionSampler;
layout (binding = 7) uniform sampler2D reflectionSampler;
layout (binding = 9) uniform sampler3D noiseMap;

in vec4 glPos;
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

bool isInShadow(vec3 waveN) {
    // Get the normalized distance from the light to the closest surface in the direction of this fragment
    vec3 distortedPos = vec3(worldPosition.x + waveN.x * .1, worldPosition.y, worldPosition.z + waveN.z * .1);
    vec3 posRelativeToLight = distortedPos - pointLight.worldPosition;
    float depth = length(posRelativeToLight);
    float closestDepth = texture(shadowSampler, posRelativeToLight).r;
    // "Unnormalize" the distance
    closestDepth *= shadowFarPlane;

    // Compare with the distance to this fragment
    float bias = 0.05; // Reduce shadow acne
    bool inShadow = depth - bias > closestDepth;

    return inShadow;
}

void applyLighting(vec3 waveN) {
    vec3 L = normalize(varyingLightDir);
    vec3 N = waveN;
    if(!gl_FrontFacing) {
        N = -N;
    }
    vec3 V = normalize(-varyingPosition);
    vec3 R = normalize(reflect(-L, N));

    float distToLight = length(varyingLightDir);
    float attenuation = exp(-distToLight / 12.0);

    vec3 ambient = (globalAmbient * material.ambient).xyz;
    ambient += (attenuation * pointLight.ambient * material.ambient).xyz;

    if(isInShadow(waveN)) {
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

float calcFresnel() {
    vec3 N = normalize(varyingNormal);
    vec3 V = normalize(-varyingPosition);
    float fresnel = acos(dot(V, N)) - 0.3;
    fresnel = pow(clamp(fresnel, 0.0, 1.0), 3.0);

    return fresnel;
}

void main(void) {
    vec3 waveN = estimateWaveNormal(.001, 48.0, 16.0);

    color = vec4(0.1, 0.2, 0.7, 1.0);
    applyLighting(waveN);


    vec2 refractionST = glPos.xy / (2.0 * glPos.w) + 0.5;
    vec2 reflectionST = vec2(glPos.x, -glPos.y) / (2.0 * glPos.w) + 0.5;

    // Distort refraction/reflection coordinates
    refractionST.s += waveN.x * .01;
    refractionST.t += waveN.z * .01;
    reflectionST.s += waveN.x * .01;
    reflectionST.t += waveN.z * .01;

    vec3 refractionColor = texture(refractionSampler, refractionST).xyz;
    vec3 reflectionColor = texture(reflectionSampler, reflectionST).xyz;
    float fresnel = calcFresnel();

    if(gl_FrontFacing) {
        vec3 reflectRefractColor = mix(refractionColor, reflectionColor, fresnel);
        color = vec4(mix(color.xyz, reflectRefractColor, 0.5), 1.0);
    }
    else {
        color = vec4(mix(color.xyz, refractionColor, 0.5), 1.0);
    }

    applyFog();
}
