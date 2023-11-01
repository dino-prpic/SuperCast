package com.supercast.tv

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity

class BrowserActivity : FragmentActivity() {
    private lateinit var myWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browser)
        myWebView = findViewById(R.id.webview)
        val ip = intent.getStringExtra("ip")
        val port = intent.getStringExtra("port")
        val url = "http://$ip:$port"
        myWebView.loadUrl(url)
        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = WebViewClient()
    }

    override fun onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
