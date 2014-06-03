package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.*;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.model.VKApiArray;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.ArrayList;
import java.util.TreeMap;


public class MembersListActivity extends BaseListActivity {

    private ArrayList<VKApiUserFull> usersList;
    private MembersListAdapter adapter;

    class MembersListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return usersList.size();
        }

        @Override
        public Object getItem(int i) {
            return usersList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return usersList.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(MembersListActivity.this, R.layout.member_item, null);
            }
            VKApiUserFull user = (VKApiUserFull) getItem(i);
            TextView memberName = (TextView) view.findViewById(R.id.userNameInMembersItem_a);
            ImageView memberAvatar = (ImageView) view.findViewById(R.id.userInGroupAvatar_k);
            memberName.setText(user.first_name+" "+user.last_name);
            UrlImageViewHelper.setUrlDrawable(memberAvatar, user.photo_100);

            return view;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        adapter = new MembersListAdapter();
        usersList = new ArrayList<VKApiUserFull>();
        setListAdapter(adapter);
        Bundle bundle = new Bundle(intent.getExtras());
        final Long groupId = bundle.getLong("group_id");

        VKRequest request = new VKApiGroups().getMembers(new VKParameters(new TreeMap<String, Object>() {
            {
                put("group_id", groupId.toString());
                put("fields", "photo_100");
                put("sort", "id_asc");
            }
        }));
        request.executeWithListener(new VKRequest.VKRequestListener(){
            @Override
            public void onComplete(VKResponse response) {
//                VKUsersArray members = (VKUsersArray) response.parsedModel;
                VKUsersArray members = new VKUsersArray();
                try {
                    members.parse(response.json);
                } catch (Exception e) {
                    System.out.println("Exception");
                }
                usersList.clear();
                for (int i = 0; i < members.size(); i++) {
                    usersList.add(members.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
             public void onError(VKError error) {
                String strError = "fignya";
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
