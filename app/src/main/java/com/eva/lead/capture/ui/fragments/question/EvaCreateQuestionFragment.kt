package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentAddQuestionBinding
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.DragManageAdapter
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.showToast

class EvaCreateQuestionFragment :
    BaseFragment<FragmentAddQuestionBinding, EvaQuestionsViewModel>(EvaQuestionsViewModel::class.java) {
    private var mContext: Context? = null
    private val optionsList = mutableListOf<String>()
    private var selectedQuestionType = ""
    private var type: Int = 1

    private val optionsAdapter: QuestionOptionAdapter by lazy {
        QuestionOptionAdapter(mContext!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        TAG = "EvaCreateQuestionFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentAddQuestionBinding {
        return FragmentAddQuestionBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initBundle()
        this.initView()
        this.initListener()
        this.initRecyclerView()
//        this.addNewEditText()
    }

    private fun initBundle() {
        if (arguments != null) {
            type = arguments!!.getInt("question_tab_type", 1)
        }
    }

    private fun initView() {
        // Set the title of the toolbar
        binding.incToolbar.tvTitle.text = "Create Questions"
        binding.incToolbar.llcbtn.visibility = View.GONE
        binding.actvQuestionType.setDropDownBackgroundResource(R.drawable.bg_edittext_focusable_selector)

        // Set up Spinner for Question Type
        val questionTypes = listOf("MCQ", "Text")
        this.showQuestionTypeDropDown(questionTypes)

        // Set default visibility
//        toggleViewsBasedOnQuestionType(questionTypes[0])
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            if (validateUi()) {
                this.saveData()
            }
        }
        binding.incEditText.apply {
            icCross.setOnClickListener {
                binding.incEditText.etDynamicInput.text.clear()
                binding.incEditText.llcEditItem.visibility = View.GONE
            }
            etDynamicInput.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    val text = etDynamicInput.text.toString().trim()
                    if (text.isNotEmpty()) {
                        addOptionIntoList(text)
                    }
                    true
                } else {
                    false
                }
            }
        }
        binding.tvAddBtn.setOnClickListener {
            val text = binding.incEditText.etDynamicInput.text
            if (!text.isNullOrEmpty()) {
                addOptionIntoList(text.toString())
            }
            binding.incEditText.llcEditItem.visibility = View.VISIBLE
            binding.incEditText.etDynamicInput.requestFocus()
        }
    }

    private fun addOptionIntoList(text: String) {
        if (!optionsList.contains(text)) {
            optionsList.add(text)
            optionsAdapter.updateQuestionList(optionsList)
            binding.incEditText.etDynamicInput.text.clear()
        } else {
            mContext?.showToast("Duplicate Option", ToastType.ERROR)
        }
    }


    private fun initRecyclerView() {
        binding.recyclerViewOptions.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(mContext)
            adapter = optionsAdapter

            val itemTouchHelper = ItemTouchHelper(DragManageAdapter(optionsAdapter))
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun showQuestionTypeDropDown(questionTypes: List<String>) {
        val spinnerAdapter = ArrayAdapter(mContext!!, R.layout.dropdown_text_item, questionTypes)
        binding.actvQuestionType.apply {
            setOnItemClickListener { _, _, position, _ ->
                selectedQuestionType = questionTypes[position]
                toggleViewsBasedOnQuestionType(questionTypes[position])
            }
            setAdapter(spinnerAdapter)
        }
    }

    private fun toggleViewsBasedOnQuestionType(questionType: String) {
        // Show MCQ Views and Hide Answer Views
        when (questionType) {
            "MCQ" -> {
                binding.llcMcq.visibility = View.VISIBLE
                binding.llcAnswerBlock.visibility = View.GONE
            }

            "Text" -> {
                binding.llcMcq.visibility = View.GONE
                binding.llcAnswerBlock.visibility = View.VISIBLE
            }
        }
    }

    private fun saveData() {
        val question = binding.questionInput.text.toString()
        val type = if (type == 1) "question" else "note"
        viewModel.saveQuestionIntoList(question, type, selectedQuestionType, optionsList) {
            findNavController().popBackStack()
        }
    }

    private fun validateUi(): Boolean {
        if (binding.questionInput.text.isNullOrEmpty()) {
            mContext?.showToast("Question is empty", ToastType.ERROR)
            return false
        }
        if (selectedQuestionType.isEmpty()) {
            mContext?.showToast("Question type not selected", ToastType.ERROR)
            return false
        }
        if (selectedQuestionType == "MCQ" && optionsList.isEmpty()) {
            mContext?.showToast("Options not added", ToastType.ERROR)
            return false
        }
        if (!binding.incEditText.etDynamicInput.text.isNullOrEmpty()) {
            mContext?.showToast("Options not added", ToastType.ERROR)
            return false
        }
        return true
    }

}