#version 430

uniform mat4 v_matrix;
uniform mat4 proj_matrix;

layout (location = 0) in vec3 position;

out vec3 varyingTexCoord;

layout (binding = 0) uniform samplerCube cubeMapSampler;

void main(void) {
  gl_Position = proj_matrix * mat4(mat3(v_matrix)) * vec4(position, 1.0);
  varyingTexCoord = position;
}
