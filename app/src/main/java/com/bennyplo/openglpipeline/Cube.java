package com.bennyplo.openglpipeline;

import android.opengl.GLES32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Cube {
    private final String vertexShaderCode =
            "attribute vec3 aVertexPosition;"+
                    "uniform mat4 uMVPMatrix;varying vec4 vColor;" +
                    "attribute vec4 aVertexColor;"+//color of each vertex.//
                    "void main() {gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);" +
                    "gl_PointSize = 40.0;"+
                    "vColor=aVertexColor;}";
    private final String fragmentShaderCode = "precision mediump float;varying vec4 vColor; "+
            "void main() {gl_FragColor = vColor;}";
    float radious=.5f;
    private final FloatBuffer vertexBuffer,colorBuffer;
    private final IntBuffer indexBuffer;
    private final int mProgram;
    private int mPositionHandle,mColorHandle;
    private int mMVPMatrixHandle;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int COLOR_PER_VERTEX = 4;
    private int vertexCount;// number of vertices
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int colorStride = COLOR_PER_VERTEX * 4; // 4 bytes per vertex
    static float cubeVertex[] = {
            // front face
            -1f,-1.0f,1.0f,
            1f,-1.0f,1.0f,
            1f,1f,1f,
            -1f,1.0f,1.0f,
            // right face
            -1f,-1.0f,-1.0f,
            -1f,1.0f,-1.0f,
            1f,1f,-1f,
            1f,-1f,-1f,
            // back face
            -1f,1.0f,-1.0f,
            -1f,1.0f,1.0f,
            1f,1f,1f,
            1f,1f,-1f,
            // left face
            -1f,-1.0f,-1.0f,
            1f,-1.0f,-1.0f,
            1f,-1.0f,1.0f,
            -1f,-1.0f,1.0f,
            // back 1
            1f,-1.0f,-1.0f,
            1f,1.0f,-1.0f,
            1f,1.0f,1.0f,
            1f,-1.0f,1.0f,
            //back 2
            -1f,-1.0f,-1.0f,
            -1f,-1.0f,1.0f,
            -1f,1.0f,1.0f,
            -1f,1.0f,-1.0f,
    };

    static int cubeIndex[] = {
            0,1,2,0,2,3,
            4,5,6,4,6,7,
            8,9,10,8,10,11,
            12,13,14,12,14,15,
            16,17,18,16,18,19,
            20,21,22,20,22,23
    };
    static float CubeColor[]={
            // front face
            0f,0f,1f,1f,
            0f,0f,1f,1f,
            0f,0f,1f,1f,
            0f,0f,1f,1f,
            // right face
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            // back face
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            // left face
            0f,1f,1f,1f,
            0f,1f,1f,1f,
            0f,1f,1f,1f,
            0f,1f,1f,1f,
            // bottom
            1f,1f,0f,1f,
            1f,1f,0f,1f,
            1f,1f,0f,1f,
            1f,1f,0f,1f,
            // bottom 1
            1f,0f,1f,0f,
            1f,0f,1f,0f,
            1f,0f,1f,0f,
            1f,0f,1f,0f,
    };

    public Cube(){
        ByteBuffer bb = ByteBuffer.allocateDirect(cubeVertex.length*4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeVertex);
        vertexBuffer.position(0);
        vertexCount = cubeVertex.length/COORDS_PER_VERTEX;
        ByteBuffer cb =  ByteBuffer.allocateDirect(CubeColor.length*4);
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(CubeColor);
        colorBuffer.position(0);

        IntBuffer ib = IntBuffer.allocate(cubeIndex.length);
        indexBuffer = ib;
        indexBuffer.put(cubeIndex);
        indexBuffer.position(0);
        cb.order(ByteOrder.nativeOrder());
        // prepare shaders and OpenGL program
        int vertexShader = MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES32.glCreateProgram();             // create empty OpenGL Program
        GLES32.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES32.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES32.glLinkProgram(mProgram);                  // link the  OpenGL program to create an executable
        GLES32.glUseProgram(mProgram);// Add program to OpenGL environment
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "aVertexPosition");
        mColorHandle =  GLES32.glGetAttribLocation(mProgram, "aVertexColor");
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle);
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mColorHandle);
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram,"uMVPMatrix");
        MyRenderer.checkGlError("glGetUniformLocation");
    }

    public void draw(float[] mvpMatrix) {
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyRenderer.checkGlError("glUniformMatrix4fv");
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES32.GL_FLOAT, false, vertexStride, vertexBuffer);

        GLES32.glVertexAttribPointer(mColorHandle, COLOR_PER_VERTEX,
                GLES32.GL_FLOAT, false, colorStride, colorBuffer);

        GLES32.glDrawElements(GLES32.GL_TRIANGLES,cubeIndex.length,GLES32.GL_UNSIGNED_INT,indexBuffer);

    }


}
