package a4;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.event.*;

public class LightController implements MouseListener, MouseMotionListener, MouseWheelListener {

    public static final float SENSITIVITY = .08f;
    private final Vector2f startPoint = new Vector2f();
    private final Vector2f endPoint = new Vector2f();
    private final Vector2f direction = new Vector2f();
    private final Vector4f lightPosition = new Vector4f();
    private final Vector4f forwardMovement = new Vector4f();
    private final Vector4f rightMovement = new Vector4f();
    private final Camera camera;
    private final Scene scene;
    private boolean isMouseDown = false;

    public LightController(Camera camera, Scene scene) {
        this.camera = camera;
        this.scene = scene;
    }

    public void update(double dt) {
        if(isMouseDown) {
            direction.set(endPoint.x - startPoint.x, -(endPoint.y - startPoint.y));
            direction.mul(SENSITIVITY * (float)dt);

            forwardMovement.set(camera.getForward(), 1f);
            forwardMovement.mul(direction.y);
            forwardMovement.y = 0f;

            rightMovement.set(camera.getRight(), 1f);
            rightMovement.mul(direction.x);
            rightMovement.y = 0f;

            lightPosition.set(RenderSystem.getInstance().getLightPosition());
            lightPosition.add(forwardMovement);
            lightPosition.add(rightMovement);
            lightPosition.set(
                    (float) MathUtil.clamp(lightPosition.x(), -50.0, 50.0),
                    lightPosition.y(),
                    (float) MathUtil.clamp(lightPosition.z(), -50.0, 50.0),
                    1f
            );
            RenderSystem.getInstance().setLightPosition(lightPosition);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint.set(e.getPoint().x, e.getPoint().y);
        endPoint.set(e.getPoint().x, e.getPoint().y);
        isMouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        endPoint.set(e.getPoint().x, e.getPoint().y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        lightPosition.set(RenderSystem.getInstance().getLightPosition());
        float y = lightPosition.y() - (float) e.getPreciseWheelRotation();
        lightPosition.y = (float) MathUtil.clamp(y, 0.1, 20.0);
        RenderSystem.getInstance().setLightPosition(lightPosition);
    }
}
