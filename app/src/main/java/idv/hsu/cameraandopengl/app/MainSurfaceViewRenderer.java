package idv.hsu.cameraandopengl.app;

import android.content.Context;
import android.opengl.GLSurfaceView;
import idv.hsu.cameraandopengl.app.utils.MatrixHelper;
import idv.hsu.cameraandopengl.app.utils.ShaderHelper;
import idv.hsu.cameraandopengl.app.utils.TextResourceReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by freeman on 2015/6/10.
 */
public class MainSurfaceViewRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = MainSurfaceViewRenderer.class.getSimpleName();
    private static final boolean D = false;

    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";
    private static final int POSITION_COMPONENT_COUNT = 4; // ues two floating point values per vertex, so ...2
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    // 2 position code + 3 color code
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final FloatBuffer vertexData;
    private final Context mContext;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private int program;
    private int aPositionLocation;
    private int aColorLocation;
    private int uMatrixLocation;
    private GLSurfaceView mSurfaceView;

    public MainSurfaceViewRenderer(Context context) {
        mContext = context;
        mSurfaceView = new GLSurfaceView(context);
        mSurfaceView.setEGLContextClientVersion(2);
//        mSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 0);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mSurfaceView.setRenderer(this);

        float[] tableVerticesWithTriangles = {
                // Order of coordinates: X, Y, Z, W, R, G, B
                   0f,    0f, 0f, 1.5f,   1f,   1f,   1f,
                -0.5f, -0.8f, 0f,   1f, 0.7f, 0.7f, 0.7f,
                 0.5f, -0.8f, 0f,   1f, 0.7f, 0.7f, 0.7f,
                 0.5f,  0.8f, 0f,   2f, 0.7f, 0.7f, 0.7f,
                -0.5f,  0.8f, 0f,   2f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0f,   1f, 0.7f, 0.7f, 0.7f,

                // line
                -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
                 0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

                // malets
                0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
                0f,  0.4f, 1f, 1.75f, 1f, 0f, 0f
        };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFromResource(mContext, R.raw.simple_fragment_shader);

        int vertexShader   = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (D) {
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);

        aPositionLocation = glGetAttribLocation(program, A_POSITION); // find simple_gragment_shader.glsl
        aColorLocation    = glGetAttribLocation(program, A_COLOR);    // find simple_vertex_shader.glsl
        uMatrixLocation   = glGetUniformLocation(program, U_MATRIX);

        vertexData.position(0);
        // tell OpenGL find data for a_Position in the buffer vertexData.
        // MUST VERY CAREFUL!
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        glEnableVertexAttribArray(aColorLocation);

        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) { // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else { // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
//        MatrixHelper.perspectiveM(projectionMatrix , 45, (float)width / (float)height, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6); // start from 0, and read in six vertices

        glDrawArrays(GL_LINES, 6, 2);

        glDrawArrays(GL_POINTS, 8, 1);

        glDrawArrays(GL_POINTS, 9, 1);
    }

    public GLSurfaceView getGLSurfaceView() {
        return mSurfaceView;
    }
}
