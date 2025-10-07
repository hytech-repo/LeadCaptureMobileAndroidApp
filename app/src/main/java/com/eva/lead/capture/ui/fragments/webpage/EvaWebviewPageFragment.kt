package com.eva.lead.capture.ui.fragments.webpage

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.databinding.FragmentEvaWebviewPageBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment

class EvaWebviewPageFragment :
    BaseFragment<FragmentEvaWebviewPageBinding, EvaWebviewPageViewModel>(EvaWebviewPageViewModel::class.java) {

    private lateinit var mContext: Context
    private var url: String = ""
    private var title: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaWebviewPageBinding {
        return FragmentEvaWebviewPageBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initBundle()
        this.initView()
        this.initListener()
        this.loadUrl()
    }

    private fun initBundle() {
        if (arguments != null) {
            url = arguments!!.getString("web_view_url", "")
            title = arguments!!.getString("page_title", "")
        }
    }

    private fun initView() {
        binding.incToolbar.llcbtn.visibility = View.GONE
        binding.incToolbar.tvTitle.text = title
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).showHideBottomNavBar(false)
    }

    private fun loadUrl() {
        binding.wvWebPage.webViewClient = webViewClient
        val setting = binding.wvWebPage.settings
        setting.javaScriptEnabled = true
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        binding.wvWebPage.loadUrl(url!!)

    }

    val webViewClient = object : WebViewClient() {
        override fun doUpdateVisitedHistory(
            view: WebView?,
            url: String?,
            isReload: Boolean
        ) {
            super.doUpdateVisitedHistory(view, url, isReload)
        }

        override fun onPageStarted(
            view: WebView?,
            url: String?,
            favicon: Bitmap?
        ) {
            super.onPageStarted(view, url, favicon)
            showProgressDialog(false)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            hideProgressDialog()
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            url: String?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

}