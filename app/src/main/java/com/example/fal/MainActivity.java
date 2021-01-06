package com.example.fal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private ArrayList<Photo> Photos;
    private int mPageNo = 1;
    private String mApiKey;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycley_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        initData();

        SearchView searchView = findViewById(R.id.textsearch);
        //搜索图片
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("Query", query);

                startActivity(intent);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        //随机图片
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view);
            }
        });


        FloatingActionButton FabMore = findViewById(R.id.more);
        FabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhotos();
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
        adapter = new PhotoAdapter(this, Photos);
        adapter.ItemClick(new PhotoAdapter.OnImageClick() {
            @Override
            public void onImageClick(String url) { //从接口获取图片url
                BigPictureDialog bigPictureDialog = new BigPictureDialog(context, url);
                bigPictureDialog.show(getSupportFragmentManager(),"BigPicture");
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getPhotos(){
        String url = Constants.BASE_URL + "photos?page=" + mPageNo + "&order_by=popular&client_id=" + mApiKey;
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();

        //ArrayList<Photo> photoArrayList = new ArrayList<>();

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
        //System.out.println("End");
    }

    private void initData() {
        readApiKey();
        getPhotos();
        verifyStoragePermissions(this);
        // List<Photo> photos = new ArrayList<>();
//        List<String> imageUrls = new ArrayList<>();
//        //String SS = "https://source.unsplash.com/random";
//        String SS ="https://source.unsplash.com/random";
//        //String SS ="https://source.unsplash.com/daily";
//        for (int i = 0; i < 20; i++){
//            imageUrls.add(SS);
//        }
//
//        ArrayList<MyItem> myItems = new ArrayList<>();
//
//        for (String imageUrl : imageUrls) {
//            MyItem myItem = new MyItem();
//            myItem.setImageUrl(imageUrl);
//            myItems.add(myItem);
//        }
//        return myItems;
    }

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog(View view){
        RandomDialog randomDialog = new RandomDialog(context);
        randomDialog.show(getSupportFragmentManager(), "RandomDialog");
    }
}
