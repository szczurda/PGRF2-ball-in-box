package projekt.objects;
import projekt.math.Vec3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Ball {
    private Vec3f position;

    private Vec3f velocity; // Rychlose (Změna polohy v čase)

    private Vec3f acceleration; //akcelerace (Změna rychlosti v čase)
    private final float PI = 3.141592f;
    private final float GRAVITY = -9.81f;
    private float mass;
    private float radius;
    private float corConstant;
    int gradation = 20;
    float dampingFactor = 0.99f;
    private boolean textureOn = false;
    private int texID;

    private CopyOnWriteArrayList<Integer> textureIDList = new CopyOnWriteArrayList<>();

    public Ball() { //default
        this.position = new Vec3f(0, 0, 0); // default position is (0, 0, 0)
        this.velocity = new Vec3f(0, 0, 0); // default velocity is (0, 0, 0)
        this.acceleration = new Vec3f(0, GRAVITY, 0); // default acceleration is (0, -9.81, 0) (gravity)
        this.mass = 1f;
        this.radius = 1f;
        this.corConstant = 1f;
    }

    public Ball(float mass, float radius, float corConstant) {
        this.position = new Vec3f(0, 0, 0); // default position is (0, 0, 0)
        this.velocity = new Vec3f(0, 0, 0); // default velocity is (0, 0, 0)
        this.acceleration = new Vec3f(0, GRAVITY, 0); // default acceleration is (0, -9.81, 0) (gravity)
        this.mass = mass;
        this.radius = radius;
        this.corConstant = corConstant;
    }

    public void update(float deltaTime) {
        Vec3f gravity = new Vec3f(0, mass * GRAVITY, 0);
        acceleration.add(gravity);
        Vec3f accelerationScaled = new Vec3f(acceleration.x * deltaTime, acceleration.y * deltaTime, acceleration.z * deltaTime);
        velocity.add(accelerationScaled);
        Vec3f velocityScaled = new Vec3f(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);
        position.add(velocityScaled);
        velocity.scale(dampingFactor);
        acceleration.set(0, 0, 0);
    }

    public void draw() {
        glEnable(GL_TEXTURE_2D);
        float x, y, z, alpha, beta;
        for (alpha = 0.0f; alpha < PI; alpha += PI / gradation) {
            glBegin(GL_TRIANGLE_STRIP);
            if(!textureOn){
                glColor3f(alpha / PI, (PI - alpha) / PI, 1.0f);
            } else {
                glColor3f(1.0f, 1.0f, 1.0f);
            }
            for (beta = 0.0f; beta < 2.01 * PI; beta += PI / gradation) {
                x = (float) (radius * Math.cos(beta) * Math.sin(alpha));
                y = (float) (radius * Math.sin(beta) * Math.sin(alpha));
                z = (float) (radius * Math.cos(alpha));
                glTexCoord2f(beta / (2.0f * PI), alpha / PI);
                glVertex3f(x, y, z);
                x = (float) (radius * Math.cos(beta) * Math.sin(alpha + PI / gradation));
                y = (float) (radius * Math.sin(beta) * Math.sin(alpha + PI / gradation));
                z = (float) (radius * Math.cos(alpha + PI / gradation));
                glTexCoord2f(beta / (2.0f * PI), alpha / PI + 1.0f / gradation);
                glVertex3f(x, y, z);
            }
            glEnd();
        }
        glDisable(GL_TEXTURE_2D);
    }



    public float getRadius(){
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vec3f getPosition(){
        return position;
    }

    public void setPosition(Vec3f position) {
        this.position = position;
    }

    public void setVelocity(Vec3f velocity) {
        this.velocity = velocity;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getCorConstant() {
        return corConstant;
    }

    public void setCorConstant(float corConstant) {
        this.corConstant = corConstant;
    }

    public void collisionCheck(Cube cube, CopyOnWriteArrayList<Ball> otherBalls){
        float ballPositiveX = position.x + radius;
        float ballNegativeX = position.x - radius;
        float ballPositiveY = position.y + radius;
        float ballNegativeY = position.y - radius;
        float ballPositiveZ = position.z + radius;
        float ballNegativeZ = position.z - radius;

        if(ballNegativeY <= -cube.getYBounds()){
            setVelocity(new Vec3f(velocity.x, -velocity.y  * corConstant , velocity.z));
            position.y = -cube.getYBounds() + radius + 0.000001f;
        }
        if(ballPositiveY >= cube.getYBounds()){
            setVelocity(new Vec3f(velocity.x, -velocity.y  * corConstant, velocity.z));
            position.y = cube.getYBounds() - radius + 0.001f;

        }
        if(ballPositiveX >= cube.getXBounds()){
            setVelocity(new Vec3f(-velocity.x  * corConstant, velocity.y,  velocity.z));
            position.x = cube.getXBounds() - radius + 0.001f;


        }
        if(ballNegativeX <= -cube.getXBounds()){
            setVelocity(new Vec3f(-velocity.x  * corConstant, velocity.y,  velocity.z));
            position.x = -cube.getXBounds() + radius + 0.001f;


        }
        if(ballPositiveZ >= cube.getZBounds()){
            setVelocity(new Vec3f(velocity.x, velocity.y,  -velocity.z * corConstant));
            position.z = cube.getZBounds() - radius + 0.001f;


        }
        if(ballNegativeZ <= -cube.getZBounds()){
            setVelocity(new Vec3f(velocity.x, velocity.y,  -velocity.z  * corConstant));
            position.z = -cube.getZBounds() + radius + 0.001f;
        }

        /* ---------------------------------------------------------------------- */

        for(Ball otherBall: otherBalls){
            handleCollision(otherBall);
        }

    }
    public void handleCollision(Ball other){
        Vec3f delta = other.position.sub(position); // Rozdíl vektorů pozic obou míčků
        float distanceSquared = delta.dot(delta); // Vzdálenost na druhou
        float radiusSquared = (radius + other.radius) * (radius + other.radius); // Součet poloměrů na druhou

        if (distanceSquared <= radiusSquared) { //Euklidovská vzdálenost
            Vec3f relativeVelocity = other.velocity.sub(velocity);
            Vec3f normDelta = delta.normalized();
            float impulse = (1 + corConstant) * (relativeVelocity.dot(normDelta)) / (1.0f / mass + 1.0f / other.mass);
            Vec3f collisionImpulse = normDelta.mul(impulse);

            float overlapDistance = (radius + other.radius) - (float) Math.sqrt(distanceSquared);
            Vec3f correction = normDelta.mul(overlapDistance * 0.5f);
            position = position.sub(correction.divide(mass));
            other.position.add(correction.divide(other.mass));
            velocity.add(collisionImpulse.divide(mass));
            other.setVelocity(other.velocity.sub(collisionImpulse.divide(other.mass)));
        }
    }

    public void giveBallRandomVelocity() {
        glPushMatrix();
        setVelocity(new Vec3f((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50));
        glPopMatrix();
    }

    private int loadTexture(String filename) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/projekt/textures/" + filename);
        BufferedImage image = ImageIO.read(inputStream);
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);
        for (int y = image.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
                buffer.put((byte) (pixel & 0xFF));         // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
            }
        }
        buffer.flip();
        int textureID = glGenTextures();
        textureIDList.add(textureID);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        return textureID;
    }


    public void setTexture(String textureString) {
        try {
            if(textureOn){
                texID = loadTexture(textureString);
            } else{
                return;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTextureOn() {
        return textureOn;
    }

    public void setTextureOn() {
        if(!textureOn){
            textureOn = true;
        }
    }

    public void setTextureOff(){
        if(textureOn){
            textureOn = false;
        }
    }
    public CopyOnWriteArrayList<Integer> getTextureIDList() {
        return textureIDList;
    }

    public void setTextureIDList(CopyOnWriteArrayList<Integer> textureIDList) {
        this.textureIDList = textureIDList;
    }
}
