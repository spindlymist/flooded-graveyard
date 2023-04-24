package a4;

import org.joml.Vector4f;
import org.joml.Vector4fc;

import static com.jogamp.opengl.GL4.GL_REPEAT;

/**
 * Data structure that defines the material characteristics of a surface. Properties include texture and
 * ADS lighting characteristics.
 */
public class Material {

    public int diffuseTex = -1;
    public int tex3D = -1;
    public int normalMap = -1;

    public Vector4f ambient = new Vector4f(1f, 1f, 1f, 1f);
    public Vector4f diffuse = new Vector4f(1f, 1f, 1f, 1f);
    public Vector4f specular = new Vector4f(1f, 1f, 1f, 1f);
    public float shininess = 1f;

    public int wrapModeS = GL_REPEAT;
    public int wrapModeT = GL_REPEAT;
    public int wrapModeR = GL_REPEAT;

    public Material() {
    }

    public Material(int diffuseTex) {
        this(diffuseTex, 1f);
    }

    public Material(int diffuseTex, float shininess) {
        this.diffuseTex = diffuseTex;
        this.shininess = shininess;
    }

    public Material(int diffuseTex, Vector4fc ambient, Vector4fc diffuse, Vector4fc specular, float shininess) {
        this.diffuseTex = diffuseTex;
        this.ambient.set(ambient);
        this.diffuse.set(diffuse);
        this.specular.set(specular);
        this.shininess = shininess;
    }

}
