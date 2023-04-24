package a4;

import com.jogamp.opengl.awt.GLCanvas;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Scene extends EntityCollection implements KeyListener {

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private final Camera camera;
    private final CameraController cameraController;
    private final LightController lightController;

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public Scene(GLCanvas canvas, Camera.CameraParameters cameraParameters) {
        super();

        camera = new Camera(cameraParameters);
        cameraController = new CameraController(camera, 2f, 60f);
        canvas.addKeyListener(new KeyboardController());
        canvas.addKeyListener(this);

        lightController = new LightController(camera, this);
        canvas.addMouseListener(lightController);
        canvas.addMouseMotionListener(lightController);
        canvas.addMouseWheelListener(lightController);
    }

    ////////////////////////////////////////////////////////////
    // Accessors and Mutators
    ////////////////////////////////////////////////////////////

    public Camera getCamera() {
        return camera;
    }

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    @Override
    public void update(double dt) {
        cameraController.update(dt);
        lightController.update(dt);
        super.update(dt);
    }

    ////////////////////////////////////////////////////////////
    // Input Event Handlers
    ////////////////////////////////////////////////////////////

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

}
