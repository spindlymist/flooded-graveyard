#version 430

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;
uniform bool use_color;
uniform bool use_texture;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec4 color;
layout (location = 4) in int texUnitIndex;

out vec4 varyingColor;
out vec2 varyingTexCoord;
flat out int fragTexUnitIndex;

layout (binding = 0) uniform sampler2D sampler0;
layout (binding = 1) uniform sampler2D sampler1;
layout (binding = 2) uniform sampler2D sampler2;
layout (binding = 3) uniform sampler2D sampler3;

void main(void) {
  gl_Position = proj_matrix * v_matrix * m_matrix * vec4(position, 1.0);
  varyingColor = color;
  varyingTexCoord = texCoord;
  fragTexUnitIndex = texUnitIndex;
}
