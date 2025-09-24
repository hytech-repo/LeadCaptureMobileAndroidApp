package com.eva.lead.capture.ui.fragments.activatelicense

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.databinding.FragmentEvaEventActivationBinding
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.ui.base.BaseFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EvaEventActivationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EvaEventActivationFragment :
    BaseFragment<FragmentEvaEventActivationBinding, EvaActivateLicenseViewModel>(
        EvaActivateLicenseViewModel::class.java
    ) {
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "ActivateLicenseFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaEventActivationBinding {
        return FragmentEvaEventActivationBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initView()
        this.initListener()
        this.fetchExhibitorFromDb()
    }

    private fun initView() {

    }

    private fun fetchExhibitorFromDb() {
        lifecycleScope.launch {
            val code = prefManager.get(AppConstants.LEAD_CODE, "")
            val exhibitor = viewModel.checkExhibitor(code).firstOrNull()
            if (exhibitor != null) {
                showDetailOnUI(exhibitor)
            }
        }
    }

    private fun showDetailOnUI(exhibitor: Exhibitor) {
        binding.tvUserName.text = "${exhibitor.firstName} ${exhibitor.lastName}"
    }

    private fun initListener() {
        val slideBtn = binding.btnActivate.slideLayout
        slideBtn.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float
            ) {
//                val percent = (progress * 100).toInt()
//                progressBar.progress = percent
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.end) {
                    binding.btnActivate.slideLayout.isInteractionEnabled = false
                    prefManager.put(AppConstants.LICENSE_ACTIVATED, true)
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float
            ) {
            }
        })

        binding.btnActivate.slideLayout.setOnClickListener {
            navigateToOtherScreen()
        }
    }

    private fun navigateToOtherScreen() {
        findNavController().navigate(R.id.action_eventActivationFragment_to_evaDeviceDetailFragment)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EvaEventActivationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EvaEventActivationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}