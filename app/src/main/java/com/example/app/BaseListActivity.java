package com.example.app;

import android.app.ListActivity;
import android.content.Intent;

import com.vk.sdk.VKUIHelper;

/**
 * Created by Алексей on 15.02.14.
 */
public class BaseListActivity extends ListActivity {
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
