package com.example.farmer.govscheme;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebChromeClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmer.R;

public class FragmentGovtScheme extends Fragment {

    private WebView myWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_govt_scheme, container, false);
        myWebView = view.findViewById(R.id.myWebView);

        // Enable JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Allow mixed content if needed
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Set a WebViewClient to handle page navigation
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // Handle SSL certificate errors
                handler.proceed(); // Ignore SSL certificate errors (use with caution)
            }
        });

        // Set a WebChromeClient for better handling of JavaScript dialogs, favicons, titles, and the progress
        myWebView.setWebChromeClient(new WebChromeClient());

        // Load the desired URL
        myWebView.loadUrl("https://agriwelfare.gov.in/en/Major#skiptomain"); // Replace with the desired URL

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clear the WebView to prevent memory leaks
        if (myWebView != null) {
            myWebView.clearHistory();
            myWebView.removeAllViews();
            myWebView.destroy();
        }
    }
}