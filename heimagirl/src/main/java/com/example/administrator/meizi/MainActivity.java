package com.example.administrator.meizi;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "aaa";
    private List<ResultBean.ResultsBean> mList = new ArrayList<>();
    private Gson mGson = new Gson();
    private boolean isLoading;
//    @BindView(R.id.listview)
    ListView mListview;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // ButterKnife.bind(this);
        mListview = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter();
        mListview.setAdapter(mAdapter);
        initListener();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        getDatas();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initListener() {
        mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && !isLoading){
                    if(mListview.getLastVisiblePosition() == mList.size()-1){
                        loadMoreDatas();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void loadMoreDatas() {
            getDatas();
    }

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            ResultBean.ResultsBean resultsBean = mList.get(position);
            holder.mTextView.setText(resultsBean.getPublishedAt());
            String url = resultsBean.getUrl();
            Glide.with(MainActivity.this).load(url).bitmapTransform(new CropCircleTransformation(MainActivity.this)).into(holder.mImageView);
            return convertView;
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }

    public class ViewHolder {
        ImageView mImageView;
        TextView mTextView;

        public ViewHolder(View view) {
            mImageView = (ImageView) view.findViewById(R.id.iv_img);
            mTextView = (TextView) view.findViewById(R.id.tv_time);
        }
    }

    private void getDatas() {
        isLoading = true;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://gank.io/api/data/福利/10/" + mList.size()/10 + 1).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                ResultBean resultBean = mGson.fromJson(jsonString, ResultBean.class);
                mList.addAll(resultBean.getResults());
                Log.d(TAG, "onResponse: " + jsonString);
                isLoading = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
