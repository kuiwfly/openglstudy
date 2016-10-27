package com.wfly.kui.artest;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    private GLSurfaceView glSurfaceView;
    private EffectRender glRender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*set full screen window*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*hide title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        glSurfaceView=new GLSurfaceView(this  );
        setContentView(glSurfaceView);
        glSurfaceView.setEGLContextClientVersion(2);
        glRender=new EffectRender(this);
        glSurfaceView.setRenderer(glRender);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
