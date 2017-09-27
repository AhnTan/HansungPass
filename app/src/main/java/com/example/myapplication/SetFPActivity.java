package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class SetFPActivity extends BaseActivity {

    Intent preIntent;
    int pre = 0;

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
}