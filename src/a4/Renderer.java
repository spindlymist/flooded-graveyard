package a4;

import org.joml.Matrix4fc;

/**
 * An object that can render to a GLCanvas.
 */
public interface Renderer {

    void render(RenderPass renderPass, Camera camera, Camera shadowCamera, Matrix4fc modelMatrix, Entity entity);

}
