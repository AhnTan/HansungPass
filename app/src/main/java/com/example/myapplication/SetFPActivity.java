package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SetFPActivity extends AppCompatActivity {

    Intent preIntent;
    int pre=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_fp);
        preIntent = getIntent();
        Button setFPbtn = (Button) findViewById(R.id.setFPbtn);
        setFPbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
            }
        });

    }
/*
    @Override
    public void onBackPressed() {
        Reprint.initialize(this);
        Intent intent = new Intent(getApplicationContext(), OldFirstView.class);
        pre = preIntent.getExtras().getInt("preFPActivity");
        if (pre==123) {
            if (Reprint.hasFingerprintRegistered()) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Toast.makeText(getApplicationContext(), "등록에 성공했습니다.", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            else
                Toast.makeText(getApplicationContext(), "등록에 실패했습니다.\n다시 등록해주세요.", Toast.LENGTH_LONG).show();
        }

        super.onBackPressed();
    }*/
    }