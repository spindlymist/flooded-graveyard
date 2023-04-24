#version 430

uniform mat4 v_matrix;
uniform mat4 proj_matrix;

in vec3 varyingTexCoord;

layout (binding = 0) uniform samplerCube cubeMapSampler;

out vec4 color;

void main(void) {
	color = vec4(texture(cubeMapSampler, varyingTexCoord).rgb, 1.0);
}
