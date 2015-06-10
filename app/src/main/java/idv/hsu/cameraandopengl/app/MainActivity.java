package idv.hsu.cameraandopengl.app;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import java.util.List;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean D = false;

    // Camera
    private Camera mCamera;
    private static int mPreviewWidth;
    private static int mPreviewHeight;
    private byte[] mPreviewBuffer = null;
    private boolean mPreviewRunning = false;
    private ViewGroup mRootViewGroup = null;
    private CameraSurfaceView mCameraSurfaceView = null;
    private final Camera.PreviewCallback mCameraCallback =
            new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    if (camera != null) {
                        camera.addCallbackBuffer(mPreviewBuffer);
                        camera.setPreviewCallbackWithBuffer(this);
                    }
                }
            };

    // OpenGL ES
    private MainSurfaceViewRenderer mRenderer;
    private GLSurfaceView mGLSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int numCam = Camera.getNumberOfCameras();
        Log.d(TAG, "numCam: " + numCam);
        if (numCam > 0) {
            try {
                mCamera = Camera.open(0);
            } catch (RuntimeException re) {
                Log.e(TAG, "can't find camera");
            }
        }

        mRootViewGroup = (ViewGroup) findViewById(android.R.id.content);
        mCameraSurfaceView = new CameraSurfaceView(this, mCamera);
        mRootViewGroup.addView(mCameraSurfaceView);

        mRenderer = new MainSurfaceViewRenderer(this);
        mGLSurface = mRenderer.getGLSurfaceView();
        mGLSurface.setZOrderMediaOverlay(true);
        mGLSurface.setVisibility(View.VISIBLE);
        mGLSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mRootViewGroup.addView(mGLSurface);

        cameraPreview();

        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();

        cameraPreviewStop();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void cameraPreview() {
        Log.d(TAG, "cameraPreview");
        if (mCamera != null) {
            cameraSetup();
            cameraCallback();
            mCamera.startPreview();
            mPreviewRunning = true;
        } else {
            Log.e(TAG, "cameraPreview, camera NULL");
        }
    }

    private void cameraPreviewStop() {
        if ((mCamera != null) && (mPreviewRunning == true)) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
        }
        mPreviewRunning = false;
        System.gc();
    }

    private void cameraSetup() {
        Camera.Parameters parameters= mCamera.getParameters();
        List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
        Camera.Size list = sizes.get(0);
        mPreviewWidth = list.width;
        mPreviewHeight = list.height;
        Log.d(TAG, "mPreviewWidth: " + list.width);
        Log.d(TAG, "mPreviewHeight: " + list.height);

        parameters.setPreviewFrameRate(30);
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);

        List<String> supportedFocusMode = parameters.getSupportedFocusModes();
        if (supportedFocusMode != null) {
            if (supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                Log.d(TAG, "Focus Mode: INFINITY");
            }
            if (supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                Log.d(TAG, "Focus Mode: AUTO");
            }
            if (supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                Log.d(TAG, "Focus Mode: CONTINUOUS PICTURE");
            }
            if (supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                Log.d(TAG, "Focus Mode: CONTINUOUS VIDEO");
            }
        }

        List<String> supportedWhiteBalance = parameters.getSupportedWhiteBalance();
        if (supportedWhiteBalance != null &&
            supportedWhiteBalance.contains(Camera.Parameters.WHITE_BALANCE_AUTO)) {
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            Log.d(TAG, "White Balance Mode: AUTO");
        }

        try {
            mCamera.setParameters(parameters);
        } catch (RuntimeException re) {
            re.printStackTrace();
            Log.e(TAG, "Unable to set Camera parameters");

        }
    }

    private void cameraCallback() {
        int bufferSize = 0;
        int pformat;
        int bitsPerPixel;

        pformat = mCamera.getParameters().getPreviewFormat();

        PixelFormat info = new PixelFormat();
        PixelFormat.getPixelFormatInfo(pformat, info);
        bitsPerPixel = info.bitsPerPixel;

        bufferSize = mPreviewWidth * mPreviewHeight * bitsPerPixel / 8;
        mPreviewBuffer = null;
        mPreviewBuffer = new byte[bufferSize + 4096];

        mCamera.addCallbackBuffer(mPreviewBuffer);
        mCamera.setPreviewCallbackWithBuffer(mCameraCallback);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
