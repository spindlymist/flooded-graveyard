#version 430

layout (triangles) in;
layout (triangle_strip, max_vertices=18) out;

uniform vec3 lightPosition;
uniform float farPlane;
uniform mat4 m_matrix;
uniform mat4 proj_matrix;
uniform mat4 v_matrices[6];

out vec4 worldPosition;

void main() {
    // Emit this triangle to each of the six cubemap faces
    for(int face = 0; face < 6; face++) {
        gl_Layer = face;

        for(int vertex = 0; vertex < 3; vertex++) {
            worldPosition = gl_in[vertex].gl_Position; // Get incoming world space position from vertex shader
            gl_Position = proj_matrix * v_matrices[face] * worldPosition; // Transform vertex to NDC
            EmitVertex();
        }
        EndPrimitive();
    }
}
