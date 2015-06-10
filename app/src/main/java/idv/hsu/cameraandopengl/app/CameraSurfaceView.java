package idv.hsu.cameraandopengl.app;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by freeman on 2015/6/5.
 */
public class CameraSurfaceView  extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraSurfaceView.class.getSimpleName();
    private static final boolean D = false;
    Camera mCamera;

    public CameraSurfaceView(Context context, Camera camera) {
        super(context);

        if (camera == null) {
            Log.d(TAG, "constructor, camera NULL");
        }
        mCamera = camera;

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
            }
        } catch (Exception exception) { }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }
}
