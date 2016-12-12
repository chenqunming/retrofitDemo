package com.cqm.retrofitdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.cqm.retrofitdemo.R;
import com.cqm.retrofitdemo.adapter.NewsAdapter;
import com.cqm.retrofitdemo.bean.NewsBean;
import com.cqm.retrofitdemo.bean.NewsResult;
import com.cqm.retrofitdemo.http.ApiService;
import com.cqm.retrofitdemo.http.NewsService;

import java.util.List;

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

    }


    private void loadData() {
        NewsService mService = ApiService.createRetrofitService(NewsService.class);
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
}
