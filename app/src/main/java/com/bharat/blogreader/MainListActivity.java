package com.bharat.blogreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bharat.blogreader.model.Blog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainListActivity extends ListActivity {

    private static final String TAG = MainListActivity.class.getSimpleName();
    private static final String BLOG_URL = "http://blog.teamtreehouse.com/api/get_recent_summary?count=20";
    private static final String KEY_POSTS = "posts";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_THUMBNAIL = "thumbnail";
    private static final String KEY_URL = "url";

    private JSONObject blogPostData;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();

        } else {
            Toast.makeText(this, getString(R.string.network_not_available), Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            JSONArray jsonPosts = blogPostData.getJSONArray(KEY_POSTS);
            JSONObject blogPost = jsonPosts.getJSONObject(position);
            String url = blogPost.getString(KEY_URL);
            Intent intent = new Intent(this, BlogWebviewActivity.class);
            intent.setData(Uri.parse(url));
            startActivity(intent);

        }catch (Exception e) {
            Log.d(TAG,"Exception Caught ", e);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void handleBlogResponse() {
        progressBar.setVisibility(View.INVISIBLE);
        if (blogPostData == null) {
            showAlertDialogForError();
        } else {
            try {
                if(!(blogPostData.get("status").equals("ok"))) {
                    showAlertDialogForError();
                    return;
                }
                JSONArray jsonPosts = blogPostData.getJSONArray(KEY_POSTS);
                ArrayList<Blog> blogPosts = new ArrayList<Blog>();

                for (int i = 0; i < jsonPosts.length(); i++) {
                    JSONObject post = jsonPosts.getJSONObject(i);

                    String title = post.getString(KEY_TITLE);
                    title = Html.escapeHtml(title);

                    title = title.replace("&amp;#8211;", "-");

                    String author = post.getString(KEY_AUTHOR);
                    author = Html.escapeHtml(author);

                    Blog blogPost = new Blog(title, author, post.getString(KEY_THUMBNAIL));

                    blogPosts.add(blogPost);
                }

                ListAdapter adapter = new ListAdapter(this, blogPosts);
                setListAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlertDialogForError() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Oops!!").setMessage("There was error while retrieving data from the blog !").setPositiveButton(android.R.string.ok, null).create().show();

        TextView emptyTextView = (TextView) getListView().getEmptyView();
        emptyTextView.setText(getString(R.string.no_blog_posts));
    }


    class GetBlogPostsTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonResponse = null;
            try {
                URL blogPostUrl = new URL(BLOG_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) blogPostUrl.openConnection();
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    char[] charArray = new char[urlConnection.getContentLength()];
                    reader.read(charArray);
                    Log.i(TAG, new String(charArray));

                    jsonResponse = new JSONObject(new String(charArray));
                } else {
                    Log.e(TAG, "Http response was unsuccessful " + responseCode);
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "Error while reading the blogpost url", e);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException je) {

            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            blogPostData = result;
            handleBlogResponse();
        }
    }

    class ListAdapter extends ArrayAdapter<Blog> {
        private List<Blog> blogPosts = new ArrayList<Blog>();
        private Context context;
        private LayoutInflater layoutInflater;

        public ListAdapter(Context context, List<Blog> blogPosts) {
            super(context, R.layout.list_row_layout, blogPosts);
            this.context = context;
            this.blogPosts = blogPosts;
            layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            }
            TextView title = (TextView)convertView.findViewById(R.id.title); // title
            TextView author = (TextView)convertView.findViewById(R.id.author); // artist name
            ImageView thumbnail=(ImageView)convertView.findViewById(R.id.thumbnail);
            Blog blog = blogPosts.get(position);
            title.setText(blog.getTitle());
            author.setText(blog.getAuthor());
            Picasso.with(getContext()).load(Uri.parse(blog.getThumbnail())).into(thumbnail);
            return convertView;
        }
    }
}
