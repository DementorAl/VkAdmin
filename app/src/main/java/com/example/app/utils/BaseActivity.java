package com.example.app.utils;

import android.app.Activity;
import android.content.Intent;

import com.vk.sdk.VKUIHelper;

/**
 * Created by Алексей on 09.02.14.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
    }
}
