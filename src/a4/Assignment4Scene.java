package a4;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.awt.GLCanvas;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.awt.event.KeyEvent;

import static com.jogamp.opengl.GL4.GL_NONE;

public class Assignment4Scene extends Scene {

    ////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////

    private static final Vector4fc X_AXIS_COLOR = new Vector4f(1f, 0f, 0f, 1f);
    private static final Vector4fc Y_AXIS_COLOR = new Vector4f(0f, 1f, 0f, 1f);
    private static final Vector4fc Z_AXIS_COLOR = new Vector4f(0f, 0f, 1f, 1f);

    private static final float AXIS_LENGTH = 1500f;
    private static final float WATER_HEIGHT = 1.5f;

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private int unlitShaderProgram, phongShaderProgram, phong3DShaderProgram, waterShaderProgram, pointShaderProgram;
    private Entity axes, lightMarker, waterPlane;
    private final Camera.FogParameters aboveWaterFog =
            new Camera.FogParameters(new Vector4f(.12f, .12f, .25f, 1f), 5f, 27f);
    private final Camera.FogParameters belowWaterFog =
            new Camera.FogParameters(new Vector4f(.12f, .12f, .25f, 1f), 3f, 20f);

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public Assignment4Scene(GLCanvas canvas, Camera.CameraParameters cameraParameters) {
        super(canvas, cameraParameters);
    }

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    @Override
    public void init(GL4 gl) throws Utils.OpenGLException {
        loadShaders();
        loadModels(gl);
        populateScene();
        resetCameraPosition();
        RenderSystem.getInstance().setWaterHeight(WATER_HEIGHT);
        super.init(gl);
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods: Controls
    ////////////////////////////////////////////////////////////


    @Override
    public void update(double dt) {
        super.update(dt);

        if(getCamera().getPosition().y() >= RenderSystem.getInstance().getWaterHeight()) {
            getCamera().setFogParameters(aboveWaterFog);
        }
        else {
            getCamera().setFogParameters(belowWaterFog);
        }
    }

    private void toggleAxes() {
        axes.setEnabled(!axes.isEnabled());
    }

    private void toggleLightMarker() {
        lightMarker.setEnabled(!lightMarker.isEnabled());
    }

    private void resetCameraPosition() {
        getCamera().setPosition(new Vector3f(4f, 5f, 9f));
        getCamera().lookAt(new Vector3f(0f, 3f, -4f));
    }

    private void toggleWater() {
        waterPlane.setEnabled(!waterPlane.isEnabled());
        if(waterPlane.isEnabled()) {
            RenderSystem.getInstance().setWaterHeight(WATER_HEIGHT);
        }
        else {
            RenderSystem.getInstance().setWaterHeight(-10f);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            toggleAxes();
        }
        else if(e.getKeyCode() == KeyEvent.VK_F1) {
            resetCameraPosition();
        }
        else if(e.getKeyCode() == KeyEvent.VK_F2) {
            toggleLightMarker();
        }
        else if(e.getKeyCode() == KeyEvent.VK_F3) {
            RenderSystem.getInstance().toggleLight();
        }
        else if(e.getKeyCode() == KeyEvent.VK_F4) {
            toggleWater();
        }
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods: Loading Assets
    ////////////////////////////////////////////////////////////

    private void loadModels(GL4 gl) {
        ModelManager modelManager = ModelManager.getInstance();

        Model axisModel = new AxisModel(AXIS_LENGTH, X_AXIS_COLOR, Y_AXIS_COLOR, Z_AXIS_COLOR);
        axisModel.initVBOs();
        modelManager.addModel("axis", axisModel);

        Model waterPlaneModel = new PlaneModel(256f, 256f, 1f, 1f);
        waterPlaneModel.initVBOs();
        waterPlaneModel.props.cullFace = GL_NONE;
        modelManager.addModel("waterPlane", waterPlaneModel);

        Model treeModel = modelManager.importModel("tree", "assets/models/tree.obj");
        treeModel.initVBOs();
        treeModel.props.cullFace = GL_NONE;

        modelManager.importModel("cross", "assets/models/cross.obj").initVBOs();
        modelManager.importModel("grave1", "assets/models/grave1.obj").initVBOs();
        modelManager.importModel("groundPlane", "assets/models/plane.obj").initVBOs();
        modelManager.importModel("ghost", "assets/models/ghost.obj").initVBOs();
    }

    private void loadShaders() throws Utils.OpenGLException {
        unlitShaderProgram = Utils.createShaderProgram("shaders/unlitVertShader.glsl", "shaders/unlitFragShader.glsl");
        phongShaderProgram = Utils.createShaderProgram("shaders/phongVertShader.glsl", "shaders/phongFragShader.glsl");
        phong3DShaderProgram = Utils.createShaderProgram("shaders/phongVertShader3D.glsl", "shaders/phongFragShader3D.glsl");
        pointShaderProgram = Utils.createShaderProgram("shaders/pointVertShader.glsl", "shaders/pointFragShader.glsl");
        waterShaderProgram = Utils.createShaderProgram("shaders/waterVertShader.glsl", "shaders/waterFragShader.glsl");
        RenderSystem.getInstance().registerLitShaderProgram(phongShaderProgram);
        RenderSystem.getInstance().registerLitShaderProgram(phong3DShaderProgram);
        RenderSystem.getInstance().registerLitShaderProgram(waterShaderProgram);
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods: Scene Creation
    ////////////////////////////////////////////////////////////

    private void populateScene() throws Utils.OpenGLException {
        placeAxes();
        placeLightMarker();
        placeGround();
        placeTrees();
        placeCross();
        placeWater();
        placeGhost();
        placeGraves();
    }

    private void placeTrees() {
        Material treeMaterial = new Material();
        treeMaterial.specular.set(.1f, .1f, .1f, 1f);
        Renderer treeRenderer = new ModelRenderer(phongShaderProgram, treeMaterial);
        placeModel("tree", treeRenderer, new Vector3f(-12f, 0f, -7f), new Vector3f(0f, -11f, 0f));
        placeModel("tree", treeRenderer, new Vector3f(14f, 0f, -8f), new Vector3f(0f, 210f, 0f));
        placeModel("tree", treeRenderer, new Vector3f(-4f, 0f, -30f), new Vector3f(0f, -135f, 0f));
    }

    private void placeGraves() throws Utils.OpenGLException {
        Material graveMat = new Material();
        graveMat.diffuseTex = TextureManager.getInstance().getTexture("textures/Rock_028_COLOR.jpg");
        graveMat.normalMap = TextureManager.getInstance().getTexture("textures/Rock_028_NORM.jpg");
        graveMat.specular.set(.1f, .1f, .1f);
        Renderer graveRenderer = new ModelRenderer(phongShaderProgram, graveMat);
        placeGrave(graveRenderer, 7f,  -6f);
        placeGrave(graveRenderer, 9f,  0f);
        placeGrave(graveRenderer, -5f, 1f);
        placeGrave(graveRenderer, -7f,4f);

        placeGrave(graveRenderer, -6f, -12f);
        placeGrave(graveRenderer, -1f, -16f);
        placeGrave(graveRenderer, 2f,  -13f);
        placeGrave(graveRenderer, 6f,  -11f);
        placeGrave(graveRenderer, 11f, -15f);
        placeGrave(graveRenderer, 13f, -9f);

        placeGrave(graveRenderer, -8f, -18f);
        placeGrave(graveRenderer, 0f,  -22f);
        placeGrave(graveRenderer, 8f,  -19f);
    }

    private void placeGhost() throws Utils.OpenGLException {
        Material ghostMat = new Material();
        ghostMat.normalMap = TextureManager.getInstance().getTexture("textures/TexturesCom_Fabric_Felt_1K_normal.jpg");
        ghostMat.specular.set(.1f, .1f, .1f, 1f);
        ghostMat.shininess = .1f;
        Renderer ghostRenderer = new ModelRenderer(phongShaderProgram, ghostMat);
        Entity ghost = placeModel("ghost", RenderSystem.SharedRenderers.dummyRenderer, new Vector3f(4f, 3f, -4f), MathUtil.VEC3_ZERO);
        ghost.setScale(2.2f);
        ghost.setRenderer(RenderPass.Alpha, ghostRenderer);
        ghost.addBehavior(new DriftBehavior(ghost));
    }

    private void placeWater() {
        Material waterMat = new Material();
        waterMat.ambient.set(.1f, .3f, .8f);
        waterMat.diffuse.set(.1f, .3f, .8f);
        waterMat.specular.set(.1f, .2f, .7f);
        Model waterModel = ModelManager.getInstance().getModel("waterPlane");
        Renderer waterRenderer = new ModelRenderer(waterShaderProgram, waterMat);
        waterPlane = new Entity(waterModel, waterRenderer);
        waterPlane.setPosition(0f, WATER_HEIGHT, 0f);
        waterPlane.setRenderer(RenderPass.WaterReflection, RenderSystem.SharedRenderers.dummyRenderer);
        waterPlane.setRenderer(RenderPass.WaterRefraction, RenderSystem.SharedRenderers.dummyRenderer);
        add(waterPlane);
    }

    private void placeCross() throws Utils.OpenGLException {
        Material crossMat = new Material(0, .1f);
        Noise noise = new MarbleNoise(200, 200, 200);
        crossMat.tex3D = Utils.loadTexture3D(noise.generateData(6), 200, 200, 200);
        crossMat.specular.set(.1f, .1f, .1f, 1f);
        ModelRenderer crossRenderer = new ModelRenderer(phong3DShaderProgram, crossMat);
        placeModel("cross", crossRenderer, new Vector3f(-2f, 0f, -10f), MathUtil.VEC3_ZERO)
            .enableShadow();
    }

    private void placeGrave(Renderer graveRenderer, float x, float z) {
        Entity grave = placeModel("grave1", graveRenderer, new Vector3f(x, 0f, z), MathUtil.VEC3_ZERO);
        grave.setScale(1.5f);
        grave.enableShadow();
    }

    private void repeatPlacement(String modelName, Renderer renderer, Vector3fc startPosition, Vector3fc rotation, int count, Vector3fc positionStep) {
        Vector3f currentPosition = new Vector3f(startPosition);
        for(int i = 0; i < count; i++) {
            placeModel(modelName, renderer, currentPosition, rotation);
            currentPosition.add(positionStep);
        }
    }

    private Entity placeModel(String modelName, Renderer renderer, Vector3fc position, Vector3fc rotation) {
        Model model = ModelManager.getInstance().getModel(modelName);
        Entity entity = new Entity(model, renderer);
        entity.setPosition(position);
        entity.setEulerAngles(rotation);
        add(entity);

        return entity;
    }

    private void placeLightMarker() {
        PointRenderer renderer = new PointRenderer(pointShaderProgram, 10f, new Vector4f(1f, 1f, 0f, 1f));
        lightMarker = new Entity(null, renderer);
        lightMarker.addBehavior(new FollowLightBehavior(lightMarker, this));
        lightMarker.setEnabled(false);
        add(lightMarker);
    }

    private void placeAxes() {
        Renderer axisRenderer = new ModelRenderer(unlitShaderProgram, new Material());
        Model axisModel = ModelManager.getInstance().getModel("axis");
        axes = new Entity(axisModel, axisRenderer);
        axes.setEnabled(false);
        add(axes);
    }

    private void placeGround() throws Utils.OpenGLException {
        int grassTexture = TextureManager.getInstance().getTexture("textures/grass.png");
        Material grassMaterial = new Material(grassTexture, .1f);
        grassMaterial.specular.set(.1f, .1f, .1f, 1f);
        grassMaterial.normalMap = TextureManager.getInstance().getTexture("textures/grass_normal.png");
        Renderer groundRenderer = new ModelRenderer(phongShaderProgram, grassMaterial);
        placeModel("groundPlane", groundRenderer, MathUtil.VEC3_ZERO, MathUtil.VEC3_ZERO);
    }


}
