package com.example.myapplication;

import android.os.Bundle;

import com.example.myapplication.patternlock.PatternView;
import com.example.myapplication.util.PatternLockUtils;
import com.example.myapplication.util.PreferenceContract;
import com.example.myapplication.util.PreferenceUtils;
import com.example.myapplication.util.ThemeUtils;

import java.util.List;

public class ConfirmPatternActivity extends com.example.myapplication.patternlock.ConfirmPatternActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isStealthModeEnabled() {
        return !PreferenceUtils.getBoolean(PreferenceContract.KEY_PATTERN_VISIBLE, PreferenceContract.DEFAULT_PATTERN_VISIBLE, this);
    }

    @Override
    public boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        return PatternLockUtils.isPatternCorrect(pattern, this);
    }
}