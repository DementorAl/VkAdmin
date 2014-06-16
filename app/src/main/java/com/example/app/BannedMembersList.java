package com.example.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.ArrayList;

public class BannedMembersList extends BaseListActivity {
    private ArrayList<VKApiUserFull> users;
    private Long group_id;
    public class BannedMemberListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return users.get(position).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(BannedMembersList.this, R.layout.member_item, null);
            }
            final VKApiUserFull user = (VKApiUserFull) getItem(i);
            Button unban = (Button) view.findViewById(R.id.make_ban_button);
            ImageView memberAvatar = (ImageView) view.findViewById(R.id.userInGroupAvatar_k);
            TextView memberName = (TextView) view.findViewById(R.id.userNameInMembersItem_a);
            memberName.setText(user.first_name + " " + user.last_name);
            UrlImageViewHelper.setUrlDrawable(memberAvatar, user.photo_100);
            unban.setText("Разблокировать");
            unban.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKRequest request = new VKApiGroups().unbanUser(VKParameters.from(
                            "group_id", group_id.toString(),
                            "user_id", String.valueOf(user.id)
                    ));
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            users.remove(user);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                            super.attemptFailed(request, attemptNumber, totalAttempts);
                        }

                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            AlertDialog.Builder builder = new AlertDialog.Builder(BannedMembersList.this);
                            builder.setMessage("Не удалось");
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                    }
                });
            return view;
        }
    }
    BannedMemberListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        users = new ArrayList<VKApiUserFull>();
        Bundle extra = intent.getExtras();
        group_id = extra.getLong("group_id");
        adapter = new BannedMemberListAdapter();
        setListAdapter(adapter);
        VKRequest request = new VKApiGroups().getBanned(VKParameters.from(
                VKApiConst.GROUP_ID, String.valueOf(group_id),
                VKApiConst.FIELDS, "photo_100",
                VKApiConst.COUNT, "200"
                ));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKUsersArray usersList = new VKUsersArray();
                try {
                    usersList.parse(response.json);
                } catch (Exception e) {
//                    Заглушка
                }
                for (int i = 0; i < usersList.size(); i++) {
                    users.add(usersList.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.members_list, menu);
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
}
