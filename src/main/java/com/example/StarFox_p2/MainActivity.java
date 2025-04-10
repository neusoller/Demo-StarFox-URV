package com.example.StarFox_p2;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

public class MainActivity extends Activity {
    private GLSurfaceView glView;
    private MyGLRenderer myGLRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glView = new GLSurfaceView(this);
        glView.setRenderer(myGLRenderer=new MyGLRenderer(this));
        this.setContentView(glView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }

    // Call back after onPause()
    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            // MOVIMENT AVIÓ
            case KeyEvent.KEYCODE_W:
                myGLRenderer.moveSpaceshipForward();
                myGLRenderer.moveCameraForward();
                return true;
            case KeyEvent.KEYCODE_A:
                myGLRenderer.moveSpaceshipLeft();
                myGLRenderer.moveCameraLeft();
                return true;
            case KeyEvent.KEYCODE_S:
                myGLRenderer.moveSpaceshipBackward();
                myGLRenderer.moveCameraBackward();
                return true;
            case KeyEvent.KEYCODE_D:
                myGLRenderer.moveSpaceshipRight();
                myGLRenderer.moveCameraRight();
                return true;

            // ROTACIÓ AVIÓ
            case KeyEvent.KEYCODE_DPAD_LEFT:
                myGLRenderer.spaceshipRollLeft();
                myGLRenderer.cameraRollLeft();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                myGLRenderer.spaceshipRollRight();
                myGLRenderer.cameraRollRight();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                myGLRenderer.moveSpaceshipUp();
                myGLRenderer.moveCameraUp();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                myGLRenderer.moveSpaceshipDown();
                myGLRenderer.moveCameraDown();
                return true;

            // CANVI CÀMERA
            case KeyEvent.KEYCODE_P:
                myGLRenderer.canviarCam();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}