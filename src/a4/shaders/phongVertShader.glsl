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

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec4 color;
layout (location = 4) in int texUnitIndex;
layout (location = 5) in vec3 tangent;

out vec3 worldPosition;
out vec3 varyingPosition;
out vec2 varyingTexCoord;
out vec3 varyingNormal;
out vec3 varyingTangent;
out vec4 varyingColor;
out vec3 varyingLightDir;
flat out int fragTexUnitIndex;

layout (binding = 0) uniform sampler2D sampler0;
layout (binding = 1) uniform sampler2D sampler1;
layout (binding = 2) uniform sampler2D sampler2;
layout (binding = 3) uniform sampler2D sampler3;
layout (binding = 4) uniform samplerCube shadowSampler;
layout (binding = 5) uniform sampler2D normMap;
layout (binding = 9) uniform sampler3D noiseMap;

void main(void) {
	worldPosition = (m_matrix * vec4(position, 1.0)).xyz;
	varyingPosition = (v_matrix * vec4(worldPosition, 1.0)).xyz;
	varyingColor = color;
	varyingTexCoord = texCoord;
	varyingNormal = (norm_matrix * vec4(normal, 1.0)).xyz;
	varyingTangent = (norm_matrix * vec4(tangent, 1.0)).xyz;
	varyingLightDir = pointLight.position - varyingPosition;
	fragTexUnitIndex = texUnitIndex;

	gl_Position = proj_matrix * vec4(varyingPosition, 1.0);
}
