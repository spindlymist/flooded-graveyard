package a4;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.jogamp.opengl.GL4.*;

public class Utils {

    public static int createShaderProgram(int... shaders) throws OpenGLException {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        // Attach shaders
        int program = gl.glCreateProgram();
        for(int shader : shaders) {
            gl.glAttachShader(program, shader);
        }

        // Link
        gl.glLinkProgram(program);
        checkError();
        int[] linked = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, linked, 0);
        if(linked[0] != 1) {
            throw new OpenGLException("Failed to link shader program");
        }

        return program;
    }

    public static int createShaderProgram(String vertShaderFile, String geomShaderFile, String fragShaderFile) throws OpenGLException {
        return createShaderProgram(
            compileShader(GL_VERTEX_SHADER, vertShaderFile),
            compileShader(GL_GEOMETRY_SHADER, geomShaderFile),
            compileShader(GL_FRAGMENT_SHADER, fragShaderFile)
        );
    }

    public static int createShaderProgram(String vertShaderFile, String fragShaderFile) throws OpenGLException {
        return createShaderProgram(
            compileShader(GL_VERTEX_SHADER, vertShaderFile),
            compileShader(GL_FRAGMENT_SHADER, fragShaderFile)
        );
    }

    private static int compileShader(int type, String file) throws OpenGLException {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        String source;
        try {
            source = Files.readString(Path.of(file));
        }
        catch(IOException e) {
            throw new OpenGLException("Failed to reader shader from `" + file + "`", e);
        }

        int shader = gl.glCreateShader(type);
        gl.glShaderSource(shader, 1, new String[] { source }, null, 0);
        gl.glCompileShader(shader);
        checkError();

        int[] shaderCompiled = new int[1];
        gl.glGetShaderiv(shader, GL_COMPILE_STATUS, shaderCompiled, 0);
        if(shaderCompiled[0] != 1) {
            throw new OpenGLException("Failed to compile shader `" + file + "`");
        }

        return shader;
    }

    private static final StringBuilder errorMessageBuilder = new StringBuilder();
    private static final GLU glu = new GLU();
    public static void checkError() throws OpenGLException {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        errorMessageBuilder.setLength(0);
        int error;
        while((error = gl.glGetError()) != GL_NO_ERROR) {
            errorMessageBuilder
                .append("glError (")
                .append(error)
                .append("): ")
                .append(glu.gluErrorString(error))
                .append('\n');
        }

        if(errorMessageBuilder.length() > 0) {
            throw new OpenGLException(errorMessageBuilder.toString());
        }
    }

    public static int loadTexture(String textureFile) throws OpenGLException {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        int texture;
        try {
            Texture tex = TextureIO.newTexture(new File(textureFile), false);
            texture = tex.getTextureObject();
        }
        catch(IOException e) {
            throw new OpenGLException("Failed to load texture from `" + textureFile + "`", e);
        }

        // Generate mipmaps
        gl.glBindTexture(GL_TEXTURE_2D, texture);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        gl.glGenerateMipmap(GL.GL_TEXTURE_2D);

        // Enable anisotropic filtering
        if(gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
            float[] aniso = new float[1];
            gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso, 0);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso[0]);
        }

        checkError();
        return texture;
    }

    public static int loadTexture3D(byte[] data, int width, int height, int depth) throws OpenGLException {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        int texture;
        {
            int[] textures = new int[1];
            gl.glGenTextures(1, textures, 0);
            checkError();
            texture = textures[0];
        }

        gl.glBindTexture(GL_TEXTURE_3D, texture);
        gl.glTexStorage3D(GL_TEXTURE_3D, 1, GL_RGBA8, width, height, depth);
        gl.glTexSubImage3D(GL_TEXTURE_3D, 0, 0, 0, 0, width, height, depth, GL_RGBA,
            GL_UNSIGNED_INT_8_8_8_8_REV, Buffers.newDirectByteBuffer(data));
        gl.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        checkError();
        return texture;
    }

    public static int loadCubeMap(String directory, int width, int height) throws OpenGLException {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        byte[] top = getRGBAPixelData(Path.of(directory, "yp.jpg").toString());
        byte[] left = getRGBAPixelData(Path.of(directory, "xn.jpg").toString());
        byte[] front = getRGBAPixelData(Path.of(directory, "zp.jpg").toString());
        byte[] right = getRGBAPixelData(Path.of(directory, "xp.jpg").toString());
        byte[] back = getRGBAPixelData(Path.of(directory, "zn.jpg").toString());
        byte[] bottom = getRGBAPixelData(Path.of(directory, "yn.jpg").toString());

        int texture;
        {
            int[] textures = new int[1];
            gl.glGenTextures(1, textures, 0);
            checkError();
            texture = textures[0];
        }

        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
        gl.glTexStorage2D(GL_TEXTURE_CUBE_MAP, 1, GL_RGBA8, width, height);
        gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 0, 0, width, height,
            GL_RGBA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(right));
        gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0, 0, width, height,
            GL_RGBA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(left));
        gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 0, 0, width, height,
            GL_RGBA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bottom));
        gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 0, 0, width, height,
            GL_RGBA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(top));
        gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0, 0, width, height,
            GL_RGBA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(front));
        gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 0, 0, width, height,
            GL_RGBA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(back));
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        checkError();
        return texture;
    }

    private static byte[] getRGBAPixelData(String file) throws OpenGLException {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(file));
        }
        catch(IOException e) {
            throw new OpenGLException("Failed to read image from `" + file + "`", e);
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int[] argb = image.getRGB(0, 0, width, height, null, 0, width);
        byte[] rgba = new byte[argb.length * 4];

        for(int i = 0; i < argb.length; i++) {
            rgba[i * 4 + 0] = (byte) ((argb[i] >> 8*2) & 0xFF); // r
            rgba[i * 4 + 1] = (byte) ((argb[i] >> 8*1) & 0xFF); // g
            rgba[i * 4 + 2] = (byte) ((argb[i] >> 8*0) & 0xFF); // b
            rgba[i * 4 + 3] = (byte) ((argb[i] >> 8*3) & 0xFF); // a
        }

        return rgba;
    }

    public static class OpenGLException extends Exception {
        public OpenGLException() {
            super();
        }

        public OpenGLException(String message) {
            super(message);
        }

        public OpenGLException(Exception cause) {
            super(cause);
        }

        public OpenGLException(String message, Exception cause) {
            super(message, cause);
        }
    }

}
