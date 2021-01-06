package com.example.fal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ArrayList<Photo> Photos;
    private int mPageNo = 1;
    private String mApiKey;
    private Context context;
    private String query;
    private String lang = "en";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;

        query = getIntent().getStringExtra("Query");
        if(query.getBytes(StandardCharsets.UTF_8).length > 1) lang = "zh";
        recyclerView = findViewById(R.id.recycley_view1);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        initData();

        FloatingActionButton FabMore = findViewById(R.id.more1);
        FabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSearchPhotos();
            }
        });
    }

    private void readApiKey() {
        try {
            ApplicationInfo applicationInfo = this.getPackageManager().getApplicationInfo(
                    this.getPackageName(), PackageManager.GET_META_DATA);
            //read meta-data from app manifest
            Bundle bundle = applicationInfo.metaData;
            if (bundle == null)
                throw new IllegalAccessException(Constants.getExceptionMsg());
            mApiKey = bundle.getString(Constants.EXTRA_API_KEY, null);
            //show error if no api key is found
            if (mApiKey == null)
                throw new IllegalAccessException(Constants.getExceptionMsg());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void updateUi() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            updateAdapter();
        } else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateAdapter();
                }
            });
        }
    }

    private void updateAdapter() {
        SearchAdapter adapter = new SearchAdapter(this, Photos);
        adapter.ItemClick(new SearchAdapter.OnImageClick() {
            @Override
            public void onImageClick(String url) { //从接口获取图片url
                BigPictureDialog bigPictureDialog = new BigPictureDialog(context, url);
                bigPictureDialog.show(getSupportFragmentManager(),"BigPicture");
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getSearchPhotos(){
        String url = Constants.SEARCH_URL + "photos?page=" + mPageNo + "&query=" + query + "&lang=" + lang + "&client_id=" + mApiKey;
        //
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Log.d(TAG, String.format("showThreadOnUiThread(%MainActivity)", e.toString()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //init and append photos to the list
                        if (Photos == null)
                            Photos = new ArrayList<>();
                        String res = response.body().string();
                        int l = res.indexOf("result");
                        int r = res.length();
                        res = res.substring(l + 9, r - 1);

                        Gson gson = new Gson();
                        try {

                            Photo[] array = gson.fromJson(res, Photo[].class);
                            //add photos to list


                            Photos.addAll(Arrays.asList(array));
                            //increment page # for more loading

                            mPageNo++;
                            updateUi();
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, Constants.getExceptionMsg());
                        }
                    }
                });
    }

    private void initData() {
        readApiKey();
        getSearchPhotos();
    }

}
