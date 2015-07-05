package idv.hsu.cameraandopengl.app.utils;

import android.content.Context;
import android.util.Log;

import static android.opengl.GLES20.glGenTextures;

/**
 * Created by freeman on 2015/6/26.
 */
public class TextureHelper {
    private static final String TAG = TextureHelper.class.getSimpleName();
    private static final boolean D = true;

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (D) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
        }
    }
}
