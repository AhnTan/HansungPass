package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ajalt.reprint.core.Reprint;

public class RegisterDialog extends Dialog {

    private TextView mTitleView;
    private TextView mContentView;
    private Button FPdialogBtn;
    private Button PTdialogBtn;
    private String mTitle;
    private String mContent;

    private View.OnClickListener FPClickListener;
    private View.OnClickListener PTClickListener;

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

        mTitleView = (TextView) findViewById(R.id.txt_title);
        FPdialogBtn = (Button) findViewById(R.id.btn_FP); //지문
        PTdialogBtn = (Button) findViewById(R.id.btn_PT); //패턴

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
        Reprint.initialize(getContext());
        if (Reprint.hasFingerprintRegistered())
            FPdialogBtn.setText("단말기에 등록된 지문으로 이용하기");
        else {
            if (Reprint.isHardwarePresent())
                FPdialogBtn.setText("지문 등록하러 가기");
            else FPdialogBtn.setText("단말기가 지문인식 기능을 지원하지 않습니다.");

        }

        // 클릭 이벤트 셋팅
        if (FPClickListener != null && PTClickListener != null) {
            FPdialogBtn.setOnClickListener(FPClickListener);
            PTdialogBtn.setOnClickListener(PTClickListener);
        } else if (FPClickListener != null
                && PTClickListener == null) {
            FPdialogBtn.setOnClickListener(FPClickListener);
        } else {

        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public RegisterDialog(Context context, String title,
                          View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.FPClickListener = singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public RegisterDialog(Context context, String title,
                          String content, View.OnClickListener leftListener,
                          View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent = content;
        this.FPClickListener = leftListener;
        this.PTClickListener = rightListener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getContext(), "등록에 실패했습니다.\n다시 등록해주세요.", Toast.LENGTH_LONG).show();
    }

}
