package com.example.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKSdk.initialize(new VKSdkListener() {
            @Override
            public void onAcceptUserToken(VKAccessToken token) {
                super.onAcceptUserToken(token);
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCaptchaError(VKError captchaError) {

            }

            @Override
            public void onTokenExpired(VKAccessToken expiredToken) {
                String [] scopes=new String[]{"notify","friends","photos","audio","video","docs","pages","wall","groups","stats"};
                VKSdk.authorize(scopes);

            }

            @Override
            public void onAccessDenied(VKError authorizationError) {
                onTokenExpired(null);
            }

            @Override
            public void onReceiveNewToken(VKAccessToken newToken) {
                super.onReceiveNewToken(newToken);
                newToken.saveTokenToSharedPreferences(MainActivity.this,"accountToken");
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
                finish();
            }
        }, "3984899",VKAccessToken.tokenFromSharedPreferences(MainActivity.this,"accountToken"));



        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LoginFragment extends Fragment {

        public LoginFragment() {
        }
        Button authButton;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            authButton = (Button) view.findViewById(R.id.authbutton);
            authButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String [] scopes=new String[]{"notify","friends","photos","audio","video","docs","pages","wall","groups","stats"};
                    VKSdk.authorize(scopes,true,true);
                }
            });
        }
    }



}
