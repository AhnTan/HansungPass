package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.util.AppUtils;
import com.example.myapplication.util.PatternLockUtils;
import com.example.myapplication.util.ThemeUtils;

import java.util.List;

import com.example.myapplication.patternlock.PatternView;

public class SetPatternActivity extends com.example.myapplication.patternlock.SetPatternActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        AppUtils.setActionBarDisplayUp(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppUtils.navigateUp(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        PatternLockUtils.setPattern(pattern, this);
    }
}
