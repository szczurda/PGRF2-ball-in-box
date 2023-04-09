package projekt;
import org.lwjgl.opengl.GL11;

import org.lwjgl.*;

import projekt.math.Vec3f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;


public class Cube {

    private float x = 5.0f, y = 5.0f, z = 5.0f;
    private float[] vertices = {
            -x, -y,  z,
             x, -y,  z,
             x,  y,  z,
            -x,  y,  z,

            -x, -y, -z,
             x, -y, -z,
             x,  y, -z,
            -x,  y, -z,

             x,  y,  z,
            -x,  y,  z,
            -x,  y, -z,
             x,  y, -z,

            -x, -y,  z,
             x, -y,  z,
             x, -y, -z,
            -x, -y, -z,

             x, -y,  z,
             x,  y,  z,
             x,  y, -z,
             x, -y, -z,

            -x, -y,  z,
            -x,  y,  z,
            -x,  y, -z,
            -x, -y, -z,
    };

    private int[] indices = {
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


    private int vbo = glGenBuffers(); // Vertex Buffer Object
    private int ibo = glGenBuffers(); // Index Buffer Object

    private int cbo = glGenBuffers();

    private IntBuffer indicesBuffer;
    private FloatBuffer verticesBuffer;
    private FloatBuffer colorBuffer;

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

    public void draw(){
        glDrawElements(GL_LINE_LOOP, indices.length, GL_UNSIGNED_INT, 0);
    }


    public void scale(Vec3f scaleVector){
        float[] scaledVertices = new float[vertices.length];
        x = x * scaleVector.x;
        y = y * scaleVector.y;
        z = z * scaleVector.z;
        for(int i = 0; i < scaledVertices.length; i += 3){
            scaledVertices[i] = vertices[i] * scaleVector.x;
            scaledVertices[i + 1] = vertices[i + 1] * scaleVector.y;
            scaledVertices[i + 2] = vertices[i + 2] * scaleVector.z;
        }
        this.vertices = scaledVertices;// Vytvoříme V-buffer
        verticesBuffer.put(scaledVertices); // Nacpeme do něj naše body
        verticesBuffer.flip(); // flipneme hodnoty
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
    }

    public float getXBounds(){
        return x;
    }

    public float getYBounds(){
        return y;
    }

    public float getZBounds(){
        return z;
    }





}
