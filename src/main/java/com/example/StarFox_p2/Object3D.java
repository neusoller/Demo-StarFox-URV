package com.example.StarFox_p2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;


public class Object3D {

    boolean textureEnabled = false;
    private FloatBuffer vertexB, normalB, texcoordB;
    private ShortBuffer iB;

    int[] textures = new int[1];
    int anInt = 0;

    public Object3D(Context ctx, int filenameId) {

        try {
            String line;
            String[] tmp, ftmp;

            ArrayList<Float> vertexL = new ArrayList<>();
            ArrayList<Float> texL = new ArrayList<>();
            ArrayList<Float> normalL = new ArrayList<>();

            ArrayList<Integer> vertexId = new ArrayList<>();
            ArrayList<Integer> texId = new ArrayList<>();
            ArrayList<Integer> normalId = new ArrayList<>();

            InputStream inputS = ctx.getResources().openRawResource(filenameId);
            BufferedReader inb = new BufferedReader(new InputStreamReader(inputS), 1024);

            while ((line = inb.readLine()) != null) {
                tmp = line.split(" ");
                if (tmp[0].equalsIgnoreCase("v"))
                    for (int i = 1; i < 4; i++) vertexL.add(Float.parseFloat(tmp[i]));

                if (tmp[0].equalsIgnoreCase("vn"))
                    for (int i = 1; i < 4; i++) normalL.add(Float.parseFloat(tmp[i]));

                if (tmp[0].equalsIgnoreCase("vt"))
                    for (int i = 1; i < 3; i++) texL.add(Float.parseFloat(tmp[i]));

                if (tmp[0].equalsIgnoreCase("f")) {
                    for (int i = 1; i < 4; i++) {
                        ftmp = tmp[i].split("/");
                        vertexId.add(Integer.parseInt(ftmp[0]) - 1);

                        if (!texL.isEmpty()) texId.add(Integer.parseInt(ftmp[1]) - 1);
                        if (!normalL.isEmpty()) normalId.add(Integer.parseInt(ftmp[2]) - 1);
                        anInt++;
                    }
                }
            }

            ByteBuffer vbb = ByteBuffer.allocateDirect(vertexId.size() * 4 * 3);
            vbb.order(ByteOrder.nativeOrder());
            vertexB = vbb.asFloatBuffer();

            for (int j = 0; j < vertexId.size(); j++) {
                vertexB.put(vertexL.get(vertexId.get(j) * 3));
                vertexB.put(vertexL.get(vertexId.get(j) * 3 + 1));
                vertexB.put(vertexL.get(vertexId.get(j) * 3 + 2));
            }
            vertexB.position(0);

            if (!texId.isEmpty()) {
                ByteBuffer vtbb = ByteBuffer.allocateDirect(texId.size() * 4 * 2);
                vtbb.order(ByteOrder.nativeOrder());
                texcoordB = vtbb.asFloatBuffer();

                for (int j = 0; j < texId.size(); j++) {
                    texcoordB.put(texL.get(texId.get(j) * 2));
                    texcoordB.put(texL.get(texId.get(j) * 2 + 1));
                }
                texcoordB.position(0);
            }

            if (!normalId.isEmpty()) {
                ByteBuffer nbb = ByteBuffer.allocateDirect(normalId.size() * 4 * 3);
                nbb.order(ByteOrder.nativeOrder());
                normalB = nbb.asFloatBuffer();

                for (int j = 0; j < normalId.size(); j++) {
                    normalB.put(normalL.get(normalId.get(j) * 3));
                    normalB.put(normalL.get(normalId.get(j) * 3 + 1));
                    normalB.put(normalL.get(normalId.get(j) * 3 + 2));
                }
                normalB.position(0);
            }

            ByteBuffer ibb = ByteBuffer.allocateDirect(anInt * 2);
            ibb.order(ByteOrder.nativeOrder());
            iB = ibb.asShortBuffer();

            for (short j = 0; j < anInt; j++) {
                iB.put(j);
            }
            iB.position(0);

        } catch (FileNotFoundException e) { e.printStackTrace();}
        catch (IOException e) { e.printStackTrace(); }
    }

    public void draw(GL10 gl) {
        gl.glColor4f(1, 1, 1, 1);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        if (textureEnabled) {
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glEnable(GL10.GL_TEXTURE_2D); // Enable texture
        }

        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexB);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalB);

        if (textureEnabled) {
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoordB);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        }

        gl.glDrawElements(GL10.GL_TRIANGLES, anInt, GL10.GL_UNSIGNED_SHORT, iB);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        if (textureEnabled) {
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisable(GL10.GL_TEXTURE_2D);
        }

        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
    }

    public void cargaTextura(GL10 gl, Context context, int idTextura) {
        // 1. Genera un ID únic per a la textura
        gl.glGenTextures(1, textures, 0);

        // 2. Vincula la textura generada
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // 3. Configura els filtres de textura (suavitzat i escalat)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        // 4. Activa el blending per suportar transparència
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        // 5. Carrega el PNG com a Bitmap
        InputStream istream = context.getResources().openRawResource(idTextura);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(istream); // Decodifica el PNG
        } catch (Exception e) {
            e.printStackTrace();
            return; // Finalitza si hi ha un error
        } finally {
            try {
                istream.close(); // Tanca l'InputStream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 6. Assigna el Bitmap com a textura d'OpenGL
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        // 7. Allibera la memòria del Bitmap (ja no cal perquè està carregat a OpenGL)
        bitmap.recycle();

        // 8. Marca que la textura està habilitada
        textureEnabled = true;
    }
}

