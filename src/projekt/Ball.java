package projekt;
import projekt.math.Vec3f;

import javax.vecmath.Vector3d;
import java.util.ArrayList;

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
    int gradation = 10;
    float dampingFactor = 0.99f;
    private int collNum = 0;

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
    // Add gravity to the accelerationw
        Vec3f gravity = new Vec3f(0, mass * GRAVITY, 0);
        acceleration.add(gravity);

        // Scale the acceleration by the elapsed time
        Vec3f accelerationScaled = new Vec3f(acceleration.x * deltaTime, acceleration.y * deltaTime, acceleration.z * deltaTime);

        // Update the velocity by adding the scaled acceleration
        velocity.add(accelerationScaled);
        // Update the position by adding the scaled velocity

        Vec3f velocityScaled = new Vec3f(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);
        position.add(velocityScaled);

        // Apply damping to the velocity to simulate air resistance

        velocity.scale(dampingFactor);

        // Reset the acceleration to zero after each update
        acceleration.set(0, 0, 0);
    }

    public void draw(){
        float x, y, z, alpha, beta; // Storage for coordinates and angles
        for (alpha = 0.0f; alpha < PI; alpha += PI / gradation) {
            glBegin(GL_TRIANGLE_STRIP);
            glColor3f(alpha / PI, (PI - alpha) / PI, 1.0f);
            for (beta = 0.0f; beta < 2.01 * PI; beta += PI / gradation) {
                x = (float) (radius * Math.cos(beta) * Math.sin(alpha));
                y = (float) (radius * Math.sin(beta) * Math.sin(alpha));
                z = (float) (radius * Math.cos(alpha));
                glVertex3f(x, y, z);
                x = (float) (radius * Math.cos(beta) * Math.sin(alpha + PI / gradation));
                y = (float) (radius * Math.sin(beta) * Math.sin(alpha + PI / gradation));
                z = (float) (radius * Math.cos(alpha + PI / gradation));
                glVertex3f(x, y, z);
            }
            glEnd();
        }
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

    public Vec3f getVelocity(){
        return velocity;
    }

    public void setVelocity(Vec3f velocity) {
        this.velocity = velocity;
    }

    public Vec3f getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vec3f acceleration) {
        this.acceleration = acceleration;
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

    public void collisionCheck(Cube cube, ArrayList<Ball> otherBalls){
        float ballPositiveX = position.x + radius;
        float ballNegativeX = position.x - radius;
        float ballPositiveY = position.y + radius;
        float ballNegativeY = position.y - radius;
        float ballPositiveZ = position.z + radius;
        float ballNegativeZ = position.z - radius;

        if(ballNegativeY <= -cube.getYBounds()){
            setVelocity(new Vec3f(velocity.x, -velocity.y  * corConstant , velocity.z));
            position.y = -cube.getYBounds() + radius + 0.000001f;
            if(velocity.y / getMass() >= 0.4f){


            }
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
    public void handleCollision(Ball other) {
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
}