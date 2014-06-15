package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiWikiPage;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;

public class WikiListActivity extends BaseListActivity {
    private ArrayList<VKApiWikiPage> wikiList;
    private WikiPagesAdapter adapter;

    class WikiPagesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return wikiList.size();
        }

        @Override
        public Object getItem(int i) {
            return wikiList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return wikiList.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(WikiListActivity.this, R.layout.wiki_item, null);
            }
            VKApiWikiPage page = (VKApiWikiPage) getItem(i);
            final Button memberName = (Button) view.findViewById(R.id.WikiPageName);
            final VKApiWikiPage thisPage = (VKApiWikiPage) getItem(i);

            memberName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(WikiListActivity.this, WikiEditActivity.class);
                    in.putExtra("page_Id", (long)thisPage.id);
                    in.putExtra("owner_id", (long)thisPage.group_id);
                    in.putExtra("created_id", (long)thisPage.creator_id);
                    //ПРИ ИЗМЕНЕНИИ INTENTA НЕ ЗАБУДЬ ТОЧНО ТАК ЖЕ ВСЕ ПОМЕНЯТЬ В WikiEditActivity НА ОБРАБОТКЕ КНОПКИ ОТПРАВКИ НОВОГО ТЕКСТА

                    startActivity(in);
                    //finish();
                }
            });


            memberName.setText(page.title);
            return view;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        adapter = new WikiPagesAdapter();
        wikiList = new ArrayList<VKApiWikiPage>();
        try {
            setListAdapter(adapter);
        } catch (Exception e) {
            String message = e.getMessage();
            e.printStackTrace();
        }
        Bundle extras = intent.getExtras();
        final Long groupId =  extras.getLong("group_id");
        VKRequest request = new VKRequest("pages.getTitles",VKParameters.from(VKApiConst.GROUP_ID, groupId.toString()));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKList<VKApiWikiPage> wikiPages = new VKList<VKApiWikiPage>(response.json, VKApiWikiPage.class);

                for (int i=0; i<wikiPages.size(); i++) {
                    wikiList.add(wikiPages.get(i));
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onError(VKError error) {
                WikiListActivity.this.showPopupError(error);
                System.out.println("error");
                String errorGroup = "список вики страниц не получен";
            }
            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                System.out.println();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wiki, menu);
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
