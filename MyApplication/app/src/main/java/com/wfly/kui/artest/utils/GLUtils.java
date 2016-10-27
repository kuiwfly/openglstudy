package com.wfly.kui.artest.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.spec.ECField;

/**
 * Created by Administrator on 2016/10/22.
 */

public class GLUtils {
    public static final String TAG="GLUtils";
    public static int checkGLErrors(String op){
        int error=GLES20.GL_NO_ERROR;
        while((error=GLES20.glGetError())!=GLES20.GL_NO_ERROR){
            Log.e(TAG,op+":error id:"+error);
            //throw new RuntimeException(op+":error id:"+error);
        }
        return error;
    }
    /*load shader by script*/
    public static int loadShader(int shadetype,String source){
        /*create shader by shade type, and save shade id. if id ==0 ,it indicates create failed*/
        int shader = GLES20.glCreateShader(shadetype);
        if(shader!=0){
            //load script for shader
            GLES20.glShaderSource(shader,source);
            //compile shader script
            GLES20.glCompileShader(shader);
            int[] compiled=new int[1];
            GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                Log.e(TAG,"compile shader failed");
                Log.e(TAG,GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }else{
            checkGLErrors("create shader");
        }
        return shader;
    }
    /*create shader programe*/
    public static int createPrograme(String vertexSource,String fragmentSource){
        //1. create programe
        int programe=GLES20.glCreateProgram();
        if(programe!=0){
            //2:load vertex shader
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
            if(vertexShader==0){
                GLES20.glDeleteProgram(programe);;
                return 0;
            }
            //3: load fragment shader
            int fragementShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
            if(fragementShader==0){
                GLES20.glDeleteShader(vertexShader);
                GLES20.glDeleteProgram(programe);
                return 0;
            }
            //4: attach vertex shader
            GLES20.glAttachShader(programe,vertexShader);
            if(checkGLErrors("attach vertex shader")!=GLES20.GL_NO_ERROR){
                GLES20.glDeleteShader(vertexShader);
                GLES20.glDeleteShader(fragementShader);
                GLES20.glDeleteProgram(programe);
                return 0;
            };
            //5: attach fragment shader
            GLES20.glAttachShader(programe,fragementShader);
            if(checkGLErrors(("attach fragement shader"))!=GLES20.GL_NO_ERROR){
                GLES20.glDeleteShader(vertexShader);
                GLES20.glDeleteShader(fragementShader);
                GLES20.glDeleteProgram(programe);
                return 0;
            };
            //5: link program
            GLES20.glLinkProgram(programe);
            int[] linkStatus= new int[1];
            GLES20.glGetProgramiv(programe,GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!=GLES20.GL_TRUE){
                Log.e(TAG,"link programe failed:"+GLES20.glGetProgramInfoLog(programe));
                GLES20.glDeleteProgram(programe);
                programe=0;
            }
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(fragementShader);
        }else{
            checkGLErrors("create programe");
        }
        return programe;
    }
    public static String loadShaderFromAssetsFile(String fileName, Resources resources){
        String result = null;
        try {
            InputStream is = resources.getAssets().open(fileName);
            int ch=0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch=is.read())!=-1){
                baos.write(ch);
            }
            byte[] buffer=baos.toByteArray();
            baos.close();
            result=new String(buffer,"UTF-8");
            result=result.replaceAll("\\r\\n","\n");
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public static Bitmap loadBitmapFromAssetsFile(String fileName,Resources resources){
        Bitmap bitmap=null;
        try{
            InputStream is=resources.getAssets().open(fileName);
            BitmapFactory.Options options=new BitmapFactory.Options();
            bitmap=BitmapFactory.decodeStream(is,null,options);
            if(bitmap==null){
                Log.e(TAG,"load image from assets file failed:"+fileName);
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
