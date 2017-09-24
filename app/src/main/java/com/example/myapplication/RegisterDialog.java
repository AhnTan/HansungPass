package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterDialog extends Dialog {

    private TextView mTitleView;
    private TextView mContentView;
    private Button mLeftButton;
    private Button mRightButton;
    private String mTitle;
    private String mContent;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    private Finger finger;
    private boolean finger_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_dialog);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_register_dialog);

        mTitleView = (TextView) findViewById(R.id.txt_title);
        mLeftButton = (Button) findViewById(R.id.btn_FP); //지문
        mRightButton = (Button) findViewById(R.id.btn_PT); //패턴

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
/*
        finger = new Finger();
        finger_check = finger.checkDeviceSpec();
        if(finger_check){
            Toast.makeText(getContext(),"true",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"false",Toast.LENGTH_SHORT).show();
        }*/

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public RegisterDialog(Context context, String title,
                          View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mLeftClickListener = singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public RegisterDialog(Context context, String title,
                          String content, View.OnClickListener leftListener,
                          View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent = content;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getContext(), "등록에 실패했습니다.\n다시 등록해주세요.", Toast.LENGTH_LONG).show();
    }

}
