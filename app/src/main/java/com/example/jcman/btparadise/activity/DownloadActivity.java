package com.example.jcman.btparadise.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.app.tool.logger.Logger;
import com.example.jcman.btparadise.R;

/**
 * Created by jcman on 16-3-6.
 */
public class DownloadActivity extends AppCompatActivity{

    private WebView mWebView;
    private String link = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_download);
        getDataFromIntent();
        setTitle("下载");
        mWebView = (WebView) findViewById(R.id.webview_download);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
//                if(url.endsWith("torrent"))
                view.loadUrl(url);
                Toast.makeText(DownloadActivity.this,url,Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        /*WebSettings s = mWebView.getSettings();
        s.setBuiltInZoomControls(true);
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setSavePassword(true);
        s.setSaveFormData(true);
        s.setJavaScriptEnabled(true);
        s.setGeolocationEnabled(true);
        s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
        s.setDomStorageEnabled(true);
        mWebView.requestFocus();*/
        mWebView.loadUrl(link);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setDownloadListener(new MyDownloadListener());
    }

    private void getDataFromIntent() {
        link = getIntent().getStringExtra("link");

    }

    private class MyDownloadListener implements DownloadListener{

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Logger.e(url);
        }
    }
}
