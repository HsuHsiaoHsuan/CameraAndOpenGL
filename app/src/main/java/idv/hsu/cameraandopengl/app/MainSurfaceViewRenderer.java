package idv.hsu.cameraandopengl.app;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import idv.hsu.cameraandopengl.app.utils.TextResourceReader;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by freeman on 2015/6/10.
 */
public class MainSurfaceViewRenderer implements GLSurfaceView.Renderer/*, GLSurfaceView.EGLContextFactory*/ {
    private static final String TAG = MainSurfaceViewRenderer.class.getSimpleName();
    private static final boolean D = false;

    private Context mContext;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private float[] tableVertices = {
            0f,  0f,
            0f, 14f,
            9f, 14f,
            9f,  0f
    };
    private float[] tableVerticesWithTriangles = {
            // first
            0f,  0f,
            9f, 14f,
            0f, 14f,

            // second
            0f,  0f,
            9f,  0f,
            9f, 14f,

            // line
            0f,  7f,
            9f,  7f,

            // malets
            4.5f,2f,
            4.5f,12f
    };
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private GLSurfaceView mSurfaceView;

    public MainSurfaceViewRenderer(Context context) {
        mContext = context;
        mSurfaceView = new GLSurfaceView(context);
        mSurfaceView.setEGLContextClientVersion(2);
//        mSurfaceView.setEGLContextFactory(this);
//        mSurfaceView.setEGLConfigChooser(new ConfigChooser(5, 6, 5, 0, 16, 0));
        mSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 0);
        mSurfaceView.setRenderer(this);

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                               .order(ByteOrder.nativeOrder())
                               .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }
//
//    @Override
//    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
//        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
//        int[] attr = {
//            EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE
//        };
//
//        return egl.eglCreateContext(display, eglConfig,EGL10.EGL_NO_CONTEXT, attr);
//    }
//
//    @Override
//    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
//        egl.eglDestroyContext(display, context);
//    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFromResource(mContext, R.raw.simple_fragment_shader);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public GLSurfaceView getGLSurfaceView() {
        return mSurfaceView;
    }

//    private static class ConfigChooser implements GLSurfaceView.EGLConfigChooser {
//        protected int mRedSize;
//        protected int mGreenSize;
//        protected int mBlueSize;
//        protected int mAlphaSize;
//        protected int mDepthSize;
//        protected int mStencilSize;
//        private int[] mValue = new int[1];
//
//        public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
//            mRedSize = r;
//            mGreenSize = g;
//            mBlueSize = b;
//            mAlphaSize = a;
//            mDepthSize = depth;
//            mStencilSize = stencil;
//        }
//
//        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
//            if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
//                return mValue[0];
//            }
//
//            return defaultValue;
//        }
//
//        private EGLConfig getMatchingConfig(EGL10 egl, EGLDisplay display, int[] configAttribs) {
//            int[] num_config = new int[1];
//            egl.eglChooseConfig(display, configAttribs, null, 0, num_config);
//
//            int numConfigs = num_config[0];
//            if (numConfigs <= 0) {
//                throw new IllegalArgumentException("No matching EGL configs");
//            }
//
//            EGLConfig[] configs = new EGLConfig[numConfigs];
//            egl.eglChooseConfig(display, configAttribs, configs, numConfigs, num_config);
//
//            return chooseConfig(egl, display, configs);
//        }
//
//        @Override
//        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eglDisplay) {
//            return null;
//        }
//
//        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
//            for (EGLConfig config : configs) {
//                int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
//                int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
//
//                if (d < mDepthSize || s < mStencilSize) {
//                    continue;
//                }
//
//                int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
//                int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
//                int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
//                int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
//
//                if (r == mRedSize && g == mGreenSize && b == mBlueSize && a == mAlphaSize) {
//                    return config;
//                }
//            }
//            return  null;
//        }
//    }

}
