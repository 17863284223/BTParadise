package com.example.jcman.btparadise.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.jcman.btparadise.MainActivity;
import com.example.jcman.btparadise.R;
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getInfo();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getInfo() throws IOException {
        Document doc = Jsoup.connect(StringUtil.WEB_MAIN_PAGE_URL).get();
        if(doc!=null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("web",doc.toString());
            startActivity(intent);
            finish();
        }
    }
}
