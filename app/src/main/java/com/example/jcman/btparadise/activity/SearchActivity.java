package com.example.jcman.btparadise.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jcman.btparadise.R;
import com.example.jcman.btparadise.adapter.CommonAdapter;
import com.example.jcman.btparadise.adapter.ViewHolder;
import com.example.jcman.btparadise.model.Video;
import com.example.jcman.btparadise.util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

/**
 * Created by jcman on 16-3-8.
 */
public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private Toolbar mToolBar;
    private String mSearchWord;
    private EditText mSearchEdit;
    private ProgressDialog mLoadingDialog;
    private List<Video> mVideos;
    private ListView mListView;
    private MyListViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_search);
        getDataFromIntent();
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoadingDialog.setMessage("加载中～～");
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_top_back);
        ab.setDisplayHomeAsUpEnabled(true);
        mSearchEdit = (EditText) findViewById(R.id.et_ac_search);
        mSearchEdit.setText(mSearchWord);
        mListView = (ListView) findViewById(R.id.listview_ac_search);
        mListView.setOnItemClickListener(this);
        bit_loading = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_cover_loading);
        searchMovie();

    }

    private void getDataFromIntent() {
        mSearchWord = getIntent().getStringExtra("search");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search:
                searchMovie();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchMovie(){
        showProdialog();
        final String seachLink = StringUtil.getSearchVideoUrl(mSearchEdit.getText().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(seachLink).get();
                    if(doc!=null){
                        Elements es = doc.getElementsByAttributeValue("class","item cl");
                        if(es.size()>1){
                            mVideos = Video.getVideoList(doc);
                            setAdapter();
                        }else{
                            showMessage("没有找到资源");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cancelProDialog();
            }
        }).start();
    }

    private void setAdapter(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter = new MyListViewAdapter(SearchActivity.this,mVideos,R.layout.item_hot_video);
                mListView.setAdapter(mAdapter);
            }
        });
    }

    private void showProdialog(){
        mLoadingDialog.show();
    }

    private void showMessage(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SearchActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelProDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
            }
        });
    }

    private Bitmap bit_loading;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Video video = mVideos.get(position);
        Intent intent = new Intent(SearchActivity.this,VideoDetailActivity.class);
        intent.putExtra("video",video);
        startActivity(intent);
    }

    private class MyListViewAdapter extends CommonAdapter<Video> {

        public MyListViewAdapter(Context context, List<Video> list, int layoutId){
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, Video video){
            holder.display(SearchActivity.this,R.id.iv_item_hot_video,video.getCoverUrl(),bit_loading);
            holder.setText(R.id.tv_item_hot_video_title,video.getTitle());
            holder.setText(R.id.tv_item_hot_video_mark,video.getMark());
        }
    }
}
