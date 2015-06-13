package idv.hsu.cameraandopengl.app.utils;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by freeman on 2015/6/11.
 */
public class ShaderHelper {
    private static final String TAG = ShaderHelper.class.getSimpleName();
    private static final boolean D = false;

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            if (D) {
                Log.w(TAG, "Could not create new shader.");
            }
            return 0;
        }

        glShaderSource(shaderObjectId, shaderCode); // upload the source code
        glCompileShader(shaderObjectId);            // compile the source code that was uploaded to shaderObjectId

        // check compile satus
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (D) {
            Log.v(TAG, "Results of compiling source: \n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
        }

        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId);

            if (D) {
                Log.w(TAG, "Compilation of shader failed.");
            }

            return 0;
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = glCreateProgram();

        if (programObjectId == 0) {
            if (D) {
                Log.w(TAG, "Could not create new progam");
            }
            return 0;
        }

        // attach shaders to program object
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        // join shaders together
        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        // check whether the link failed or succeeded
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        if (D) { // print the program info
            Log.v(TAG, "Results to linking pogram: \n" + glGetProgramInfoLog(programObjectId));
        }
        if (linkStatus[0] == 0) { // it it failed.
            glDeleteProgram(programObjectId);
            if (D) {
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }

        return programObjectId;
    }

    // before start to use it, validate it is valid for the current OpenGL state.
    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results to validating program: " + validateStatus[0] +
                   "\n Log:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }
}