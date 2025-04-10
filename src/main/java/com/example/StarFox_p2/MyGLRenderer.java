package com.example.StarFox_p2;

import static javax.microedition.khronos.opengles.GL10.GL_CULL_FACE;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.util.Random;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    // CAMERA  ---------
    private Camera camera, altraCam;
    boolean canviCam = false;

    private float pitch = 0, roll = 0, yaw = 0;

    // NAU ---------
    private float spaceshipPosX = 0.0f;
    private float spaceshipPosY = 0.0f;
    private float spaceshipPosZ = 4.0f;

    Context context;

    Object3D spaceship, enemy;
    Object3D background, hud, ring;

    // ENEMIC ---------
    private float enemy1Y = 0.0f;
    private float enemy2Y = 0.0f;
    private float enemy3Y = 0.0f;
    private float enemy4Y = 0.0f;
    private float enemy5Y = 0.0f;
    private float enemySpeed = 0.05f; // Velocitat de moviment

    // ANELL ---------
    private Random random = new Random();
    private int numberOfRings = 10;           // Nombre d'anells
    private float ringSpacing = 20.0f;        // Separació entre anells en Z
    private float[][] ringPositions;          // Array per emmagatzemar les posicions (X, Y, Z)

    public MyGLRenderer(Context context) {
        this.context = context;
        initializeRingPositions();
        this.spaceship = new Object3D(context, R.raw.nau_objecte);
        this.enemy = new Object3D(context, R.raw.enemic_objecte);
        this.background = new Object3D(context, R.raw.mar_objecte);
        this.hud = new Object3D(context, R.raw.hud_objecte);
        this.ring = new Object3D(context, R.raw.ring_objecte);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Configura el color de fons (blanc)
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Configuració de profunditat
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        // Configuració de transparència (blending)
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        // Configuració general
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER); // Desactiva el dithering (opcional)

        // ILUMINACIÓ AMBIENTAL GLOBAL
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, new float[]{0, 0, 0, 0}, 0);
        gl.glEnable(GL10.GL_LIGHTING);      // ILUMINACIÓ GLOBAL
        gl.glEnable(GL10.GL_NORMALIZE);     // ILUMINACIÓ per escalar bé als objectes

        // Configuració de culling (desactiva les cares posteriors)
        gl.glEnable(GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        initializeRingPositions();

        this.camera = new Camera(gl, new Vector4( 0, 1, 307f, 1), new Vector4(0f, 0f, 0f, 1), new Vector4(0f, 1f, 0f, 1));
        this.altraCam = new Camera(gl, new Vector4(0, 5, 307.0f, 1.0f), new Vector4(0, 0, 0, 1), new Vector4(0, 1, 0, 1));


        this.spaceship.cargaTextura(gl, context, R.raw.nau_textura);
        this.enemy.cargaTextura(gl, context, R.raw.enemic_textura);
        this.background.cargaTextura(gl, context, R.raw.mar_textura);
        this.hud.cargaTextura(gl, context, R.raw.hud_textura);
        this.ring.cargaTextura(gl, context, R.raw.ring_textura);

        // ILUMINACIÓ
        Light light = new Light(gl, GL10.GL_LIGHT0);
        light.setPosition(new float[]{0.0f, 1, 1, 0.0f}); // arriba de forma paral·lela
        light.setDiffuseColor(new float[]{0.5f, 0.5f, 0.5f}); // color difús a la llum

        // ILUMINACIÓ - PUNTUAL
        Light pointLight = new Light(gl, GL10.GL_LIGHT1);
        pointLight.setPosition(new float[]{0.0f, 4, 0, 1.0f});
        pointLight.setDiffuseColor(new float[]{0.68f, 0.93f, 0.93f});
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;
        float aspect = (float) width / height;

        // LIGHT ---------
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 90.0f, aspect, 0.1f, 10000.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Fons transparent
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if (ringPositions == null) return; // Evita cridar si no s'ha inicialitzat

        if(!canviCam) {
            // HUD ---------------
            gl.glPushMatrix();
            gl.glTranslatef(0, -1.5f, -8);
            gl.glScalef(0.9f, 0.9f, 0.9f);
            hud.draw(gl);
            gl.glPopMatrix();

            // FONS  ---------------
            gl.glPushMatrix();
            gl.glScalef(1.0f, 2, 1.5f); // Escala el fons (ajusta els valors segons el model)
            gl.glTranslatef(0, -120, -70);         // Ajusta la posició si cal
            background.draw(gl);
            gl.glPopMatrix();

            camera.gluLookAt();

            gl.glPushMatrix();
            gl.glTranslatef(0, 0, 300);
            gl.glRotatef(180, 1, 0, 0);
            gl.glTranslatef(spaceshipPosX, spaceshipPosY, spaceshipPosZ);
            gl.glRotatef(roll, 0.0f, 0.0f, 1.0f);
            gl.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
            spaceship.draw(gl);
            gl.glPopMatrix();
        } else {
            // FONS  ---------------
            gl.glPushMatrix();
            gl.glScalef(1.0f, 2, 1.5f); // Escala el fons (ajusta els valors segons el model)
            gl.glTranslatef(0, -120, -70);         // Ajusta la posició si cal
            background.draw(gl);
            gl.glPopMatrix();

            altraCam.gluLookAt();
        }

        // ENEMICS ---------------
        // Actualitza el moviment dels enemics
        updateEnemyMovement();

        // Calcula les posicions X inicials dels enemics
        float baseX = -7.5f; // Punt de partida per al primer enemic
        float spacing = 10.0f; // Separació fixa entre enemics

        // Enemic 1
        gl.glPushMatrix();
        gl.glTranslatef(baseX - spacing, enemy1Y, 250.0f);
        enemy.draw(gl);
        gl.glPopMatrix();

        // Enemic 2
        gl.glPushMatrix();
        gl.glTranslatef(baseX, enemy2Y, 250.0f);
        enemy.draw(gl);
        gl.glPopMatrix();

        // Enemic 3
        gl.glPushMatrix();
        gl.glTranslatef(baseX + spacing, enemy3Y, 250.0f);
        enemy.draw(gl);
        gl.glPopMatrix();

        // Enemic 4
        gl.glPushMatrix();
        gl.glTranslatef(baseX + spacing * 2, enemy4Y, 250.0f);
        enemy.draw(gl);
        gl.glPopMatrix();

        // Enemic 5
        gl.glPushMatrix();
        gl.glTranslatef(baseX + spacing * 3, enemy5Y, 250.0f);
        enemy.draw(gl);
        gl.glPopMatrix();

        // ANELLS  ---------------
        int numberOfRings = 10;    // Nombre d'anells al camí

        for (int i = 0; i < numberOfRings; i++) {
            gl.glPushMatrix();
            gl.glTranslatef(ringPositions[i][0], ringPositions[i][1], ringPositions[i][2]); // X, Y, Z
            ring.draw(gl);
            gl.glPopMatrix();
        }
    }

    public void updateEnemyMovement() {

        // Límit de moviment vertical
        float enemyYLimit = 8.0f;

        // Moviment enemic 1 (puja i baixa)
        enemy1Y -= enemySpeed;
        if (enemy1Y >= enemyYLimit || enemy1Y <= -enemyYLimit) {
            enemySpeed *= -1; // Invertir direcció
        }
        // Moviment enemic 2 (baixa i puja)
        enemy2Y += enemySpeed;
        if (enemy2Y >= enemyYLimit || enemy2Y <= -enemyYLimit) {
            enemySpeed *= -1; // Invertir direcció
        }
        // Moviment enemic 3 (puja i baixa)
        enemy3Y -= enemySpeed;
        if (enemy3Y >= enemyYLimit || enemy3Y <= -enemyYLimit) {
            enemySpeed *= -1; // Invertir direcció
        }
        // Moviment enemic 4 (baixa i puja)
        enemy4Y += enemySpeed;
        if (enemy4Y >= enemyYLimit || enemy4Y <= -enemyYLimit) {
            enemySpeed *= -1; // Invertir direcció
        }
        // Moviment enemic 5 (puja i baixa)
        enemy5Y -= enemySpeed;
        if (enemy5Y >= enemyYLimit || enemy5Y <= -enemyYLimit) {
            enemySpeed *= -1; // Invertir direcció
        }
    }

    // Inicialitza les posicions aleatòries
    private void initializeRingPositions() {
        ringPositions = new float[numberOfRings][3];
        for (int i = 0; i < numberOfRings; i++) {
            ringPositions[i][0] = random.nextFloat() * 10 - 5; // X: entre -5 i 5
            ringPositions[i][1] = random.nextFloat() * 4 - 2;  // Y: entre -2 i 2
            ringPositions[i][2] = 200 + i * ringSpacing;       // Z: separació regular
        }
    }

    // MOVIMENT DE LA CÀMERA TOTAL
    public void moveCameraLeft() {
        camera.mvLeft(0.1f);
        camera.rotH(-0.01f);
    }
    public void moveCameraRight() {
        camera.mvRight(0.1f);
        camera.rotH(0.01f);
    }
    public void moveCameraForward() {
        camera.mvEndavant(0.5f);
    }
    public void moveCameraBackward() {
        camera.mvEnrere(0.5f);
    }
    public void moveCameraUp() {
        camera.mvAmunt(0.1f);
        camera.rotV(0.01f);
    }
    public void moveCameraDown() {
        camera.mvAvall(0.1f);
        camera.rotV(-0.01f);
    }
    public void cameraRollLeft() {
        camera.mvGira(-0.05f);
    }
    public void cameraRollRight() {
        camera.mvGira(0.05f);
    }

    // MOVIMENT DE LA NAU TOTAL
    public void moveSpaceshipLeft() {
        spaceshipPosX -= 0.1f;
        yaw -= 0.1f;
    }
    public void moveSpaceshipRight() {
        spaceshipPosX += 0.1f;
        yaw += 0.1f;
    }
    public void moveSpaceshipForward() {
        spaceshipPosZ += 0.5f;
    }
    public void moveSpaceshipBackward() {
        spaceshipPosZ -= 0.5f;
    }
    public void moveSpaceshipUp() {
        spaceshipPosY -= 0.1f;
        pitch += 0.1f;
    }
    public void moveSpaceshipDown() {
        spaceshipPosY += 0.1f;
        pitch -= 0.1f;
    }
    public void spaceshipRollRight() {
        roll += 0.5f;
    }
    public void spaceshipRollLeft() {
        roll -= 0.5f;
    }

    public void canviarCam() {
        canviCam = !canviCam;
    }


}
