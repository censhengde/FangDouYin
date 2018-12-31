#extension GL_OES_EGL_image_external : require
//SurfaceTexture比较特殊

//设置float数据的精度，这里设为中等。
precision mediump float;

//采样点坐标，只要varying vec2 aCoord这一句和camera_verte中的一样就能传过来。
varying vec2 aCoord;

//采样器 因为是用SurfaceTexture，所以选用samplerExternalOES
uniform samplerExternalOES fTexture;

void main(){
    //gl_FragColor:内置变量 接收像素值
    // texture2D：采样器 采集 aCoord的像素
    //赋值给 gl_FragColor 就可以了
  gl_FragColor=texture2D(fTexture,aCoord);
}