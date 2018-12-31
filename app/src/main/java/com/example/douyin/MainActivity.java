package com.example.douyin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.douyin.R;
import android.widget.RadioGroup;

import com.example.douyin.widget.DouYinView;
import com.example.douyin.widget.RecordButton;


public class MainActivity extends AppCompatActivity {


    private DouYinView mDouyinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDouyinView =(DouYinView) findViewById(R.id.douyinView);

        RecordButton recordButton = (RecordButton) findViewById(R.id.btn_record);
        recordButton.setOnRecordListener(new RecordButton.OnRecordListener() {
            /**
             * 开始录制
             */
            @Override
            public void onRecordStart() {
                mDouyinView.startRecord();
            }

            /**
             * 停止录制
             */
            @Override
            public void onRecordStop() {
                mDouyinView.stopRecord();
            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_speed);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            /**
             * 选择录制模式
             * @param group
             * @param checkedId
             */
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_extra_slow: //极慢
                        mDouyinView.setSpeed(DouYinView.Speed.MODE_EXTRA_SLOW);
                        break;
                    case R.id.rb_slow:
                        mDouyinView.setSpeed(DouYinView.Speed.MODE_SLOW);
                        break;
                    case R.id.rb_normal:
                        mDouyinView.setSpeed(DouYinView.Speed.MODE_NORMAL);
                        break;
                    case R.id.rb_fast:
                        mDouyinView.setSpeed(DouYinView.Speed.MODE_FAST);
                        break;
                    case R.id.rb_extra_fast: //极快
                        mDouyinView.setSpeed(DouYinView.Speed.MODE_EXTRA_FAST);
                        break;
                }
            }
        });








    }



    


}
