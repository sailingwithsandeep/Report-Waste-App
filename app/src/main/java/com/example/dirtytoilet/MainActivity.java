package com.example.dirtytoilet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    WebView webView;
    private static final int PERMISSION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView=findViewById(R.id.webview);

        webView.loadUrl("https://www.rkuinfo.ml/hackathon/main/");
                webView.getSettings().setJavaScriptEnabled(true);
        webView.scrollBy(1,2);
        //w1.addJavascriptInterface(new WebA);
        //w1.setWebViewClient();
        webView.canGoBack();
        webView.canGoForward();
        webView.getSettings().getBuiltInZoomControls();
        webView.getSettings().setDefaultFontSize(10);
        if (checkPermission()) {
            //main logic or main code

            // . write your main code to execute, It will execute if the permission is already given.

        } else {
            requestPermission();
        }
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // callback.invoke(String origin, boolean allow, boolean remember);
                callback.invoke(origin, true, false);
            }
        });

        webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        webView.getSettings().setSupportZoom(true);
        //   w1.getSettings().setDatabaseEnabled();
        webView.computeScroll();
        //w1.canGoBack();
    }
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }
}
