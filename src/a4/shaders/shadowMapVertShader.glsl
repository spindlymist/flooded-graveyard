#version 430

uniform vec3 lightPosition;
uniform float farPlane;
uniform mat4 m_matrix;
uniform mat4 proj_matrix;
uniform mat4 v_matrices[6];

layout (location = 0) in vec3 position;

void main(void) {
    gl_Position = m_matrix * vec4(position, 1.0);
}
