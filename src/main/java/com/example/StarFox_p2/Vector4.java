package com.example.StarFox_p2;

public class Vector4 {

    private float values[];

    public Vector4() {
        values = new float[4];
    }

    public Vector4(float x, float y, float z, float w) {
        values = new float[] {x, y, z, w};
    }

    public float get(int index) {
        return values[index];
    }

    public void set(int index, float value) {
        values[index] = value;
    }

    public Vector4 add(float x, float y, float z, float w) {
        return add(new Vector4(x, y, z, w));
    }

    public float module() {
        float length = 0.0f;
        for(int i = 0; i < values.length; i++)
            length += values[i] * values[i];
        return (float) Math.sqrt(length);
    }

    public Vector4 normalize() {
        float aux = module();
        if (aux == 0) return this;
        else
            return new Vector4(values[0]/aux, values[1]/aux, values[2]/aux, values[3]/aux);
    }

    public Vector4 cross3(Vector4 o) {
        Vector4 result = new Vector4(
                values[1] * o.values[2] - values[2] * o.values[1],
                values[2] * o.values[0] - values[0] * o.values[2],
                values[0] * o.values[1] - values[1] * o.values[0],
                0.0f);
        return result;
    }

    public Vector4 add(Vector4 vector) {
        Vector4 v = new Vector4();
        for(int i = 0; i < 4; i++)
            v.set(i, values[i] + vector.values[i]);
        return v;
    }

    public Vector4 subtract(Vector4 auxV) {
        Vector4 vector = new Vector4();
        for (int i = 0; i < 4; i++)
            vector.set(i, values[i] - auxV.values[i]);
        return vector;
    }

    public Vector4 mult(float aux) {
        Vector4 vector = new Vector4();
        for(int i = 0; i < 4; i++)
            vector.set(i, values[i] * aux);
        return vector;
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        for(int i = 0; i < values.length; i++) {
            stringBuilder.append(values[i]);
            if (i != values.length - 1)
                stringBuilder.append(", ");
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
