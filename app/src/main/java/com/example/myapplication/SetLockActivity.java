package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.github.ajalt.reprint.core.Reprint;

public class SetLockActivity extends BaseActivity {

    public static Boolean FPcheck;
    public static Boolean PTcheck;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock);

        final Switch FPsb = (Switch) findViewById(R.id.FPsb);
        final Switch PTsb = (Switch) findViewById(R.id.PTsb);
        pref = getSharedPreferences("pref", MODE_PRIVATE); // Shared Preference를 불러옵니다.

        // 저장된 값들을 불러옵니다.
        FPcheck = pref.getBoolean("FP", false);

        PTcheck = pref.getBoolean("PT", true);

        FPsb.setChecked(FPcheck);
        PTsb.setChecked(PTcheck);

        FPsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //단말기에 등록된 지문이 존재한다면
                if (Reprint.hasFingerprintRegistered()) {
                    Toast.makeText(getApplicationContext(), "단말기에 등록된 지문을 이용합니다.", Toast.LENGTH_LONG).show();

                    //지문 버튼 활성화 후 true값 저장
                    FPsb.setChecked(true);
                    PTsb.setChecked(false);
                    FPcheck = true;
                    PTcheck = false;
                    //System.out.println("지문 o - 지문은 : " + FPcheck.toString() + "패턴은 : " + PTcheck.toString());


                } else {
                    Toast.makeText(getApplicationContext(), "지문을 먼저 등록 후 사용해주세요.", Toast.LENGTH_LONG).show();

                    FPsb.setChecked(false);
                    PTsb.setChecked(true);

                    FPcheck = false;
                    PTcheck = true;
                    //System.out.println("지문 x - 지문은 : " + FPcheck.toString() + "패턴은 : " + PTcheck.toString());
                }
            }
        });

        PTsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FPsb.setChecked(false);
                PTsb.setChecked(true);

                FPcheck = false;
                PTcheck = true;

                //System.out.println("패턴눌렀을때 - 지문은 : " + FPcheck.toString() + "패턴은 : " + PTcheck.toString());
            }
        });

    }

    public void onStop() { // 어플리케이션이 화면에서 사라질때
        super.onStop();

        pref = getSharedPreferences("pref", MODE_PRIVATE); // UI 상태를 저장합니다.
        SharedPreferences.Editor editor = pref.edit(); // Editor를 불러옵니다.

        // 저장할 값들을 입력합니다.
        editor.putBoolean("FP", FPcheck);
        editor.putBoolean("PT", PTcheck);

        editor.commit(); // 저장합니다.

    }
}