package idv.hsu.cameraandopengl.app.utils;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by freeman on 2015/6/11.
 */
public class TextResourceReader {
    public static String readTextFromResource(Context context, int resId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Could not open resources: " + resId, ioe);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resId, nfe);
        }

        return body.toString();
    }
}
