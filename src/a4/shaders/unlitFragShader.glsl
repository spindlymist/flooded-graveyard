#version 430

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;
uniform bool use_color;
uniform bool use_texture;

layout (binding = 0) uniform sampler2D sampler0;
layout (binding = 1) uniform sampler2D sampler1;
layout (binding = 2) uniform sampler2D sampler2;
layout (binding = 3) uniform sampler2D sampler3;

in vec4 varyingColor;
in vec2 varyingTexCoord;
flat in int fragTexUnitIndex;

out vec4 color;

vec4 getTextureColor(vec2 texCoord) {
	if(fragTexUnitIndex == 1) return texture(sampler1, texCoord);
	else if(fragTexUnitIndex == 2) return texture(sampler2, texCoord);
	else if(fragTexUnitIndex == 3) return texture(sampler3, texCoord);
	else return texture(sampler0, texCoord);
}

void main(void) {
	if(use_texture && use_color) {
		color = varyingColor * getTextureColor(varyingTexCoord);
	}
	else if(use_texture) {
		color = getTextureColor(varyingTexCoord);
	}
	else {
		color = varyingColor;
	}
}
