package com.example.douyin.widget;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.douyin.filter.CameraFilter;
import com.example.douyin.filter.ScreenFilter;
import com.example.douyin.recode.MediaRecorder;
import com.example.douyin.util.CameraHelper;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
/*
 * 三个实现方法已经在GLThread中的run()方法调用
 * */

public class DouYinRenderer implements GLSurfaceView.Renderer ,SurfaceTexture.OnFrameAvailableListener {
    private ScreenFilter mScreenFilter;
    private CameraHelper mCameraHelper;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private int[] mTextures;
    private DouYinView mView;
    private CameraFilter mCameraFilter;
    private MediaRecorder mMediaRecorder;

    public DouYinRenderer(DouYinView douYinView) {
        mView = douYinView;
    }

    /**
     * 创建画布
     *
     * @param gl
     * @param config
     */

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置摄像头，默认后置摄像头
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
         //准备好摄像头绘制的画布
        //通过opengl创建一个纹理id
        mTextures=new int[1];
        GLES20.glGenTextures(mTextures.length,mTextures,0);
        mSurfaceTexture=new SurfaceTexture(mTextures[0]);

        //关联SufaceTexture
        mSurfaceTexture.setOnFrameAvailableListener(this);


        //注意：必须在gl线程操作opengl
        mCameraFilter=new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());

        //渲染线程的EGL上下文
        EGLContext eglContext = EGL14.eglGetCurrentContext();
        mMediaRecorder = new MediaRecorder(mView.getContext(), "/storage/emulated/0/a.mp4", CameraHelper.HEIGHT, CameraHelper.WIDTH, eglContext);



    }

    /**
     * 画布改变，如横竖屏切换，home键返回
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //拿到摄像头数据传给filte
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width,height);
        mScreenFilter.onReady(width,height);
    }

    /**
     * 渲染，画画
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        /** 配置屏幕
         *
         * */
        //清理屏幕 :告诉opengl 需要把屏幕清理成什么颜色
        GLES20.glClearColor(0, 0, 0, 0);
        //执行上一个：glClearColor配置的屏幕颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 把摄像头的数据先输出来
        // 更新纹理，然后我们才能够使用opengl从SurfaceTexure当中获得数据 进行渲染
        mSurfaceTexture.updateTexImage();
        //surfaceTexture 比较特殊，在opengl当中 使用的是特殊的采样器 samplerExternalOES （不是sampler2D）
        //获得变换矩阵
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setMatrix(mtx);
       int fboId= mCameraFilter.onDrawFrame(mTextures[0]);
       //=======加滤镜效果=========

     //..................

// id  = 效果1.onDrawFrame(id);
        // id = 效果2.onDrawFrame(id);
        //....
        //加完之后再显示到屏幕中去

        //========从FBO中取得数据显示到GLSufaceView
        mScreenFilter.onDrawFrame(fboId);

        //从FBO中取得数据去进行录制
        mMediaRecorder.encodeFrame(fboId, mSurfaceTexture.getTimestamp());
    }


    /**
     * surfaceTexture 有一个有效的新数据的时候回调
     *
     * @param surfaceTexture
     */

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

        mView.requestRender();
    }

    public void onSufaceDestroyed(){
        mCameraHelper.stopPreview();
    }


    /**开启录制
     * @param speed
     */
    public void startRecord(float speed) {
        try {
            mMediaRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        mMediaRecorder.stop();
    }
}
