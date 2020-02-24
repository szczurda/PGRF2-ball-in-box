package lvl2advanced.p05pipeline.p04moreShaders;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import lvl2advanced.p01gui.p01simple.AbstractRenderer;
import lwjglutils.OGLBuffers;
import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLTexture2D;
import lwjglutils.OGLUtils;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

	double ox, oy;
	boolean mouseButton1 = false;
	
	OGLBuffers buffers;
	
	int shaderProgram, locValue, locMat;
	
	OGLTexture2D texture;
	OGLTexture2D texture2;

	Camera cam = new Camera();
	Vec3D cameraPosition;
	Mat4 proj = new Mat4PerspRH(Math.PI / 4, 1, 0.01, 1000.0);
	
	float value = 0;
	OGLTexture2D.Viewer textureViewer;
	
	private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			if (action == GLFW_PRESS || action == GLFW_REPEAT){
				switch (key) {
				case GLFW_KEY_W:
					cam = cam.forward(1);
					break;
				case GLFW_KEY_D:
					cam = cam.right(1);
					break;
				case GLFW_KEY_S:
					cam = cam.backward(1);
					break;
				case GLFW_KEY_A:
					cam = cam.left(1);
					break;
				case GLFW_KEY_LEFT_CONTROL:
					cam = cam.down(1);
					break;
				case GLFW_KEY_LEFT_SHIFT:
					cam = cam.up(1);
					break;
				case GLFW_KEY_SPACE:
					cam = cam.withFirstPerson(!cam.getFirstPerson());
					if (!cam.getFirstPerson()) {
						cameraPosition=cam.getPosition();
						cam = cam.withPosition(new Vec3D(0, 0, 0));
					} else {
						cam = cam.withPosition(cameraPosition);
					}
					break;
				case GLFW_KEY_R:
					cam = cam.mulRadius(0.9f);
					break;
				case GLFW_KEY_F:
					cam = cam.mulRadius(1.1f);
					break;
				case GLFW_KEY_V:
					value = (value + 0.1f)%1;
					break;
					
				}
			}
		}
	};
    
    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
    	@Override
    	public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 && 
            		(w != width || h != height)) {
            	width = w;
            	height = h;
            	proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
            	if (textRenderer != null)
            		textRenderer.resize(width, height);
            }
        }
    };
    
    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
    	@Override
		public void invoke(long window, int button, int action, int mods) {
			mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
			
			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
				mouseButton1 = true;
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				ox = xBuffer.get(0);
				oy = yBuffer.get(0);
			}
			
			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
				mouseButton1 = false;
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				double x = xBuffer.get(0);
				double y = yBuffer.get(0);
				cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
        				.addZenith((double) Math.PI * (oy - y) / width);
				ox = x;
				oy = y;
        	}
		}
	};
	
    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
    	@Override
        public void invoke(long window, double x, double y) {
			if (mouseButton1) {
				cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
						.addZenith((double) Math.PI * (oy - y) / width);
				ox = x;
				oy = y;
			}
    	}
    };
    
	private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
		@Override
		public void invoke(long window, double dx, double dy) {
			if (dy < 0)
				cam = cam.mulRadius(0.9f);
			else
				cam = cam.mulRadius(1.1f);

		}
	};

	@Override
	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

	@Override
	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return cpCallbacknew;
	}

	@Override
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}

	void createBuffers() {
		float[] cube = {
						// bottom (z-) face
						1, 0, 0,	0, 0, -1,	1, 0, 
						0, 0, 0,	0, 0, -1,	0, 0, 
						1, 1, 0,	0, 0, -1,	1, 1, 
						0, 1, 0,	0, 0, -1,	0, 1
						
		};
		int[] indexBufferData = {0, 1, 2, 1, 2, 3};		
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 3),
				new OGLBuffers.Attrib("inNormal", 3),
				new OGLBuffers.Attrib("inTextureCoordinates", 2)
		};

		buffers = new OGLBuffers(cube, attributes, indexBufferData);
	}

	@Override
	public void init() {
		OGLUtils.printOGLparameters();
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		createBuffers();
		
		String[] shaderNames = {
				"/lvl2advanced/p05pipeline/p04moreShaders/start.frag",
				"/lvl2advanced/p05pipeline/p04moreShaders/func.frag",
				"/lvl2advanced/p05pipeline/p04moreShaders/func.vert",
				"/lvl2advanced/p05pipeline/p04moreShaders/start.vert",
				"/lvl2advanced/p05pipeline/p04moreShaders/empty.vert"
				};
		int[] shaderTypes = {
				GL_FRAGMENT_SHADER,
				GL_FRAGMENT_SHADER,
				GL_VERTEX_SHADER,
				GL_VERTEX_SHADER,
				GL_VERTEX_SHADER
				};
		try {
			texture = new OGLTexture2D("textures/mosaic.jpg");
			texture2 = new OGLTexture2D("textures/testTexture.jpg");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		shaderProgram = ShaderUtils.loadProgramMultiple(shaderNames, shaderTypes, (shaderProgram)->{}); 
		
		cameraPosition = new Vec3D(5, 5, 2.5);
		cam = cam.withPosition(new Vec3D(0, 0, 0))
				.withAzimuth(Math.PI * 1.25)
				.withZenith(Math.PI * -0.125)
				.withFirstPerson(false)
				.withRadius(5);
		
		
		glUseProgram(this.shaderProgram);
		
		locValue = glGetUniformLocation(shaderProgram, "value");
		textRenderer = new OGLTextRenderer(width, height);
		textureViewer = new OGLTexture2D.Viewer();
		
}
	
	@Override
	public void display() {
		String text = new String(this.getClass().getName() + ": [LMB] camera, WSAD, value: " + value);

		glViewport(0, 0, width, height);
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
		
		// set the current shader to be used
		glUseProgram(shaderProgram); 
		
		locMat = glGetUniformLocation(shaderProgram, "mat");
		glUniformMatrix4fv(locMat, false,
				ToFloatArray.convert(cam.getViewMatrix().mul(proj)));
		
		glUniform1f(locValue,value);
		
		texture.bind(shaderProgram, "textureID", 0);
		texture2.bind(shaderProgram, "textureID2", 1);
		
		// bind and draw
		buffers.draw(GL_TRIANGLES, shaderProgram);
		
		textureViewer.view(texture, -1, -1, 0.5);
		textureViewer.view(texture2, -1, -0.5, 0.5);
		
		textRenderer.clear();
		textRenderer.addStr2D(3, 20, text);
		textRenderer.addStr2D(width-90, height-3, " (c) PGRF UHK");
		textRenderer.draw();
		
	}
}