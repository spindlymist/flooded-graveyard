package a4;

import com.jogamp.common.nio.Buffers;

import java.nio.FloatBuffer;

/**
 * A collection of buffers that may be reused to avoid allocating new memory.
 */
public class ReusableBuffers {

    public static final FloatBuffer floatBuf3 = Buffers.newDirectFloatBuffer(3);
    public static final FloatBuffer floatBuf4 = Buffers.newDirectFloatBuffer(4);
    public static final FloatBuffer floatBuf16 = Buffers.newDirectFloatBuffer(16);

}
