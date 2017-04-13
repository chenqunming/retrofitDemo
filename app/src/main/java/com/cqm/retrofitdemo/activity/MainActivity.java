package com.cqm.retrofitdemo.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.cqm.retrofitdemo.R;
import com.cqm.retrofitdemo.adapter.NewsAdapter;
import com.cqm.retrofitdemo.bean.NewsBean;
import com.cqm.retrofitdemo.bean.NewsResult;
import com.cqm.retrofitdemo.http.ApiService;
import com.cqm.retrofitdemo.http.RetrofitUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private List<NewsBean> mDatas;
    private NewsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        }
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
//                DividerItemDecoration.VERTICAL));
        mAdapter = new NewsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        loadData();


//        String filePath = "http://wdj-uc1-apk.wdjcdn.com/0/7f/4314a770f18eaa2744c92a9a87f497f0.apk";
//        downloadFild(filePath);

    }


    private void loadData() {
        ApiService mService = RetrofitUtil.create(ApiService.class);
        Call<NewsResult> call = mService.getNewsData("115daac717fe4295d16734595c2a86e3", "top");
        call.enqueue(new Callback<NewsResult>() {
                         @Override
                         public void onResponse(Call<NewsResult> call, Response<NewsResult> response) {
                             mDatas = response.body().getResult().getData();
                             mAdapter.setDatas(mDatas);
                             mAdapter.notifyDataSetChanged();
                         }

                         @Override
                         public void onFailure(Call<NewsResult> call, Throwable t) {
                             System.out.println(t.toString());
                             Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                         }
                     }
        );
    }


    private void uploadFild(String filePath) {
        File file = new File(filePath);
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part  和后端约定好Key，这里的partName是用image
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        // 添加描述
        file.getPath();
        file.getAbsolutePath();

        String descriptionString = "hello, 这是文件描述";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);
        // 执行请求
        ApiService mService = RetrofitUtil.create(ApiService.class);

//        HashMap<String, RequestBody> map = new HashMap<>();
//        map.put("file", requestFile);
//        map.put("description", description);
//        Call<ResponseBody> mcall = mService.uploadFiles("", map);


        Call<ResponseBody> call = mService.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    private void uploadFild1(String filePath) {
        File file = new File(filePath);
        //构建body
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", "chenqm")
                .addFormDataPart("name", "123456")
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                .build();
        ApiService mService = RetrofitUtil.create(ApiService.class);
        Call<ResponseBody> call = mService.upload(requestBody);
        // 执行
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    private void downloadFild(String filePath) {
        ApiService mService = RetrofitUtil.create(ApiService.class);
        Call<ResponseBody> call = mService.downloadFile(filePath);
        // 执行
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   final Response<ResponseBody> response) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        writeResponseBodyToDisk(response.body());
                        Log.v("Download", "success");
                    }
                }.start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Download error:", t.getMessage());
            }
        });
    }


    private boolean writeResponseBodyToDisk(ResponseBody body) {
        File futureStudioIconFile = new File(Environment.getExternalStorageDirectory(),"aa.apk");

        try{
            if(!futureStudioIconFile.exists()){
                futureStudioIconFile.createNewFile();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    fileSizeDownloaded += read;
                    String str = (fileSizeDownloaded*100/fileSize)+"%";
                    Log.v("Download","downing ... "+str);

                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e("Download error:", "write file error");
            return false;
        }
    }
}
