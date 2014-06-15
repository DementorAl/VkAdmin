package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

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

    @Override
    public void onBackPressed() {
        finish();
    }

    public void showPopup(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public  void showPopupError (VKError error) {
        int errorCode = error.errorCode;
        switch (errorCode) {
            case 7: showPopup("Нет прав для выполнения этого действия");
                break;
            case 9: showPopup("Слишком много однотипных действий");
                break;
            case -105: showPopup("Проверьте подключение к интернету");
            default:
                showPopup("Неизвестная ошибка: " + errorCode);
        }

    }
}
