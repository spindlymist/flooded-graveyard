package a4;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;

import static com.jogamp.opengl.GL4.GL_VERSION;

/**
 * The main application window. Handles initialization, rendering, and input events.
 */
public class Application extends JFrame implements GLEventListener {

    ////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////

    private static final String FRAME_TITLE = "CSC 155 Assignment 4";
    public static int GL_CANVAS_WIDTH = 1920;
    public static int GL_CANVAS_HEIGHT = 1080;
    public static float GL_CANVAS_ASPECT_RATIO = (float) GL_CANVAS_WIDTH / GL_CANVAS_HEIGHT;

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private GLCanvas canvas;
    private final Animator animator;
    /**
     * Stores the IDs of vertex array objects.
     */
    private final int[] vaos = new int[1];
    private long lastFrameMillis;
    private static double gameTime = 0.0;
    private Scene scene;

    public static double getGameTime() {
        return gameTime;
    }

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public Application() {
        initFrame();
        buildInterface();
        setVisible(true);
        animator = new Animator(canvas);
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods
    ////////////////////////////////////////////////////////////

    private void initFrame() {
        setTitle(FRAME_TITLE);
        setSize(GL_CANVAS_WIDTH, GL_CANVAS_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void buildInterface() {
        setLayout(new BorderLayout());
        buildCanvasPanel();
        pack();
    }

    private void buildCanvasPanel() {
        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout(new BoxLayout(canvasPanel, BoxLayout.X_AXIS));
        canvasPanel.setPreferredSize(new Dimension(GL_CANVAS_WIDTH, GL_CANVAS_HEIGHT));

        canvas = new GLCanvas();
        canvas.addGLEventListener(this);

        canvasPanel.add(canvas);
        add(canvasPanel);
    }

    private void createVAOs(GL4 gl) {
        gl.glGenVertexArrays(vaos.length, vaos, 0);
        gl.glBindVertexArray(vaos[0]);
    }

    private void printVersionNumbers(GL4 gl) {
        System.out.println("JOGL version:   " + JoglVersion.getInstance().getImplementationVersion());
        System.out.println("OpenGL version: " + gl.glGetString(GL_VERSION));
    }

    private double calcElapsedSeconds() {
        long currentFrameMillis = System.currentTimeMillis();
        double elapsedTime = (currentFrameMillis - lastFrameMillis) / 1000.0;
        lastFrameMillis = currentFrameMillis;

        return elapsedTime;
    }

    /**
     * Closes the program gracefully.
     * @param exitStatus 0 if the program executed successfully, nonzero otherwise
     */
    private void exit(int exitStatus) {
        animator.stop();
        super.dispose(); // Not to be confused with dispose(GLAutoDrawable) from the GLEventListener interface
        System.exit(exitStatus);
    }

    ////////////////////////////////////////////////////////////
    // OpenGL Event Handlers
    ////////////////////////////////////////////////////////////

    /**
     * Updates the scene in real time and draws the current view to the GL canvas. This method is called repeatedly by
     * the animator.
     */
    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        // Update scene
        double dt = calcElapsedSeconds();
        gameTime += dt;
        scene.update(dt);

        // Render scene
        RenderSystem.getInstance().render(scene);
    }

    /**
     * Performs one-time initialization such as loading shaders and creating VBOs.
     */
    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        printVersionNumbers(gl);
        createVAOs(gl);
        try {
            scene = new Assignment4Scene(
                    canvas,
                    new Camera.CameraParameters(60f, GL_CANVAS_ASPECT_RATIO, .1f, 1000f)
            );
            scene.init(gl);
            RenderSystem.getInstance().init(scene.getCamera());
        }
        catch(Utils.OpenGLException e) {
            System.err.println("Failed to initialize: " + e.getMessage());
            e.printStackTrace();
            exit(1);
        }
        lastFrameMillis = System.currentTimeMillis();
        animator.start();
    }

    /**
     * Reacts to changes in the GL canvas size. Not used in this program.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    /**
     * Performs clean-up operations when the GL canvas is destroyed. Not used in this program.
     */
    public void dispose(GLAutoDrawable drawable) {
    }

}
