package com.srpark.myapp.home.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityWebviewBinding
import com.srpark.myapp.utils.ActivityConstant.INTENT_WEB_URL
import com.srpark.myapp.utils.showSnackbar
import kotlinx.android.synthetic.main.activity_webview.*

class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {

    companion object {
        private const val GOOGLE_STORE = "market://"
        private const val ONE_STORE = "onestore://"
    }
    override val layoutResourceId: Int
        get() = R.layout.activity_webview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.toolbar.toolbar)
        val intentUrl = intent.getStringExtra(INTENT_WEB_URL)
        viewBinding.toolbar.toolbarTitle.text = intentUrl ?: ""
        settingWebView()
        viewBinding.webView.loadUrl(intentUrl)
    }

    override fun onResume() {
        super.onResume()
        viewBinding.webView.onResume()
    }

    override fun onPause() {
        viewBinding.webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        viewBinding.webView.stopLoading()
        viewBinding.webView.clearCache(true)
        viewBinding.webView.destroy()
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    private fun settingWebView() {
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.settings.setAppCacheEnabled(true)
        webView.settings.loadsImagesAutomatically = true
        webView.settings.allowFileAccess = false
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.webViewClient = CustomWebViewClient()
        webView.webChromeClient = WebViewChromeClient()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            webView.settings.loadWithOverviewMode = true
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        }
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            url?.let {
                return when {
                    it.startsWith(GOOGLE_STORE) || it.startsWith(ONE_STORE) -> {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                        true
                    }
                    else -> false
                }
            }
            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            viewBinding.progressBar.progress = 0
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            description?.let { showSnackbar(window.decorView.rootView, it) }
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            error?.let { showSnackbar(window.decorView.rootView, it.description.toString()) }
        }
    }

    /**
     * 크롬 클라이언트
     */
    inner class WebViewChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            viewBinding.progressBar.progress = newProgress
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            viewBinding.toolbar.toolbarTitle.text = title
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            AlertDialog.Builder(this@WebViewActivity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ -> result.confirm() }
                .setCancelable(false)
                .create()
                .show()
            return true
        }

        override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
            AlertDialog.Builder(this@WebViewActivity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ -> result.confirm() }
                .setNegativeButton(android.R.string.no) { _, _ -> result.cancel() }
                .setCancelable(true)
                .create()
                .show()
            return true
        }

        override fun onJsPrompt(
            view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?
        ): Boolean {
            val etView = EditText(this@WebViewActivity)
            AlertDialog.Builder(this@WebViewActivity)
                .setMessage(message)
                .setView(etView)
                .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm(etView.text.toString()) }
                .setNegativeButton(android.R.string.no) { _, _ -> result?.cancel() }
                .setCancelable(true)
                .create()
                .show()
            return true
        }
    }

    override fun onBackPressed() {
        if (viewBinding.webView.canGoBack()) {
            viewBinding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}