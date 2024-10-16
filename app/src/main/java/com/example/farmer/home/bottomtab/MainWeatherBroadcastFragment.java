package com.example.farmer.home.bottomtab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmer.R;

public class MainWeatherBroadcastFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_weather_broadcast, container, false);  // Your fragment layout

        WebView webView = view.findViewById(R.id.myWebView);

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Ensure links open within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Load the website
        webView.loadUrl("https://www.accuweather.com/");

        return view;
    }
}
