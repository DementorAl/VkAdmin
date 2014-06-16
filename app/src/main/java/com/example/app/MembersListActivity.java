package com.example.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;


public class MembersListActivity extends BaseListActivity {

    private ArrayList<VKApiUserFull> usersList;
    private MembersListAdapter adapter;
    private Long group_id;

    public class MembersListAdapter extends BaseAdapter {

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
            final VKApiUserFull user = (VKApiUserFull) getItem(i);
            TextView memberName = (TextView) view.findViewById(R.id.userNameInMembersItem_a);
            Button make_ban = (Button) view.findViewById(R.id.make_ban_button);
            ImageView memberAvatar = (ImageView) view.findViewById(R.id.userInGroupAvatar_k);
            memberName.setText(user.first_name + " " + user.last_name);
            UrlImageViewHelper.setUrlDrawable(memberAvatar, user.photo_100);
            final long user_id = adapter.getItemId(i);
            final LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            make_ban.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View testView = layoutInflater.inflate(R.layout.ban_menu, null);
                    final PopupWindow ban_menu = new PopupWindow(testView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT,true);
                    ban_menu.showAtLocation(view, Gravity.CENTER, 0, 0);
                    final Button ban = (Button) testView.findViewById(R.id.ban_button);
                    final EditText ban_time = (EditText) testView.findViewById(R.id.end_ban_time);
                    final EditText ban_comment = (EditText) testView.findViewById(R.id.ban_reason_comment);
                    final RadioGroup ban_reason_chooser = (RadioGroup) testView.findViewById(R.id.ban_reason_chooser);
                    final Switch comment_visible = (Switch) testView.findViewById(R.id.ban_comment_visible);
                    Button cancel = (Button) testView.findViewById(R.id.cancel_ban_button);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ban_menu.dismiss();
                        }
                    });
                    ban.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Date date = new Date();
                            final long end_ban_time;
                            if (!ban_time.getText().toString().equals("")) {
                                end_ban_time = date.getTime() + Long.valueOf(ban_time.getText().toString())*3600000;
                            }
                            else {
                                end_ban_time = 0;
                            }
                            VKParameters params = new VKParameters();
                            params.put("group_id", group_id.toString());
                            params.put("user_id", String.valueOf(user_id));
                            if (end_ban_time > 0) {
                                params.put("end_date",String.valueOf(end_ban_time/1000));
                            }
                            if (ban_reason_chooser.getCheckedRadioButtonId() < 0) {
                                params.put("reason", "0");
                            }
                            else {
                                RadioButton checkedButton = (RadioButton) testView.findViewById(ban_reason_chooser.getCheckedRadioButtonId());
                                params.put("reason", checkedButton.getHint());
                            }
                            if (!ban_comment.getText().toString().equals("")) {
                                params.put("comment", ban_comment.getText().toString());
                            }
                            if (comment_visible.isChecked()) {
                                params.put("comment_visible", "1");
                            }
                            else {
                                params.put("comment_visible", "0");
                            }
                            VKRequest request = new VKApiGroups().banUser(params);
                            request.executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    try {
                                        ban_menu.dismiss();
                                        if ((Integer) response.json.get("response") != (1)) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MembersListActivity.this);
                                            builder.setMessage("Не удалось");
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                        else {
                                            usersList.remove(user);
                                            adapter.notifyDataSetChanged();
                                        }
                                    } catch (Exception e) {
//                                        заглушка
                                    }
                                }

                                @Override
                                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                                    super.attemptFailed(request, attemptNumber, totalAttempts);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MembersListActivity.this);
                                    builder.setMessage("Не удалось");
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    ban_menu.dismiss();
                                }

                                @Override
                                public void onError(VKError error) {
                                    super.onError(error);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MembersListActivity.this);
                                    builder.setMessage("Не удалось: " + error.apiError.errorMessage);
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    ban_menu.dismiss();
                                }
                            });

                        }
                    });
                }
            });
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
        group_id = bundle.getLong("group_id");
        VKRequest request = new VKApiGroups().getMembers(new VKParameters(new TreeMap<String, Object>() {
            {
                put("group_id", group_id.toString());
                put("fields", "photo_100");
                put("sort", "id_asc");
            }
        }));
        request.executeWithListener(new VKRequest.VKRequestListener(){
            @Override
            public void onComplete(VKResponse response) {
                VKUsersArray members = new VKUsersArray();
                try {
                    members.parse(response.json);
                } catch (Exception e) {
//                    Error
                }
                usersList.clear();
                for (int i = 0; i < members.size(); i++) {
                    usersList.add(members.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VKError error) {
                String strError = "Error";
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
