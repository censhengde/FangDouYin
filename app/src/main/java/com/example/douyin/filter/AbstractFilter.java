package com.example.douyin.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.example.douyin.util.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class AbstractFilter {

    protected FloatBuffer mGLVertexBuffer;
    protected FloatBuffer mGLTextureBuffer;

    //=====raw资源=================
    protected int mVertexShaderId;//顶点着色器
    protected int mFragmentShaderId;//片段着色器


    protected int mGLProgramId;

    //.vert中的变量
    protected int vPosition;//起始位置
    protected int vCoord;//顶点坐标
    protected int vMatrix;//变换矩阵

    //.frag中的变量
    protected int vTexture;


    protected int mOutputWidth;
    protected int mOutputHeight;


    public AbstractFilter(Context context,int vertexShaderId, int fragmentShaderId) {
        mVertexShaderId = vertexShaderId;
        mFragmentShaderId = fragmentShaderId;

        //=========顶点着色器缓冲区=============
        // 4个点 x，y = 4*2 float 4字节 所以 4*2*4=32个字节
        mGLVertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLVertexBuffer.clear();
        //OpenGL世界坐标
        float[] VERTEX = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f
        };
        mGLVertexBuffer.put(VERTEX);

        //=========片元着色器缓冲区=============

        mGLTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.clear();

        //
        float[] TEXTURE = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };
        mGLTextureBuffer.put(TEXTURE);

        initilize(context);
        initCoordinate();
    }


    /**获取.vert,.frag中变量索引
     * @param context
     */
    protected void initilize(Context context) {
        String vertexSharder = OpenGLUtils.readRawTextFile(context, mVertexShaderId);
        String framentShader = OpenGLUtils.readRawTextFile(context, mFragmentShaderId);
        mGLProgramId = OpenGLUtils.loadProgram(vertexSharder, framentShader);
        // 获得着色器中的 attribute 变量 position 的索引值
        vPosition = GLES20.glGetAttribLocation(mGLProgramId, "vPosition");
        vCoord = GLES20.glGetAttribLocation(mGLProgramId,
                "vCoord");
        vMatrix = GLES20.glGetUniformLocation(mGLProgramId,
                "vMatrix");
        // 获得Uniform变量的索引值
        vTexture = GLES20.glGetUniformLocation(mGLProgramId,
                "vTexture");
    }


    /**获取屏幕宽高
     * @param width
     * @param height
     */
    public void onReady(int width, int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    /**
     * 释放着色器程序
     */
    public void release() {
        GLES20.glDeleteProgram(mGLProgramId);
    }

    /**画画（本质就是给着色器传值）
     * @param textureId：纹理Id
     * @return 返回FBO纹理Id
     */
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);

        //使用着色器
        GLES20.glUseProgram(mGLProgramId);

        //传递坐标
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(vTexture, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        return textureId;
    }

    //修改坐标
    protected void initCoordinate() {

    }
}
