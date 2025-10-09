package com.eva.lead.capture.ui.fragments.help

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaHelpBinding
import com.eva.lead.capture.domain.model.FAQItemList
import com.eva.lead.capture.ui.base.BaseFragment
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class EvaHelpFragment :
    BaseFragment<FragmentEvaHelpBinding, EvaHelpViewModel>(EvaHelpViewModel::class.java) {
    private lateinit var mContext: Context

    private val faqHelpAdapter: EvaHelpFaqAdapter by lazy {
        EvaHelpFaqAdapter(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaHelpBinding {
        return FragmentEvaHelpBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initView()
        this.initListener()
        this.loadJson()
    }

    private fun loadJson() {
        val inputStream = resources.openRawResource(R.raw.faq)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson().fromJson(jsonString, FAQItemList::class.java)
        faqHelpAdapter.setFaqItems(gson.faqs)

    }

    private fun initView() {
        binding.incToolbar.llcbtn.visibility = View.GONE
        binding.incToolbar.tvTitle.text = "Help"
        this.initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvFAQ.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = faqHelpAdapter
        }
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}