package lvl0fixpipeline.p01start;

import lvl0fixpipeline.global.AbstractRenderer;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.opengl.GL11.*;

/**
 * Simple scene rendering
 *
 * @author PGRF FIM UHK
 * @version 3.1
 * @since 2020-01-20
 */
public class Renderer extends AbstractRenderer {

    public float angle = 1f;

    public Renderer() {
        super();



        glfwWindowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                if (w > 0 && h > 0) {
                    width = w;
                    height = h;
                }
            }
        };

        /*used default glfwKeyCallback */

        glfwMouseButtonCallback = null; //glfwMouseButtonCallback do nothing

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                System.out.println("glfwCursorPosCallback");
            }
        };

        glfwScrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                //do nothing
            }
        };
    }

    @Override
    public void init() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void display() {
        angle++;
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        // Rendering triangle by fixed pipeline
        glPolygonMode(GL_FRONT, GL_FILL);
        glPolygonMode(GL_BACK, GL_FILL);
        glEnable(GL_BLEND);
        glPushMatrix();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glRotatef(angle,0, 0, 1);

        glBegin(GL_TRIANGLES);
        glColor3f(0f, 0f, 1);
        glVertex2f(-0.5f, -0.5f);
        glVertex2f(0.5f, 0.5f);
        glVertex2f(0.5f, -0.5f);
        glEnd();

        glPopMatrix();

        glRotatef(-angle,0, 0, 1);
        glBegin(GL_TRIANGLES);
        glColor3f(1, 0f, 0f);
        glVertex2f(-0.5f, -0.5f);
        glVertex2f(0.5f, 0.5f);
        glVertex2f(-0.5f, 0.5f);
        glEnd();
        //oba trojuhelniky se posunou do prava pri toceni
    }

}
