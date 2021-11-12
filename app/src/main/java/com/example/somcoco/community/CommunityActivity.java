package com.example.somcoco.community;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.somcoco.R;

public class CommunityActivity extends AppCompatActivity {
    WebView commuWeb;
    ProgressBar progressBar;
    EditText urlEdt;
    ImageView search;
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        commuWeb = (WebView) findViewById(R.id.community_web);
        progressBar = (ProgressBar) findViewById(R.id.community_prog);
        //urlEdt = (EditText) findViewById(R.id.community_keyword);
        //search = (ImageView) findViewById(R.id.community_search);
        close = (ImageView) findViewById(R.id.community_close);

        progressBar.setVisibility(View.GONE);

        initWebView();

//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                commuWeb.loadUrl("http://" + urlEdt.getText().toString() + "");
//            }
//        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void initWebView() {
        commuWeb.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings ws = commuWeb.getSettings();
        ws.setJavaScriptEnabled(true);
        commuWeb.loadUrl("http://somcoco.co.kr/");
    }

    @Override
    public void onBackPressed() {
        if (commuWeb.canGoBack()){
            commuWeb.goBack();
        } else {
            super.onBackPressed();
        }

    }
}
