package lvl0fixpipeline.global;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ColorSelector;
import de.matthiasmann.twl.Widget;
import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import projekt.ControlPanel;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author PGRF FIM UHK
 * @version 3.1
 * @since 2020-01-20
 */
public abstract class AbstractRenderer {

    private int pass;
    protected int width;
    protected int height;
    protected OGLTextRenderer textRenderer;

    public AbstractRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public AbstractRenderer() {
        this.width = 600;
        this.height = 400;

    }

    public void init() {
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        textRenderer = new OGLTextRenderer(width, height);

    }

    public void display() {
        glViewport(0, 0, width, height);
        pass++;
        // Set the clear color
        glClearColor(
                (float) (Math.sin(pass / 100.) / 2 + 0.5),
                (float) (Math.cos(pass / 200.) / 2 + 0.5),
                (float) (Math.sin(pass / 300.) / 2 + 0.5),
                0.0f
        );
        // clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //create and draw text
    }

    protected GLFWKeyCallback glfwKeyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                // We will detect this in our rendering loop
                glfwSetWindowShouldClose(window, true);
            if (action == GLFW_RELEASE) {
                System.out.println("Key release " + key);
            }
            if (action == GLFW_PRESS) {
                System.out.println("Key pressed " + key);
            }
        }
    };

    protected GLFWWindowSizeCallback glfwWindowSizeCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0) {
                width = w;
                height = h;
                System.out.println("Windows resize to [" + w + ", " + h + "]");
                if (textRenderer != null) {
                    textRenderer.resize(width, height);
                }
            }
        }
    };

    protected GLFWMouseButtonCallback glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

        @Override
        public void invoke(long window, int button, int action, int mods) {
            DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(window, xBuffer, yBuffer);
            double x = xBuffer.get(0);
            double y = yBuffer.get(0);

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                System.out.println("Mouse button 1 is pressed at cursor position [" + x + ", " + y + "]");
            }

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
                System.out.println("Mouse button 1 is released at cursor position [" + x + ", " + y + "]");
            }
        }

    };

    protected GLFWCursorPosCallback glfwCursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            System.out.println("Cursor position [" + x + ", " + y + "]");
        }
    };

    protected GLFWScrollCallback glfwScrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            System.out.println("Mouse wheel velocity " + dy);
        }
    };

    public GLFWKeyCallback getGlfwKeyCallback() {
        return glfwKeyCallback;
    }

    public GLFWWindowSizeCallback getGlfwWindowSizeCallback() {
        return glfwWindowSizeCallback;
    }

    public GLFWMouseButtonCallback getGlfwMouseButtonCallback() {
        return glfwMouseButtonCallback;
    }

    public GLFWCursorPosCallback getGlfwCursorPosCallback() {
        return glfwCursorPosCallback;
    }

    public GLFWScrollCallback getGlfwScrollCallback() {
        return glfwScrollCallback;
    }

    public void dispose() {

    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


}
