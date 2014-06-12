package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKApiCommunityFull;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Алексей on 09.02.14.
 */
public class SecondActivity extends BaseListActivity {
    ArrayList<VKApiCommunityFull> list;
    GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<VKApiCommunityFull>();
        adapter = new GroupAdapter();
        setListAdapter(adapter);
        VKRequest request = new VKApiGroups().get(new VKParameters(new TreeMap<String, Object>() {
            {
                put("extended", 1);
                put("filter", "admin");
            }
        }));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiCommunityArray obj = (VKApiCommunityArray) response.parsedModel;
                list.clear();
                for (int i = 0; i < obj.size(); i++) {
                    list.add(obj.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VKError error) {
                String errorUserName = "имя не получено";
//Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            }
        });


    }

    protected class GroupAdapter extends BaseAdapter {

        Button groupNameButton;
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) view = View.inflate(SecondActivity.this, R.layout.group_item, null);
            VKApiCommunity currentGroup = (VKApiCommunityFull) getItem(i);
            final long currentGroupId = currentGroup.id;
            groupNameButton = (Button) view.findViewById(R.id.groupNameButton);
//
            //TextView groupName = (TextView) view.findViewById(R.id.group_name);
//            groupNameButton.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    Intent in = new Intent(SecondActivity.this, GroupActivity.class);
//                    in.putExtra("groudId", currentGroupId);
//                    startActivity(in);
//                    finish();
//                }
//            });
            groupNameButton.setText(currentGroup.name);
            groupNameButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent in = new Intent(SecondActivity.this, GroupActivity.class);
                    in.putExtra("groudId", currentGroupId);
                    startActivity(in);
                    //finish();
                }
            });
//            ((TextView) view.findViewById(R.id.type_of_group)).setText(currentGroup.type);
            ImageView groupImage = (ImageView) view.findViewById(R.id.group_image);
            UrlImageViewHelper.setUrlDrawable(groupImage, currentGroup.photo_100);
            return view;

        }
    }


}
