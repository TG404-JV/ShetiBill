package com.example.farmer.govscheme;


import android.graphics.Bitmap;
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import im.delight.android.webview.AdvancedWebView;

public class FragmentGovtScheme extends Fragment implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_govt_scheme, container, false);
        mWebView = view.findViewById(R.id.myWebView);
        progressBar = view.findViewById(R.id.progressBar);

        setupWebView();
        return view;
    }

    private void setupWebView() {
        mWebView.setListener(getActivity(), this);
        mWebView.setMixedContentAllowed(true);
        mWebView.setGeolocationEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.loadUrl("https://www.myscheme.gov.in/schemes/chief-minister-sustainable-agriculture-irrigation-scheme");
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(String url) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        progressBar.setVisibility(View.GONE);
        // Handle error scenario
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {
        // Handle external page requests if needed
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView.onDestroy();
    }
}
