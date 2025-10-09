package com.eva.lead.capture.ui.fragments.addlead

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaAddLeadBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.services.EvaRecordAudioService
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.ui.dialog.EvaConfirmationDialog
import com.eva.lead.capture.ui.dialog.EvaLeadAudioSelectionDialog
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import java.io.File

class EvaAddLeadFragment :
    BaseFragment<FragmentEvaAddLeadBinding, EvaAddLeadViewModel>(EvaAddLeadViewModel::class.java) {
    private lateinit var mContext: Context
    private var recordService: EvaRecordAudioService? = null
    private var isBound = false
    private var leadListName: ArrayList<String> = arrayListOf()
    private var leadList: ArrayList<EvaLeadData>? = arrayListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaAddLeadFragment"
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(mContext, EvaRecordAudioService::class.java)
//        mContext.startService(intent)
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaAddLeadBinding {
        return FragmentEvaAddLeadBinding.inflate(inflater, container, false)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as EvaRecordAudioService.AudioBinder
            recordService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            recordService = null
        }
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
        this.initView()
        this.initListener()
        this.fetchLeadList()
    }

    private fun fetchLeadList() {
        lifecycleScope.launch {
            val leads = viewModel.getLeadList().firstOrNull()
            if (leads != null) {
                leadList = leads as ArrayList
                val leadsName = leads.map { "${it.firstName} ${it.lastName}" }
                leadListName = leadsName as ArrayList<String>
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkAudioRecording()
    }

    private fun checkAudioRecording() {
        if (recordService?.isRecordingInProgress() == true) {
            binding.llcRecording.visibility = View.VISIBLE
            binding.btnRecord.visibility = View.GONE
            checkAudioPermission()
        } else {
            binding.llcRecording.visibility = View.GONE
            binding.btnRecord.visibility = View.VISIBLE
        }
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Add Lead"
    }

    private fun initListener() {
        binding.cvAddManual.setOnClickListener {
            findNavController().navigate(R.id.action_evaAddLeadFragment_to_evaAddManualLead)
        }
        binding.cvQrCode.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("mode", "qr")
            bundle.putBoolean("send_fragment_result", false)
            findNavController().navigate(R.id.action_evaAddLeadFragment_to_evaCameraFragment, bundle)
        }
        binding.cvBusinessCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("mode", "card")
            bundle.putBoolean("send_fragment_result", false)
            findNavController().navigate(R.id.action_evaAddLeadFragment_to_evaCameraFragment, bundle)
        }
        binding.btnRecord.setOnClickListener {
            checkAudioPermission()
        }
        binding.ivStopRecording.setOnClickListener {
            showSavedRecordingFile()
        }
        binding.incToolbar.ivUserImage.setOnClickListener {
            findNavController().navigate(R.id.action_evaAddLeadFragment_to_evaUserProfileFragment)
        }
    }

    private fun showSavedRecordingFile() {
        recordService?.removeProgressCallback()
        val file = recordService?.stopRecording()
        binding.waveRecording.addAmplitude(0)
        binding.btnRecord.visibility = View.VISIBLE
        binding.llcRecording.visibility = View.GONE

        if (file != null) {
            showConfirmationDialog(file)
        }
    }


    private fun showConfirmationDialog(audioFile: File) {
        val confirmationDialog = EvaConfirmationDialog()
        val bundle = Bundle()
        bundle.putString("heading", mContext.getString(R.string.eva_stop_recording))
        bundle.putString("sub_heading", mContext.getString(R.string.eva_stop_record_msg))
        bundle.putString("primary_btn_text", mContext.getString(R.string.eva_save))
        bundle.putString("seconday_btn_text", mContext.getString(R.string.eva_discard))
        bundle.putInt("icon_bgcolor", R.color.color_lime_green)
        bundle.putInt("ivIcon", R.drawable.ic_mic_white)
        confirmationDialog.arguments = bundle
        confirmationDialog.isCancelable = false
        confirmationDialog.apply {
            onConfirmationListener = { isPrimaryBtnClicked ->
                if (isPrimaryBtnClicked) {
                    showAudioSelectionDialog(audioFile)
                } else {
                    deleteRecordingFile(audioFile)
                }
                dismiss()
            }
        }.show(requireActivity().supportFragmentManager, "EvaConfirmationAudioDialog")
    }

    fun showAudioSelectionDialog(audioFile: File) {
        val leadSelectionDialog = EvaLeadAudioSelectionDialog()
        val bundle = Bundle()
        bundle.putParcelableArrayList("lead_list", leadList)
        leadSelectionDialog.arguments = bundle
        leadSelectionDialog.isCancelable = false
        leadSelectionDialog.apply {
            onItemClickListener = { action, leadName ->
                when (action) {
                    "dismiss" -> deleteRecordingFile(audioFile)
                    "save_only" -> { recordService?.saveRecordingIntoDb(audioFile) }
                    "save" -> { saveRecordingWithLeadDetail(leadName, audioFile) }
                }
                dismiss()
            }
        }.show(requireActivity().supportFragmentManager, "EvaSaveLeadRecordingDialog")
    }

    fun saveRecordingWithLeadDetail(leadName: EvaLeadData?, audioFile: File) {
        recordService?.saveRecordingIntoDb(audioFile)
        if (leadName != null) {
            leadName.audioFilePath = audioFile.name
            viewModel.updateLeadData(leadName)
        }
    }

    fun deleteRecordingFile(audioFile: File) {
        if (audioFile.exists()) {
            if (audioFile.delete()) {
                log.d(TAG, "audio discarded and deleted file ${audioFile.absolutePath}")
            }
        }

    }

    private fun startRecordingAtBackground() {
        recordService?.setOnProgressListener { progress, amplifiy ->
            binding.llcRecording.visibility = View.VISIBLE
            binding.btnRecord.visibility = View.GONE
            log.d("Recording", "progress ${progress}, amp: $amplifiy")
            binding.waveRecording.addAmplitude(amplifiy)
        }
        recordService?.startRecording()
    }

    private fun checkAudioPermission() {
        if (checkPermissions()) {
            startRecordingAtBackground()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_PERMISSION_CODE
        )
    }

    override fun onPermissionResult(permission: Map<String, Boolean>, requestCode: Int) {
        if (permission[Manifest.permission.RECORD_AUDIO] == true) {
            startRecordingAtBackground()
        }
    }

    companion object {
        fun newInstance() = EvaAddLeadFragment()
        private const val REQUEST_PERMISSION_CODE = 1001
    }
}