#version 430

uniform vec4 pointColor;
uniform mat4 mvp_matrix;

out vec4 color;

void main() {
    color = pointColor;
}
