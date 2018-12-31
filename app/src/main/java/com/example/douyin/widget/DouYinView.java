package com.example.douyin.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class DouYinView extends GLSurfaceView {

    //默认正常速度
    private Speed mSpeed = Speed.MODE_NORMAL;

    public enum Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }


    private DouYinRenderer mDouYinRenderer;
    public DouYinView(Context context) {
        this(context, null);
    }

    public DouYinView(Context context, AttributeSet attrs) {
        super(context, attrs);
/**
 * 配置GLSurfaceView
 */
        //1.配置EGL版本
        setEGLContextClientVersion(2);
       //2.s设置渲染器
        setRenderer(new DouYinRenderer(this));
        //3.设置按需渲染 当我们调用 requestRender 请求GLThread 回调一次 onDrawFrame
        // 连续渲染 就是自动的回调onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mDouYinRenderer.onSufaceDestroyed();
    }

    public void setSpeed(Speed speed){
        mSpeed = speed;
    }



    public void startRecord() {
        float speed = 1.f;
        switch (mSpeed) {
            case MODE_EXTRA_SLOW:
                speed = 0.3f;
                break;
            case MODE_SLOW:
                speed = 0.5f;
                break;
            case MODE_NORMAL:
                speed = 1.f;
                break;
            case MODE_FAST:
                speed = 1.5f;
                break;
            case MODE_EXTRA_FAST:
                speed = 3.f;
                break;
        }
        mDouYinRenderer.startRecord(speed);
    }

    public void stopRecord() {
        mDouYinRenderer.stopRecord();
    }

}
