package com.example.jcman.btparadise.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.tool.logger.Logger;
import com.example.jcman.btparadise.R;
import com.example.jcman.btparadise.model.Video;

import net.tsz.afinal.FinalBitmap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcman on 16-3-6.
 */
public class VideoDetailActivity extends AppCompatActivity{

    private ImageView mCoverView;
    private Video mVideo;
    private ProgressDialog mLoadingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_videodetail);
        getDataFromIntent();
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setMessage("加载中~~");
        mLoadingDialog.show();
        mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mCoverView = (ImageView) findViewById(R.id.iv_ac_videodetail);
        getDataFromNet();
        setTitle(mVideo.getTitle());

    }

    private void initListView(Document doc){
        final List<Video> list = Video.getVideoDownList(doc);
        List<String> titles = new ArrayList<>();
        for(Video video:list)
            titles.add(video.getTitle());
        ListView listview = (ListView) findViewById(R.id.listview__ac_videodetail_downlist);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,titles);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String link = list.get(position).getLink();

                /*Intent intent = new Intent(VideoDetailActivity.this,DownloadActivity.class);
                intent.putExtra("link",link);
                startActivity(intent);*/

                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(link);
                intent.setData(content_url);
                startActivity(intent);
            }
        });


    }

    private void getDataFromNet(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.e(mVideo.getLink());
                    Document doc = Jsoup.connect(mVideo.getLink()).get();
                    mVideo = Video.getVideoInfo(doc);
                    updateView(doc);
                    cancelDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    cancelDialog();
                }

            }
        }).start();
    }

    private void cancelDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
            }
        });
    }

    private void getDataFromIntent() {
        mVideo  = (Video) getIntent().getSerializableExtra("video");

    }

    private void updateView(final Document doc){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(mVideo.getTitle());
                String coverUrl = mVideo.getCoverUrl();
                FinalBitmap finalBit = FinalBitmap.create(VideoDetailActivity.this);
                finalBit.display(mCoverView,coverUrl);
                ((TextView)findViewById(R.id.tv_ac_videodetail_actor)).setText(mVideo.getActor());
                ((TextView)findViewById(R.id.tv_ac_videodetail_label)).setText(mVideo.getLabel());
                ((TextView)findViewById(R.id.tv_ac_videodetail_locality)).setText(mVideo.getLocality());
                ((TextView)findViewById(R.id.tv_ac_videodetail_time)).setText(mVideo.getTime());
                ((TextView)findViewById(R.id.tv_ac_videodetail_daoyan)).setText(mVideo.getDaoyan());
                initListView(doc);
            }
        });
    }
}
