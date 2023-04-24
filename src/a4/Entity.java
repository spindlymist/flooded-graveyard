package a4;

import com.jogamp.opengl.GL4;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents any physical or mathematically convenient entity in the 3D world. Includes position, rotation, and scale;
 * controlling behaviors; and child entities.
 */
public class Entity {

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private final Vector3f position = new Vector3f(0f, 0f, 0f);
    private final Vector3f eulerAngles = new Vector3f(0f, 0f, 0f);
    private float scale = 1f;
    private final Renderer defaultRenderer;
    private final Map<RenderPass,Renderer> renderers = new HashMap<>();
    private final EntityCollection children = new EntityCollection();
    private final Collection<Behavior> behaviors = new ArrayList<Behavior>();
    private final Model model;
    private boolean enabled = true;

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public Entity() {
        this.model = null;
        this.defaultRenderer = null;
    }

    public Entity(Model model, Renderer defaultRenderer) {
        this.model = model;
        this.defaultRenderer = defaultRenderer;
        renderers.put(RenderPass.Shadows, RenderSystem.SharedRenderers.dummyRenderer);
        renderers.put(RenderPass.Alpha, RenderSystem.SharedRenderers.dummyRenderer);
    }

    ////////////////////////////////////////////////////////////
    // Accessors and Mutators
    ////////////////////////////////////////////////////////////

    public Vector3fc getPosition() {
        return position;
    }

    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getEulerAngles() {
        return new Vector3f(
                (float) Math.toDegrees(eulerAngles.x),
                (float) Math.toDegrees(eulerAngles.y),
                (float) Math.toDegrees(eulerAngles.z)
        );
    }

    public void setEulerAngles(Vector3fc eulerAngles) {
        this.eulerAngles.set(
            Math.toRadians(eulerAngles.x()),
            Math.toRadians(eulerAngles.y()),
            Math.toRadians(eulerAngles.z())
        );
    }

    public void setEulerAngles(float x, float y, float z) {
        this.eulerAngles.set(
                Math.toRadians(x),
                Math.toRadians(y),
                Math.toRadians(z)
        );
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public EntityCollection getChildren() {
        return children;
    }

    public Model getModel() {
        return model;
    }

    public void enableShadow() {
        renderers.put(RenderPass.Shadows, RenderSystem.SharedRenderers.shadowRenderer);
    }

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    public void init(GL4 gl) {
        children.init(gl);
    }

    public void update(double dt) {
        if(!enabled) return;

        for(Behavior behavior : behaviors) {
            behavior.update(dt);
        }
        children.update(dt);
    }

    private Renderer getRenderer(RenderPass renderPass) {
        return renderers.getOrDefault(renderPass, defaultRenderer);
    }

    public void render(RenderPass renderPass, Camera camera, Camera shadowCamera, Matrix4fStack modelMatStack) {
        if(!enabled) return;

        // Apply transformations to a new matrix in stack
        modelMatStack.pushMatrix();
        modelMatStack.translate(position);
        modelMatStack.scale(scale);
        modelMatStack.rotateXYZ(eulerAngles);

        // Render self and children
        Renderer renderer = getRenderer(renderPass);
        if(renderer != null) renderer.render(renderPass, camera, shadowCamera, modelMatStack, this);
        children.render(renderPass, camera, shadowCamera, modelMatStack);

        // Remove the model matrix from stack
        modelMatStack.popMatrix();
    }

    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }

    public void translate(Vector3fc offset) {
        position.add(offset);
    }

    public void setRenderer(RenderPass renderPass, Renderer renderer) {
        renderers.put(renderPass, renderer);
    }

}
