package idv.hsu.cameraandopengl.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.*;

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
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(),
                resourceId, options);

        if (bitmap == null) {
            if (D) {
                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            }

            glDeleteTextures(1, textureObjectIds, 0);
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        return 0;
    }
}
