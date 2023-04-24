package a4;

import org.joml.Matrix4fc;

public class DummyRenderer implements Renderer {

    @Override
    public void render(RenderPass renderPass, Camera camera, Camera shadowCamera, Matrix4fc modelMatrix, Entity entity) {
    }

}
