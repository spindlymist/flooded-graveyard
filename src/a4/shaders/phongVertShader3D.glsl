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

layout (location = 0) in vec3 position;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec4 color;

out vec3 originalPos;
out vec3 worldPosition;
out vec3 varyingPosition;
out vec3 varyingNormal;
out vec4 varyingColor;
out vec3 varyingLightDir;

layout (binding = 8) uniform sampler3D sampler;
layout (binding = 9) uniform sampler3D noiseMap;
layout (binding = 4) uniform samplerCube shadowSampler;

void main(void) {
  originalPos = position;
  worldPosition = (m_matrix * vec4(position, 1.0)).xyz;
  varyingPosition = (v_matrix * vec4(worldPosition, 1.0)).xyz;
  varyingColor = color;
  varyingNormal = (norm_matrix * vec4(normal, 1.0)).xyz;
  varyingLightDir = pointLight.position - varyingPosition;

  gl_Position = proj_matrix * vec4(varyingPosition, 1.0);
}
