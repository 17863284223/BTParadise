package com.example.jcman.btparadise.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.tool.logger.Logger;
import com.example.jcman.btparadise.MainActivity;
import com.example.jcman.btparadise.R;
import com.example.jcman.btparadise.util.NetUtils;
import com.example.jcman.btparadise.util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by jcman on 16-3-3.
 */
public class WelcomeActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_welcome);
        getInfo();

    }

    private void getInfo(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                if(!NetUtils.isConnected(WelcomeActivity.this))
                    showMessage("网络连接失败");
                else
                    getInfoFromNet();
            }
        }).start();
    }

    private void getInfoFromNet(){
        try {
            Document doc = Jsoup.connect(StringUtil.WEB_MAIN_PAGE_URL).get();
            if(doc!=null){
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("web",doc.toString());
                startActivity(intent);
                finish();
            }else{
                showMessage("获取影片信息失败");
            }
        } catch (Exception e){
            showMessage("获取影片信息失败");
            e.printStackTrace();
        }
    }

    private void showMessage(final String msg){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(WelcomeActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
