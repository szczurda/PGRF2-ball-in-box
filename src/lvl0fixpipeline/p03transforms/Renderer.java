package lvl0fixpipeline.p03transforms;

import lvl0fixpipeline.global.AbstractRenderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.DoubleBuffer;
import java.util.Locale;

import static lvl0fixpipeline.global.GluUtils.gluLookAt;
import static lvl0fixpipeline.global.GluUtils.gluPerspective;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Shows using 3D transformation and interaction in a scene
 *
 * @author PGRF FIM UHK
 * @version 3.1
 * @since 2020-01-20
 */
public class Renderer extends AbstractRenderer {
    private double dx, dy;
    private double ox, oy;
    private long oldmils;
    private long oldFPSmils;
    private double fps;

    private float uhel = 0;
    private int mode = 0;
    private float[] modelMatrix = new float[16];

    private boolean per = false, depth = true;
    private boolean mouseButton1 = false;

    public Renderer() {
        super();

        /*used default glfwWindowSizeCallback see AbstractRenderer*/

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_RELEASE) {
                    //do nothing
                }
                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_P:
                            per = !per;
                            break;
                        case GLFW_KEY_D:
                            depth = !depth;
                            break;
                        case GLFW_KEY_M:
                            mode++;
                            break;
                    }
                }
            }
        };

        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);

                mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    ox = x;
                    oy = y;
                }
            }

        };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    dx = x - ox;
                    dy = y - oy;
                    ox = x;
                    oy = y;
                }
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
        super.init();
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        glEnable(GL_DEPTH_TEST);

        glFrontFace(GL_CCW);
        glPolygonMode(GL_FRONT, GL_FILL);
        glPolygonMode(GL_BACK, GL_LINE);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glMatrixMode(GL_MODELVIEW);

        glLoadIdentity();
        glGetFloatv(GL_MODELVIEW_MATRIX, modelMatrix);
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);
        // zapnuti nebo vypnuti viditelnosti
        if (depth)
            glEnable(GL_DEPTH_TEST);
        else
            glDisable(GL_DEPTH_TEST);

        // mazeme image buffer i z-buffer
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        // vypocet fps, nastaveni rychlosti otaceni podle rychlosti prekresleni
        long mils = System.currentTimeMillis();
        if ((mils - oldFPSmils) > 300) {
            fps = 1000 / (double) (mils - oldmils + 1);
            oldFPSmils = mils;
        }

        //System.out.println(fps);
        float speed = 10; // pocet stupnu rotace za vterinu
        float step = speed * (mils - oldmils) / 1000.0f; // krok za jedno prekresleni
        oldmils = mils;

        uhel++;
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // nastaveni transformace zobrazovaciho objemu
        if (per)
            gluPerspective(45, width / (float) height, 0.1f, 100.0f);
        else
            glOrtho(-20 * width / (float) height,
                    20 * width / (float) height,
                    -20, 20, 0.1f, 100.0f);

        // pohledova transformace
        // divame se do sceny z kladne osy x, osa z je svisla
        gluLookAt(0, -30, 0, 0, 0, 0, 0, 0, -1);
// nad scenou, 0 0 Z20
        glBegin(GL_TRIANGLE_FAN);
        glColor3f(1.0f, 1.0f, 1.0f);
        glVertex3f(5.0f, 5.0f, 10.0f);
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(10.0f, 0.0f, 0.0f);
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(10.0f, 10.0f, 0.0f);
        glColor3f(1.0f, 1.0f, 0.0f);
        glVertex3f(0.0f, 10.0f, 0.0f);
        glEnd();

        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();

        glBegin(GL_LINES);
        glColor3f(1f, 0f, 0f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(100f, 0f, 0f);
        glColor3f(0f, 1f, 0f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(0f, 100f, 0f);
        glColor3f(0f, 0f, 1f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(0f, 0f, 100f);
        glEnd();

        float[] color = {1.0f, 1.0f, 1.0f};
        glColor3fv(color);
        glDisable(GL_DEPTH_TEST);
    }

}
