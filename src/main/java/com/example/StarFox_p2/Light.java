package com.example.StarFox_p2;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Light {
    GL10 gl;
    int id;
    FloatBuffer index;

    public Light(GL10 gl, int id) {
        this.gl = gl;
        this.id = id;
        gl.glEnable(id);
    }
    public void setPosition(float[] i) {
        index = FloatBuffer.wrap(i);
        gl.glLightfv(id, GL10.GL_POSITION, index);
    }
    public void setDiffuseColor(float[] color) {
        FloatBuffer aux = FloatBuffer.wrap(color);
        gl.glLightfv(id, GL10.GL_DIFFUSE, aux);
    }
}