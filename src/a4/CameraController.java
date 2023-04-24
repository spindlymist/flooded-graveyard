package a4;

import java.awt.event.KeyEvent;

/**
 * Controls the position and rotation of the camera using the keyboard.
 */
public class CameraController {

    ////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////

    public static final int SPRINT_MODIFIER_KEY = KeyEvent.VK_SHIFT;
    public static final int ROLL_MODIFIER_KEY = KeyEvent.VK_ALT;
    public static final float SPRINT_MULTIPLIER = 5f;

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private final Camera camera;
    private final float speed;
    private final float angularSpeed;

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public CameraController(Camera camera, float speed, float angularSpeed) {
        this.camera = camera;
        this.speed = speed;
        this.angularSpeed = angularSpeed;
    }

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    public void update(double dt) {
        handleMovement(dt);
        handleRotation(dt);
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods
    ////////////////////////////////////////////////////////////

    private float getMoveSpeed() {
        if(KeyboardController.isKeyDown(SPRINT_MODIFIER_KEY)) {
            return speed * SPRINT_MULTIPLIER;
        }
        else {
            return speed;
        }
    }

    private void handleMovement(double dt) {
        float deltaPos = (float) (getMoveSpeed() * dt);

        // Forward-back motion
        if(KeyboardController.isKeyDown(KeyEvent.VK_W)) {
            camera.moveForward(deltaPos);
        }
        else if(KeyboardController.isKeyDown(KeyEvent.VK_S)) {
            camera.moveForward(-deltaPos);
        }

        // Left-right motion
        if(KeyboardController.isKeyDown(KeyEvent.VK_D)) {
            camera.moveRight(deltaPos);
        }
        else if(KeyboardController.isKeyDown(KeyEvent.VK_A)) {
            camera.moveRight(-deltaPos);
        }

        // Up-down motion
        if(KeyboardController.isKeyDown(KeyEvent.VK_Q)) {
            camera.moveUp(deltaPos);
        }
        else if(KeyboardController.isKeyDown(KeyEvent.VK_E)) {
            camera.moveUp(-deltaPos);
        }
    }

    private void handleRotation(double dt) {
        float deltaAngle = (float) (angularSpeed * dt);

        // Pitch (look up/down)
        if(KeyboardController.isKeyDown(KeyEvent.VK_UP)) {
            camera.pitch(deltaAngle);
        }
        else if(KeyboardController.isKeyDown(KeyEvent.VK_DOWN)) {
            camera.pitch(-deltaAngle);
        }

        // Pan (look left/right)
        if (KeyboardController.isKeyDown(KeyEvent.VK_RIGHT)) {
            camera.rotateY(-deltaAngle);
        } else if (KeyboardController.isKeyDown(KeyEvent.VK_LEFT)) {
            camera.rotateY(deltaAngle);
        }
    }

}
