package com.eva.lead.capture.ui.dialog

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaLeadAudioSelectionDialogBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EvaLeadAudioSelectionDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class EvaLeadAudioSelectionDialog :
    AdaptiveDialogFragment<FragmentEvaLeadAudioSelectionDialogBinding>() {
    private lateinit var mcontext: Context
    private var leadList: List<EvaLeadData> = mutableListOf()
    private var selectedLead: EvaLeadData? = null

    var onItemClickListener: (action: String, leadName: EvaLeadData?) -> Unit = { action, leadName -> }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mcontext = context
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaLeadAudioSelectionDialogBinding {
        return FragmentEvaLeadAudioSelectionDialogBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initBundle()
        this.initListener()
        this.initAdapter()
    }

    private fun initListener() {
        binding.ivIcon.setOnClickListener {
            onItemClickListener.invoke("dismiss", null)
        }
        binding.tvPrimaryBtn.setOnClickListener {
            onItemClickListener.invoke("save", selectedLead!!)
        }
        binding.tvSecondaryBtn.setOnClickListener {
            onItemClickListener.invoke("save_only", null)
        }
    }

    private fun initBundle() {
        if (arguments != null) {
            leadList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments!!.getParcelableArrayList("lead_list", EvaLeadData::class.java)?: mutableListOf()
            } else {
                arguments!!.getParcelableArrayList("lead_list")?: mutableListOf()
            }
        }
    }

    private fun initAdapter() {
        val leadsName = leadList.map { "${it.firstName} ${it.lastName}" }
        val leadAdapter = ArrayAdapter<String>(
            mcontext,
            R.layout.dropdown_text_item,
            leadsName.toMutableList()
        )
        binding.actvLeadDropDown.apply {
            setOnItemClickListener { _, _, position, _ ->
                onItemSelected(leadList[position])
            }
            setAdapter(leadAdapter)
        }
    }

    private fun onItemSelected(leadName: EvaLeadData) {
        this.selectedLead = leadName
        binding.tvPrimaryBtn.isEnabled = true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EvaLeadAudioSelectionDialog.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EvaLeadAudioSelectionDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}