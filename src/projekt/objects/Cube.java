package projekt.objects;

import org.lwjgl.BufferUtils;
import projekt.math.Vec3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;


public class Cube {

    private float x = 5.0f, y = 5.0f, z = 5.0f;

    private final float dx = 5.0f;
    private final float dy = 5.0f;
    private final float dz = 5.0f;

    private Vec3f originalScale = new Vec3f(1.0f, 1.0f, 1.0f);
    private float[] vertices = {
            -x, -y, z,
            x, -y, z,
            x, y, z,
            -x, y, z,

            -x, -y, -z,
            x, -y, -z,
            x, y, -z,
            -x, y, -z,

            x, y, z,
            -x, y, z,
            -x, y, -z,
            x, y, -z,

            -x, -y, z,
            x, -y, z,
            x, -y, -z,
            -x, -y, -z,

            x, -y, z,
            x, y, z,
            x, y, -z,
            x, -y, -z,

            -x, -y, z,
            -x, y, z,
            -x, y, -z,
            -x, -y, -z,
    };

    private final int[] indices = {
            0, 1,
            1, 2,
            2, 3,
            3, 0,    // Front face

            4, 5,
            5, 6,
            6, 7,
            7, 4,    // Back face

            0, 3,
            3, 7,
            7, 4,
            4, 0,    // Left face

            1, 2,
            2, 6,
            6, 5,
            5, 1,    // Right face

            0, 1,
            1, 5,
            5, 4,
            4, 0,    // Bottom face

            3, 2,
            2, 6,
            6, 7,
            7, 3,    // Top face
    };

    float[] colors = {
            1.0f, 0.0f, 0.0f, // red
            0.0f, 1.0f, 0.0f, // green
            0.0f, 0.0f, 1.0f, // blue
            1.0f, 1.0f, 1.0f, // white
            1.0f, 0.0f, 1.0f, // magenta
            0.0f, 1.0f, 1.0f,  // cyan
            1.0f, 0.0f, 0.0f, // red
            0.0f, 1.0f, 0.0f, // green
            0.0f, 0.0f, 1.0f, // blue
            1.0f, 1.0f, 1.0f, // white
            1.0f, 0.0f, 1.0f, // magenta
            0.0f, 1.0f, 1.0f,  // cyan
    };

    private final float[] defaultVertices = vertices;

    private final int vbo = glGenBuffers(); // Vertex Buffer Object
    private final int ibo = glGenBuffers(); // Index Buffer Object

    private final int cbo = glGenBuffers();

    private final IntBuffer indicesBuffer;
    private final FloatBuffer verticesBuffer;
    private final FloatBuffer colorBuffer;

    public Cube() {
        //VB
        verticesBuffer = BufferUtils.createFloatBuffer(vertices.length); // Vytvoříme V-buffer
        verticesBuffer.put(vertices); // Nacpeme do něj naše body
        verticesBuffer.flip(); // flipneme hodnoty
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        int stride = 3 * 4;
        int positionAttribute = 0;
        glVertexAttribPointer(positionAttribute, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(positionAttribute);

        //IB
        indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        //CB
        glEnableClientState(GL_COLOR_ARRAY);
        colorBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorBuffer.put(colors);
        colorBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, cbo);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glColorPointer(3, GL_FLOAT, 0, 0);
    }

    public void draw() {
        glDrawElements(GL_LINE_LOOP, indices.length, GL_UNSIGNED_INT, 0);
    }

    public void scale(float scaleValue){
        Vec3f currentScale;
        if(scaleValue < 0){
            currentScale = originalScale.divide(-scaleValue);
        } else {
            currentScale = originalScale.mul(scaleValue);
        }
        x = x * currentScale.x;
        y = y * currentScale.y;
        z = z * currentScale.z;
        float[] scaledVertices = new float[vertices.length];
        for (int i = 0; i < scaledVertices.length; i += 3) {
            scaledVertices[i] = vertices[i] * currentScale.x;
            scaledVertices[i + 1] = vertices[i + 1] * currentScale.y;
            scaledVertices[i + 2] = vertices[i + 2] * currentScale.z;
        }
        this.vertices = scaledVertices;// Vytvoříme V-buffer
        verticesBuffer.put(scaledVertices); // Nacpeme do něj naše body
        verticesBuffer.flip(); // flipneme hodnoty
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
    }

    public void defaultScale(){
        this.x = dx;
        this.y = dy;
        this.z = dz;
        this.vertices = defaultVertices;// Vytvoříme V-buffer
        verticesBuffer.put(defaultVertices); // Nacpeme do něj naše body
        verticesBuffer.flip(); // flipneme hodnoty
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
    }



    public float getXBounds() {
        return x;
    }

    public float getYBounds() {
        return y;
    }

    public float getZBounds() {
        return z;
    }

    public Vec3f getOriginalScale() {
        return originalScale;
    }

    public void setOriginalScale(Vec3f originalScale) {
        this.originalScale = originalScale;
    }
}
