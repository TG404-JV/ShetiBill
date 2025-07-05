package com.example.farmer.govscheme

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.farmer.R
import im.delight.android.webview.AdvancedWebView


class FragmentGovtScheme : Fragment(), AdvancedWebView.Listener {
    private var mWebView: AdvancedWebView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_govt_scheme, container, false)
        mWebView = view.findViewById<AdvancedWebView>(R.id.myWebView)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        setupWebView()
        return view
    }

    private fun setupWebView() {
        mWebView!!.setListener(getActivity(), this)
        mWebView!!.setMixedContentAllowed(true)
        mWebView!!.setGeolocationEnabled(true)
        mWebView!!.getSettings().setJavaScriptEnabled(true)
        mWebView!!.getSettings().setDomStorageEnabled(true)

        mWebView!!.loadUrl("https://www.myscheme.gov.in/schemes/chief-minister-sustainable-agriculture-irrigation-scheme")
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        progressBar!!.setVisibility(View.VISIBLE)
    }

    override fun onPageFinished(url: String?) {
        progressBar!!.setVisibility(View.GONE)
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        progressBar!!.setVisibility(View.GONE)
        // Handle error scenario
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
    }

    override fun onExternalPageRequest(url: String?) {
        // Handle external page requests if needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mWebView!!.onDestroy()
    }
}
