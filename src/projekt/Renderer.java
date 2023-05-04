package projekt;

import lvl0fixpipeline.global.AbstractRenderer;
import lvl0fixpipeline.global.GLCamera;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFWVidMode;
import projekt.objects.Ball;
import projekt.objects.Cube;
import transforms.Vec3D;

import javax.sound.sampled.LineUnavailableException;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.nio.file.*;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static lvl0fixpipeline.global.GluUtils.gluPerspective;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


public class Renderer extends AbstractRenderer {
    private double dx, dy;
    private double ox, oy;
    private boolean per = false;
    private final boolean depth = true;
    private boolean mouseButton1 = false;
    private float zenit, azimut;
    private float trans, deltaTrans = 0.001f;
    private GLCamera camera;
    private Cube cube;
    private Ball ball;
    private float cubeScale = 1.0f;
    private final CopyOnWriteArrayList<Ball> balls = new CopyOnWriteArrayList<>();
    private long lastTime;
    private final float[] deltaTimeBuffer = new float[100];
    private int nextIndex = 0;
    private float increase = 1;
    private float oldCubeScale;
    private ControlPanel controlPanel;
    int windowPosX;
    int windowPosY;
    GLFWVidMode vidmode;

    private String currTexture = "";

    private ArrayList<String> textureStringList = new ArrayList<>();

    private int textureListIndex = 0;

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
                                ball.giveBallRandomVelocity();
                            }
                            break;
                        case GLFW_KEY_B:
                            increase += 0.1f;
                            Ball newBall = new Ball(increase, increase, 1f);
                            if (currTexture != "") {
                                newBall.setTextureOn();
                                newBall.setTexture(currTexture);
                            }
                            balls.add(newBall);
                            break;
                        case GLFW_KEY_R:
                            resetScene();
                            break;
                        case GLFW_KEY_T:
                            System.out.println(textureListIndex + " / " +textureStringList.size());
                            if (textureListIndex < textureStringList.size()) {
                                currTexture = textureStringList.get(textureListIndex);
                                controlPanel.setCurrTextureLabel(currTexture);
                                for (Ball ball : balls) {
                                    ball.setTextureOn();
                                    ball.setTexture(currTexture);
                                }
                                textureListIndex++;
                            } else {
                                textureListIndex = 0;
                                String currTexture = textureStringList.get(textureListIndex);
                                ball.setTexture(currTexture);
                            }
                            break;
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

        glfwWindowCloseCallback = new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                controlPanel.dispose();
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
                    if (zenit > 90) zenit = 90;
                    if (zenit <= -90) zenit = -90;
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

            }
        };
        glfwWindowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                controlPanel.toFront();
            }
        };
    }

    @Override
    public void init() {
        createTextureFile();
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
        vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        windowPosX = (vidmode.width() - 1280) / 2;

        windowPosY = (vidmode.height() - 720) / 2;

        controlPanel = new ControlPanel(balls, this, cube);
        controlPanel.setVisible(true);
    }

    @Override
    public void display() {
        if (reset) {
            reset = false;
            resetScene();
        }
        glViewport(0, 0, width, height);
        if (depth) glEnable(GL_DEPTH_TEST);
        else glDisable(GL_DEPTH_TEST);

        trans += deltaTrans;

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        camera.setFirstPerson(false);
        camera.setRadius(30);
        camera.setMatrix();
        glMatrixMode(GL_PROJECTION);

        glLoadIdentity();
        if (!per) gluPerspective(45, width / (float) height, 0.1f, 200.0f);
        else glOrtho(-20 * width / (float) height, 20 * width / (float) height, -20, 20, 0.1f, 200.0f);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        drawScene();
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
    }

    public void drawScene() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glPushMatrix();
        if (cubeScale > oldCubeScale) {
            cube.scale(cubeScale);
        } else if (cubeScale < oldCubeScale) {
            if (cubeScale <= 1.0) {
                cube.defaultScale();
            } else cube.scale(-cubeScale);
        }
        cube.draw();
        glPopMatrix();
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastTime) / 1000000000.0f;
        deltaTimeBuffer[nextIndex] = deltaTime;
        nextIndex = (nextIndex + 1) % deltaTimeBuffer.length;

        float deltaTimeSmoothed = 0.0f;
        for (int i = 0; i < deltaTimeBuffer.length; i++) {
            deltaTimeSmoothed += deltaTimeBuffer[i];
        }
        deltaTimeSmoothed /= deltaTimeBuffer.length;
        for (Ball ball : balls) {
            CopyOnWriteArrayList<Ball> otherBalls = new CopyOnWriteArrayList<>();
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
        oldCubeScale = cubeScale;
    }

    public float getCubeScale() {
        return cubeScale;
    }


    public void setCubeScale(float value) {
        this.cubeScale = value;
    }

    public int getWindowPosX() {
        return windowPosX;
    }

    public void setWindowPosX(int windowPosX) {
        this.windowPosX = windowPosX;
    }

    public int getWindowPosY() {
        return windowPosY;
    }

    public void setWindowPosY(int windowPosY) {
        this.windowPosY = windowPosY;
    }

    public void resetScene() {
        cubeScale = 1.0f;
        for (Ball ball : balls) {
            deleteTextures(ball.getTextureIDList());
        }
        balls.clear();
        setCurrTexture("");
        Ball defaultBall = new Ball();
        defaultBall.setTextureOff();
        balls.add(defaultBall);
        zenit = 0;
        azimut = 0;
        camera.setPosition(new Vec3D(0, 0, 0));
        camera.setAzimuth(zenit);
        camera.setZenith((azimut));
        ox = 0;
        oy = 0;
        dx = 0;
        dy = 0;
        controlPanel.getCubeSizeSlider().setValue(1);
        controlPanel.getBallRadiusSlider().setValue(10);
        controlPanel.getBallCorSlider().setValue(10);
        controlPanel.getBallMassSlider().setValue(1);
        reset = false;
    }


    private void createTextureFile() {
        URI uri = null;
        try {
            uri = getClass().getResource("/projekt/textures").toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = null;
            try {
                fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            myPath = fileSystem.getPath("/projekt/textures");
        } else {
            myPath = Paths.get(uri);
        }
        Stream<Path> walk = null;
        try {
            walk = Files.walk(myPath, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Iterator<Path> it = walk.iterator();

            while(it.hasNext()){
                String nextLine = it.next().toString();
                String output;
                if(nextLine.contains("\\")) {
                    output = nextLine.substring(nextLine.lastIndexOf('\\') + 1);
                } else {
                    output = nextLine.substring(nextLine.lastIndexOf("/") + 1);
                }
                if(output.endsWith(".jpg") || output.endsWith(".jpeg")){
                    textureStringList.add(output);
                }
            }
    }


    public String getCurrTexture() {
        return currTexture;
    }

    public void setCurrTexture(String currTexture) {
        this.currTexture = currTexture;
    }

    private void deleteTextures(CopyOnWriteArrayList<Integer> listOfIDs) {
        for (Integer id : listOfIDs) {
            glDeleteTextures(id);
            listOfIDs.remove(id);
        }
    }

    private boolean reset = false;

    public void setReset(boolean reset) {
        this.reset = reset;
    }
}

