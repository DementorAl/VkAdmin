package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiWikiPage;

import org.json.JSONException;
import org.json.JSONObject;


public class WikiEditActivity extends BaseActivity {
    private long pageId;
    private long ownerId; //group_id
    private long createdId;
    private EditText wikiPageView = null;
    private TextView titleWikiPageViev = null;
    private Intent intentForRestartPage = null;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_edit);
        final Intent intent = getIntent();
        intentForRestartPage = intent;
        Bundle extras = intent.getExtras();
        pageId = extras.getLong("page_Id");
        ownerId = extras.getLong("owner_id");
        createdId = extras.getLong("created_id");
        title = extras.getString("title");
        final VKRequest request = new VKRequest("pages.get", VKParameters.from(
                "owner_id", "-" + String.valueOf(ownerId),
                "page_id", String.valueOf(pageId),
                "global", "1",
                "site_preview", "0",
                "title", title,
//                "need_source", '1',
                "need_html", "0"
//                VKApiConst.VERSION

        ));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiWikiPage wikiPage = new VKApiWikiPage();
                try {
                    wikiPage = wikiPage.parse(response.json.getJSONObject("response"));
                } catch (JSONException e) {

                }
                wikiPageView = (EditText) findViewById(R.id.wikiPage);
                wikiPageView.setText(wikiPage.source);

                titleWikiPageViev = (TextView) findViewById(R.id.titleWikiPage);

                titleWikiPageViev.setText(wikiPage.title);


            }

            @Override
            public void onError(VKError error) {
                System.out.println("error");
                String errorGroup = "вики страница не получен";
                EditText wikiPageView = (EditText) findViewById(R.id.wikiPage);
                wikiPageView.setText(errorGroup + '\n'
                        + "ownerId=" + ownerId + '\n' +
                        "pageId=" + pageId + '\n' +
                        title + '\n' +
                        "--------------------" +
                        request.toString() + '\n' +
                        "=====================" +
                        error.toString());
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                System.out.println();
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveWikiPageButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = wikiPageView.getText().toString();

                final VKRequest request = new VKRequest("pages.save", VKParameters.from(
                        "Text", "\""+text+"\"",
                        "page_id", String.valueOf(pageId),
                        "group_id", ownerId,
                        "user_id", createdId
                        //  "title", title
                ));
                String t = VKSdk.getAccessToken().accessToken;
                JSONObject job = new JSONObject();

                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        Intent in = new Intent(WikiEditActivity.this, WikiEditActivity.class);
                        in.putExtra("page_Id", pageId);
                        in.putExtra("owner_id", ownerId);
                        in.putExtra("created_id", createdId);
                        //ПРИ ИЗМЕНЕНИИ ПОМЕНЯТЬ ТО ЖЕ САМОЕ В WikiListActivity!!

                        startActivity(in);
                        // finish();
                    }
                    @Override
                    public void onError(VKError error) {
                        System.out.println("error");
                        String errorGroup = "список вики страниц не получен";
                        throw new RuntimeException(error.errorMessage);
                    }
                });
            }
        });

        Button show = (Button) findViewById(R.id.showWikiPageButton);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = wikiPageView.getText().toString();

                final VKRequest request = new VKRequest("pages.parseWiki", VKParameters.from(
                        "text", text,
                        "group_id", ownerId
                ));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {

                        String htmlText = "";
                        try {
                            //может тут надо использовать ту же фишку, что и в wikiListA?
//                            htmlText = response.json.getString("response");
                            htmlText = response.json.getJSONObject("response").toString();
                        } catch (JSONException e) {
                            //Вывести ошибку или че нить другое
                        }
                        setContentView(R.layout.activity_wiki_html);

                        Intent in = new Intent(WikiEditActivity.this, WikiHTMLActivity.class);
                        in.putExtra("html", htmlText);
                        startActivity(in);
                        finish();
                    }
                    @Override
                    public void onError(VKError error) {
                        System.out.println("error");
                        String errorGroup = "не получилось распарсить в HTML";
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wiki_edit, menu);
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
