package a4;

import static com.jogamp.opengl.GL4.*;

/**
 * Stores model properties including primitive type, winding order, and texture wrapping behavior.
 */
public class ModelProps {

    public int primitive = GL_TRIANGLES;
    public int windingOrder = GL_CCW;
    public int cullFace = GL_BACK;

}
