#version 450
in vec2 texCoord;
uniform float value;
uniform sampler2D textureID;
uniform sampler2D textureID2;
out vec4 outColor; 

vec4 green();

void main() {
	if (value>0.5)
		outColor = green(); 
	else
		outColor = texture(textureID2,texCoord);
} 
