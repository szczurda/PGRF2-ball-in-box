#version 450
in vec2 inPosition; // input from the vertex buffer
in vec3 inNormal; // input from the vertex buffer
in vec2 inTextureCoordinates; // input from the vertex buffer
out vec2 texCoord;
uniform mat4 mat;

vec2 mirror(vec2 xy);

void main() {
	vec2 position = inPosition;
	texCoord = inTextureCoordinates;
	
	if (position.x > 0.5) {
		position = mirror(position);
	} 
	
	gl_Position = mat*vec4(position, 0.0, 1.0); 
} 
