#version 430

uniform vec3 lightPosition;
uniform float farPlane;
uniform mat4 m_matrix;
uniform mat4 proj_matrix;
uniform mat4 v_matrices[6];

in vec4 worldPosition;

void main() {
    float distanceToLight = length(worldPosition.xyz - lightPosition);
    float normalizedDistance = distanceToLight / farPlane;
//    normalizedDistance = clamp(normalizedDistance, 0.0, 1.0);
    gl_FragDepth = normalizedDistance;
}
