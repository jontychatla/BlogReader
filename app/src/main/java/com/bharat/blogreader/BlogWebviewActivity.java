package com.bharat.blogreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.bharat.blogreader.R;

public class BlogWebviewActivity extends Activity {

    private String blogUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_webview);

        blogUrl = getIntent().getData().toString();
        WebView webView = (WebView)findViewById(R.id.webView3);
        webView.loadUrl(blogUrl);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_webview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_share) {
            sharePost();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void sharePost() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, blogUrl);
        startActivity(Intent.createChooser(intent, "How do you want to share?"));
    }
}
