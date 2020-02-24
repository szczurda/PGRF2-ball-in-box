#version 450
in vec2 texCoord;
uniform float value;

uniform sampler2D textureID;

vec4 green() {
	//return vec4(0.0, value, 0.0, 1.0);
	return texture(textureID,texCoord); 
} 
