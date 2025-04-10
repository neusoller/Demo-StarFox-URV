package com.example.StarFox_p2;

import android.opengl.GLU;
import javax.microedition.khronos.opengles.GL10;

public class Camera {
    GL10 gl;
    Vector4 center, aux, up;

    public Camera(GL10 gl, Vector4 aux, Vector4 center, Vector4 up) {
        this.gl = gl;
        this.center = center;
        this.aux = aux;
        this.up = up;
    }

    public void newMov(Vector4 x) {
        this.center = this.center.add(x);
        this.aux = this.aux.add(x);
    }
    public void gluLookAt() {
        GLU.gluLookAt(gl, aux.get(0), aux.get(1), aux.get(2),
                center.get(0), center.get(1), center.get(2),
                up.get(0), up.get(1), up.get(2));
    }

    // ---------- MOVIMENTS DE LA CAMERA ----------
    // esquerra
    public void mvLeft(float distance) {
        Vector4 left = up.cross3(center.subtract(aux));
        newMov(left.normalize().mult(distance));
    }
    // dreta
    public void mvRight(float distance) {
        Vector4 right = center.subtract(aux).cross3(up);
        newMov(right.normalize().mult(distance));
    }
    // endavant
    public void mvEndavant(float distance) {
        Vector4 forward = center.subtract(aux).normalize().mult(distance);
        newMov(forward);
    }
    // enrere
    public void mvEnrere (float distance) {
        Vector4 backward = aux.subtract(center).normalize();
        newMov(backward.mult(distance));
    }
    // amunt
    public void mvAmunt(float distance) {
        Vector4 upMovement = up.mult(distance);
        newMov(upMovement);
    }
    // avall
    public void mvAvall(float distance) {
        Vector4 downMovement = up.mult(-distance);
        newMov(downMovement);
    }

    // ---------- ROTACIONS DE LA CAMERA ----------
    // rotació vertical
    public void rotV(float angle) {
        Vector4 direction = center.subtract(aux);
        float cosAngle = (float) Math.cos(Math.toRadians(angle));
        float sinAngle = (float) Math.sin(Math.toRadians(angle));

        float newY = direction.get(1) * cosAngle - direction.get(2) * sinAngle;
        float newZ = direction.get(1) * sinAngle + direction.get(2) * cosAngle;

        center = new Vector4(direction.get(0) + aux.get(0), newY + aux.get(1), newZ + aux.get(2), 1.0f);
    }

    public void mvGira(float angle) {
        Vector4 forward = center.subtract(aux).normalize();

        float cosAngle = (float) Math.cos(Math.toRadians(angle));
        float sinAngle = (float) Math.sin(Math.toRadians(angle));

        Vector4 newUp = up.mult(cosAngle).add(forward.cross3(up).mult(sinAngle));
        up = newUp.normalize();
    }
    // rotació horitzontal
    public void rotH(float angle) {
        Vector4 direction = center.subtract(aux);
        float cosAngle = (float) Math.cos(Math.toRadians(angle));
        float sinAngle = (float) Math.sin(Math.toRadians(angle));

        float newX = direction.get(0) * cosAngle - direction.get(2) * sinAngle;
        float newZ = direction.get(0) * sinAngle + direction.get(2) * cosAngle;

        center = new Vector4(newX + aux.get(0), direction.get(1) + aux.get(1), newZ + aux.get(2), 1.0f);
    }
}

