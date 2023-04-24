#version 430

uniform vec4 pointColor;
uniform mat4 mvp_matrix;

void main() {
    gl_Position = mvp_matrix * vec4(0.0, 0.0, 0.0, 1.0);
}
