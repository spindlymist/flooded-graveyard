package a4;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import org.joml.*;

import java.util.ArrayList;
import java.util.Collection;

import static com.jogamp.opengl.GL4.*;

public class RenderSystem {

    public static class SharedRenderers {
        public static ShadowRenderer shadowRenderer;
        public static DummyRenderer dummyRenderer = new DummyRenderer();
    }

    ////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////

    private static final int MATRIX_STACK_SIZE = 16;
    private static final RenderSystem instance = new RenderSystem();

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private AmbientLight globalAmbient;
    private PointLight pointLight;
    private final Matrix4fStack modelMatStack = new Matrix4fStack(MATRIX_STACK_SIZE);
    private final Collection<Integer> litShaderPrograms = new ArrayList<Integer>();

    // For shadows
    private Camera shadowCamera;
    private final int[] shadowCubemap = new int[1];
    private final int[] shadowBuffer = new int[1];
    private static final int SHADOW_WIDTH = 1024;
    private static final int SHADOW_HEIGHT = 1024;

    // For water
    private final int[] waterBuffers = new int[2];
    private final int[] waterTextures = new int[2];
    private int waterNoiseTexture;
    private static final int REFLECTION = 0;
    private static final int REFRACTION = 1;
    private float waterHeight;

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    private RenderSystem() {
        try {
            int shadowProgram = Utils.createShaderProgram(
                    "shaders/shadowMapVertShader.glsl",
                    "shaders/shadowMapGeomShader.glsl",
                    "shaders/shadowMapFragShader.glsl"
            );
           SharedRenderers.shadowRenderer = new ShadowRenderer(shadowProgram);
        }
        catch(Utils.OpenGLException e) {
            System.err.println("Failed to initialize RenderSystem: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    public static RenderSystem getInstance() {
        return instance;
    }

    public void init(Camera camera) throws Utils.OpenGLException {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

        shadowCamera = new ShadowCamera(new Camera.CameraParameters(
                90f,
                (float) SHADOW_WIDTH / SHADOW_HEIGHT,
                .1f,
                100f
        ));
        createLights();
        prepareShadowBuffer();
        prepareWaterBuffer(REFLECTION);
        prepareWaterBuffer(REFRACTION);
        Noise noise = new WaterNoise(256, 256, 256);
        waterNoiseTexture = Utils.loadTexture3D(noise.generateData(6), 256, 256, 256);

        int skyboxProgram = Utils.createShaderProgram(
                "shaders/skyboxVertShader.glsl",
                "shaders/skyboxFragShader.glsl"
        );
        camera.setSkybox(new Skybox(skyboxProgram, "assets/textures/starry_skybox"));
    }

    public void render(Scene scene) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        Camera camera = scene.getCamera();
        for(Integer shaderProgram : litShaderPrograms) {
            installLights(gl, camera, shaderProgram);
        }

        shadowRenderPass(gl, scene);
        waterRefractionRenderPass(gl, scene);
        waterReflectionRenderPass(gl, scene);
        mainRenderPass(gl, scene);
        alphaRenderPass(gl, scene);
    }

    public Vector4fc getLightPosition() {
        return pointLight.getPosition();
    }

    public void setLightPosition(Vector4fc position) {
        pointLight.setPosition(position);
    }

    public boolean isUnderwater(Vector3fc position) {
        return position.y() < waterHeight;
    }

    public void setWaterHeight(float waterHeight) {
        this.waterHeight = waterHeight;
    }

    public float getWaterHeight() {
        return waterHeight;
    }

    ////////////////////////////////////////////////////////////
    // Protected Methods
    ////////////////////////////////////////////////////////////

    protected void registerLitShaderProgram(int id) {
        litShaderPrograms.add(id);
    }

    protected void toggleLight() {
        pointLight.setEnabled(!pointLight.isEnabled());
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods
    ////////////////////////////////////////////////////////////

    private void createLights() {
        globalAmbient = new AmbientLight(new Vector4f(.18f, .18f, .4f, 1f));
        pointLight = new PointLight(
                new Vector4f(-5f, 4f, -19f, 1f),
                new Vector4f(.4f, .2f, .2f, 0f),
                new Vector4f(1f, .9f, .5f, 0f),
                new Vector4f(1f, .9f, .5f, 0f)
        );
    }

    private void installLights(GL4 gl, Camera camera, int shaderProgram) {
        gl.glUseProgram(shaderProgram);
        globalAmbient.installLight(gl, camera, shaderProgram);
        pointLight.installLight(gl, camera, shaderProgram);
    }

    private void prepareShadowBuffer() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        // Create custom frame buffer
        gl.glGenFramebuffers(1, shadowBuffer, 0);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);

        // Create and configure shadow cube map
        gl.glGenTextures(1, shadowCubemap, 0);
        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, shadowCubemap[0]);
        for(int face = 0; face < 6; face++) {
            gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0, GL_DEPTH_COMPONENT24, SHADOW_WIDTH,
                    SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        }
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        // Associate framebuffer with shadow texture
        gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowCubemap[0], 0);

        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void prepareWaterBuffer(int index) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        int[] bufferID = new int[1];
        gl.glGenFramebuffers(1, bufferID, 0);
        waterBuffers[index] = bufferID[0];
        gl.glBindFramebuffer(GL_FRAMEBUFFER, bufferID[0]);

        gl.glGenTextures(1, bufferID, 0);
        waterTextures[index] = bufferID[0];
        gl.glBindTexture(GL_TEXTURE_2D, bufferID[0]);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, Application.GL_CANVAS_WIDTH, Application.GL_CANVAS_HEIGHT, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, null);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, bufferID[0], 0);
        gl.glDrawBuffer(GL_COLOR_ATTACHMENT0);

        gl.glGenTextures(1, bufferID, 0);
        gl.glBindTexture(GL_TEXTURE_2D, bufferID[0]);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, Application.GL_CANVAS_WIDTH, Application.GL_CANVAS_HEIGHT,
                0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, bufferID[0], 0);

        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void shadowRenderPass(GL4 gl, Scene scene) {
        // Set viewport size to the size of a face of the cubemap
        gl.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);

        // Select custom framebuffer
        gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);

        // Clear depth buffer
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        // Enable depth testing
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        // Disable drawing colors
        gl.glDrawBuffer(GL_NONE);
        gl.glReadBuffer(GL_NONE);

        shadowCamera.setPosition(pointLight.getPosition());

        modelMatStack.clear();
        scene.render(RenderPass.Shadows, shadowCamera, shadowCamera, modelMatStack);

        // Undo state changes
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        gl.glViewport(0, 0, Application.GL_CANVAS_WIDTH, Application.GL_CANVAS_HEIGHT);
        gl.glDrawBuffer(GL_FRONT);
        gl.glReadBuffer(GL_FRONT);
    }

    private void waterRefractionRenderPass(GL4 gl, Scene scene) {
        gl.glBindFramebuffer(GL_FRAMEBUFFER, waterBuffers[REFRACTION]);
        genericRenderPass(gl, scene, RenderPass.WaterRefraction, scene.getCamera(), true, true);
    }

    private final Vector3f newPos = new Vector3f();
    private final Vector3f newFwd = new Vector3f();
    private void waterReflectionRenderPass(GL4 gl, Scene scene) {
        // Create reflection camera
        Camera mainCamera = scene.getCamera();
        Camera reflectionCamera = new Camera(mainCamera.getCameraParameters());
        reflectionCamera.setSkybox(mainCamera.getSkybox());

        Vector3fc mainPos = mainCamera.getPosition();
        Vector3fc mainFwd = mainCamera.getForward();

        mainFwd.reflect(MathUtil.VEC3_UP, newFwd);
        float heightAboveWater = mainPos.y() - waterHeight;
        newPos.set(mainPos.x(), waterHeight - heightAboveWater, mainPos.z());

        reflectionCamera.setPosition(newPos);
        reflectionCamera.lookAt(newPos.add(newFwd));

        // Render scene to reflection buffer
        gl.glBindFramebuffer(GL_FRAMEBUFFER, waterBuffers[REFLECTION]);
        genericRenderPass(gl, scene, RenderPass.WaterReflection, reflectionCamera, true, true);
    }

    private void mainRenderPass(GL4 gl, Scene scene) {
        // Select default framebuffer
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Bind shadow cubemap to texture unit 4
        gl.glActiveTexture(GL_TEXTURE4);
        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, shadowCubemap[0]);

        // Bind refraction texture to texture unit 6
        gl.glActiveTexture(GL_TEXTURE6);
        gl.glBindTexture(GL_TEXTURE_2D, waterTextures[REFRACTION]);

        // Bind reflection texture to texture unit 7
        gl.glActiveTexture(GL_TEXTURE7);
        gl.glBindTexture(GL_TEXTURE_2D, waterTextures[REFLECTION]);

        // Bind water noise texture to texture unit 9
        gl.glActiveTexture(GL_TEXTURE9);
        gl.glBindTexture(GL_TEXTURE_3D, waterNoiseTexture);

        genericRenderPass(gl, scene, RenderPass.Main, scene.getCamera(), true, true);
    }

    private void alphaRenderPass(GL4 gl, Scene scene) {
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendEquation(GL_FUNC_ADD);

        genericRenderPass(gl, scene, RenderPass.Alpha, scene.getCamera(), false, false);

        gl.glDisable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ZERO);
    }

    private void genericRenderPass(GL4 gl, Scene scene, RenderPass renderPass, Camera camera, boolean clear, boolean renderSkybox) {
        if(clear) {
            // Clear color/depth buffers
            gl.glClearColor(.12f, .12f, .25f, 1f);
            gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }

        // Enable drawing colors
        gl.glDrawBuffer(GL_FRONT);
        gl.glReadBuffer(GL_FRONT);

        Skybox skybox = camera.getSkybox();
        if(renderSkybox && skybox != null) {
            // Disable depth testing
            gl.glDisable(GL_DEPTH_TEST);
            skybox.render(camera);
        }

        // Enable depth testing
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        modelMatStack.clear();
        scene.render(renderPass, camera, shadowCamera, modelMatStack);
    }

}
