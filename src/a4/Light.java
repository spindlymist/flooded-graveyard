package a4;

import com.jogamp.opengl.GL4;

public interface Light {

    void installLight(GL4 gl, Camera camera, int shaderProgram);

}
