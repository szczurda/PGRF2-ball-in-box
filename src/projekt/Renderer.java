package projekt;

import lvl0fixpipeline.global.AbstractRenderer;
import lvl0fixpipeline.global.GLCamera;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;
import projekt.math.Vec3f;

import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import static lvl0fixpipeline.global.GluUtils.gluPerspective;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


public class Renderer extends AbstractRenderer {
    private double dx, dy;
    private double ox, oy;
    private boolean per = false, depth = true, move = false;
    private double ex, ey, ez;
    private float uhel = 0;
    private boolean mouseButton1 = false;
    private float zenit, azimut;
    private float trans, deltaTrans = 0.002f;
    private float[] modelMatrix = new float[16];
    private GLCamera camera;
    private Cube cube;
    private Ball ball;

    private ArrayList<Ball> balls = new ArrayList<>();

    private final float GRAVITY = -9.81f;
    private long lastTime;
    private float[] deltaTimeBuffer = new float[100];
    private int nextIndex = 0;
    private float increase = 1;

    public Renderer() throws LineUnavailableException {

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);

                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_P:
                            per = !per;
                            break;
                        case GLFW_KEY_M:
                            move = !move;
                            break;
                        case GLFW_KEY_LEFT_SHIFT:
                        case GLFW_KEY_LEFT_CONTROL:
                        case GLFW_KEY_W:
                        case GLFW_KEY_S:
                        case GLFW_KEY_A:
                        case GLFW_KEY_D:
                            deltaTrans = 0.001f;
                            break;
                        case GLFW_KEY_SPACE:
                            for (Ball ball : balls) {
                                glPushMatrix();
                                ball.setVelocity(new Vec3f((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50));
                                glPopMatrix();
                            }
                            break;
                        case GLFW_KEY_B:
                            increase += 0.1f;
                            Ball newBall = new Ball(increase, increase, 1f);
                            balls.add(newBall);

                    }
                }
                switch (key) {
                    case GLFW_KEY_W -> camera.forward(trans);
                    case GLFW_KEY_S -> camera.backward(trans);
                    case GLFW_KEY_A -> camera.left(trans);
                    case GLFW_KEY_D -> camera.right(trans);
                    case GLFW_KEY_LEFT_SHIFT -> camera.up(trans);
                    case GLFW_KEY_LEFT_CONTROL -> camera.down(trans);
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
                    ox = (float) x;
                    oy = (float) y;
                }
            }

        };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    dx = (float) x - ox;
                    dy = (float) y - oy;
                    ox = (float) x;
                    oy = (float) y;
                    zenit -= dy / width * 180;
                    if (zenit > 90)
                        zenit = 90;
                    if (zenit <= -90)
                        zenit = -90;
                    azimut += dx / height * 180;
                    azimut = azimut % 360;
                    camera.setAzimuth(Math.toRadians(azimut));
                    camera.setZenith(Math.toRadians(zenit));
                    dx = 0;
                    dy = 0;
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
        lastTime = System.nanoTime();
        GL.createCapabilities();
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);
        camera = new GLCamera();
        cube = new Cube();
        ball = new Ball();
        balls.add(ball);
        ControlPanel controlPanel = new ControlPanel(0, 0, new Dimension(width, height), balls, cube);
        controlPanel.setVisible(true);
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);
        // zapnuti nebo vypnuti viditelnosti
        if (depth)
            glEnable(GL_DEPTH_TEST);
        else
            glDisable(GL_DEPTH_TEST);

        trans += deltaTrans;

        double a_rad = azimut * Math.PI / 180;
        double z_rad = zenit * Math.PI / 180;
        ex = Math.sin(a_rad) * Math.cos(z_rad);
        ey = Math.sin(z_rad);
        ez = -Math.cos(a_rad) * Math.cos(z_rad);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        camera.setFirstPerson(false);
        camera.setRadius(30);
        camera.setMatrix();
        glMatrixMode(GL_PROJECTION);

        glLoadIdentity();
        if (!per)
            gluPerspective(45, width / (float) height, 0.1f, 200.0f);
        else
            glOrtho(-20 * width / (float) height,
                    20 * width / (float) height,
                    -20, 20, 0.1f, 200.0f);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        drawScene();
        glPopMatrix();
    }

    public void drawScene() {
        // Clear the buffers and enable depth testing
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glPushMatrix();
        cube.draw();
        glPopMatrix();
        // Draw the cube


        // Calculate delta time and store it in a buffer
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastTime) / 1000000000.0f;
        deltaTimeBuffer[nextIndex] = deltaTime;
        nextIndex = (nextIndex + 1) % deltaTimeBuffer.length;

        // Calculate smoothed delta time by averaging the buffer
        float deltaTimeSmoothed = 0.0f;
        for (int i = 0; i < deltaTimeBuffer.length; i++) {
            deltaTimeSmoothed += deltaTimeBuffer[i];
        }
        deltaTimeSmoothed /= deltaTimeBuffer.length;
        for (Ball ball : balls) {
            ArrayList<Ball> otherBalls = new ArrayList<>();
            for (Ball ball1 : balls) {
                if (ball1 != ball) {
                    otherBalls.add(ball1);
                }
            }


            ball.collisionCheck(cube, otherBalls);
            ball.update(deltaTimeSmoothed);
            glPushMatrix();
            glTranslatef(ball.getPosition().x, ball.getPosition().y, ball.getPosition().z);
            ball.draw();
            glPopMatrix();
        }

        glDisable(GL_DEPTH_TEST);
        lastTime = currentTime;
    }
}

