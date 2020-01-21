package com.bennyplo.openglpipeline;

import android.opengl.GLES32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Pyramid {
    private final String vertexShaderCode =
            "attribute vec3 aVertexPosition;"+"uniform mat4 uMVPMatrix;varying vec4 vColor;" +
                    "void main() {gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);" +
                    "gl_PointSize = 40.0;"+
                    "vColor=vec4(1.0,0.0,0.0,1.0);}";
    private final String fragmentShaderCode = "precision mediump float;varying vec4 vColor; "+
            "void main() {gl_FragColor = vColor;}";
    float radious=.5f;
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private int vertexCount;// number of vertices
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    static float pyramidVertex[] = {
            // front face
            0.0f,1.0f,0.0f,
            -1f,-1f,-1f,
            1f,-1f,1f,
            // right face
            0f,1f,0f,
            1f,-1f,1f,
            1f,-1f,-1f,
            // back face
            0f,1f,0f,
            1f,-1f,-1f,
            -1f,-1f,-1f,
            // left face
            0f,1f,0f,
            -1f,-1f,-1f,
            -1f,-1f,1f
    };
    public Pyramid(){
        ByteBuffer bb = ByteBuffer.allocateDirect(pyramidVertex.length*4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(pyramidVertex);
        vertexBuffer.position(0);
        vertexCount = pyramidVertex.length/COORDS_PER_VERTEX;
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
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle);
    }

    public void draw(float[] mvpMatrix) {
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyRenderer.checkGlError("glUniformMatrix4fv");
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES32.GL_FLOAT, false, vertexStride, vertexBuffer);
    }
}
