package com.wfly.kui.artest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.wfly.kui.artest.utils.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** http://www.2cto.com/kf/201506/404366.html
 * Created by Administrator on 2016/10/22.
 */

public class EffectRender implements GLSurfaceView.Renderer {
    public static final String TAG="EffectRender";
    /*vertex for render*/
    private final float vertices[]={
            -1f,-1f,
            1f,-1f,
            -1f,1f,
            1f,1f,

            -1f,0f,
            0f,0f,
            -1f,1f,
            0f,1f,

            0f,0f,
            1f,0f,
            0f,1f,
            1f,1f,
    };
    /*texture map vertex coordinate*/
    private final float texturevertices[]={
            0f,1f,
            1f,1f,
            0f,0f,
            1f,0f,

            0f,1f,
            1f,1f,
            0f,0f,
            1f,0f,

            0f,1f,
            1f,1f,
            0f,0f,
            1f,0f,

    };
    private FloatBuffer verticesBuf;
    private FloatBuffer textureVerticesBuf;
    private Activity holder;
    private String vertexShader;
    private String fragmentShader;
    private int aPositionHandle;
    private int uTextureHandle;
    private int aTexPositionHandle;
    private int programe;
    private Bitmap bitmap;
    private int textures[]=new int[2];

    private EffectContext effectContext;
    private Effect effect;

    public EffectRender(Activity activity){
        this.holder=activity;
    }
    private void initFloatBuffer(){
        ByteBuffer byteBuffer=ByteBuffer.allocateDirect(vertices.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        verticesBuf=byteBuffer.asFloatBuffer();
        verticesBuf.put(vertices);
        verticesBuf.position(0);

        byteBuffer=ByteBuffer.allocateDirect(texturevertices.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureVerticesBuf=byteBuffer.asFloatBuffer();
        textureVerticesBuf.put(texturevertices);
        textureVerticesBuf.position(0);
    }

    private void generateTexture(){
        bitmap= GLUtils.loadBitmapFromAssetsFile("test.jpg",holder.getResources());

        GLES20.glGenTextures(2,textures,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
    }
    private void applyEffectForTexture(){
        effect=effectContext.getFactory().createEffect(EffectFactory.EFFECT_BRIGHTNESS);
        effect.setParameter("brightness",4.0f);
        effect.apply(textures[0],bitmap.getWidth(),bitmap.getHeight(),textures[1]);
    }
    private void drawTexture(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        GLES20.glUseProgram(programe);
        GLES20.glDisable(GLES20.GL_BLEND);

        aPositionHandle=GLES20.glGetAttribLocation(programe,"aPosition");
        aTexPositionHandle=GLES20.glGetAttribLocation(programe,"aTexPosition");
        uTextureHandle=GLES20.glGetUniformLocation(programe,"uTexture");

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glVertexAttribPointer(aPositionHandle,2,GLES20.GL_FLOAT,false,0,verticesBuf);
        GLES20.glEnableVertexAttribArray(aPositionHandle);


        GLES20.glVertexAttribPointer(aTexPositionHandle,2,GLES20.GL_FLOAT,false,0,textureVerticesBuf);
        GLES20.glEnableVertexAttribArray(aTexPositionHandle);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[1]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,4,4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,8,4);

    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        /*set back groud color*/
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        generateTexture();
        initFloatBuffer();
        effectContext=EffectContext.createWithCurrentGlContext();
        applyEffectForTexture();
        vertexShader=GLUtils.loadShaderFromAssetsFile("vertexshader.vs",holder.getResources());
        fragmentShader=GLUtils.loadShaderFromAssetsFile("fragmentshader.vs",holder.getResources());
        Log.e(TAG,fragmentShader);
        programe=GLUtils.createPrograme(vertexShader,fragmentShader);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        drawTexture();
    }
}
