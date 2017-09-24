package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class OldFirstView extends AppCompatActivity {

    private Button btn;
    private Button btn2;
    private Intent settingintent;
    SharedPreferences pref;
    private BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_first_view);
        backPressCloseHandler = new BackPressCloseHandler(this); //뒤로버튼 종료

        pref = getSharedPreferences("pref", MODE_PRIVATE); // Shared Preference를 불러옵니다.
        btn = (Button) findViewById(R.id.ofv_load_btn);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pref.getBoolean("FP", true)) {
                    Intent intent = new Intent(getApplicationContext(), Finger.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConfirmPatternActivity.class);
                    intent.putExtra("preActivity","OldFirstView");
                    startActivity(intent);
                }
            }
        });


        //설정버튼
        btn2 = (Button) findViewById(R.id.ofv_setting_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingintent = new Intent(getApplicationContext(), Setting.class);
                startActivity(settingintent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
        /*
        am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        am.restartPackage(getPackageName());*/
    }


}