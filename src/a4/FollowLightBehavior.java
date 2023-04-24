package a4;

import org.joml.Vector4fc;

public class FollowLightBehavior extends Behavior {

    private final Scene scene;

    public FollowLightBehavior(Entity owner, Scene scene) {
        super(owner);
        this.scene = scene;
    }

    @Override
    public void update(double dt) {
        Vector4fc lightPosition = RenderSystem.getInstance().getLightPosition();
        getOwner().setPosition(lightPosition.x(), lightPosition.y(), lightPosition.z());
    }

}
