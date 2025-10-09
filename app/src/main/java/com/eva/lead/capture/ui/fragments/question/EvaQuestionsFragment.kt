package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaQuestionsBinding
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.QuickNote
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.QuestionTabType
import com.eva.lead.capture.utils.showPopupDialog
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaQuestionsFragment :
    BaseFragment<FragmentEvaQuestionsBinding, EvaQuestionsViewModel>(EvaQuestionsViewModel::class.java) {

    private var mContext: Context? = null
    private val questionsListAdapter: QuestionsListAdapter by lazy {
        QuestionsListAdapter(mContext!!)
    }

    private val quickNoteAdapter: EvaQuickNoteAdapter by lazy {
        EvaQuickNoteAdapter(mContext!!)
    }
    private var currentTabType = QuestionTabType.QUESTIONS

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentEvaQuestionsBinding {
        return FragmentEvaQuestionsBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.init()
        this.initListener()
        this.setupRecyclerView()
        this.setupTabLayout()
    }

    private fun init() {
        binding.incToolbar.llcbtn.visibility = View.GONE
        binding.incToolbar.tvTitle.text = "Questions"
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.incQuickNote.tvAddBtn.setOnClickListener {
            binding.incQuickNote.llcAddNew.visibility = View.VISIBLE
        }
        binding.incQuickNote.tvSaveBtn.setOnClickListener {
            val note = binding.incQuickNote.etTypeHere.text
            if (!note.isNullOrEmpty()) {
                addQuickNoteIntoDB(note.toString())
            }
        }
        // Navigate to Add Question screen
        binding.addQuestion.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("question_tab_type", currentTabType.ordinal)
            findNavController().navigate(
                R.id.action_evaQuestionsFragment_to_evaCreateQuestionFragment, bundle
            )
        }
    }

    private fun addQuickNoteIntoDB(note: String) {
        viewModel.addQuickNote(note) {
            binding.incQuickNote.llcAddNew.visibility = View.GONE
            binding.incQuickNote.etTypeHere.text?.clear()
            fetchQuickNotesFromDb()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).showHideBottomNavBar(false)
    }


    private fun setupTabLayout() {
        binding.tabLayout.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> showQuestions()
                        1 -> showMyQuestions()
                        2 -> showQuickNotes()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
            val targetTab = when (currentTabType) {
                QuestionTabType.QUESTIONS -> getTabAt(0)
                QuestionTabType.MY_QUESTIONS -> getTabAt(1)
                QuestionTabType.QUICK_NOTES -> getTabAt(2)
            }

            // Select the tab
            selectTab(targetTab)

            // Manually trigger the selection action if it's already selected
            if (targetTab?.isSelected == true) {
                when (targetTab.position) {
                    0 -> showQuestions()
                    1 -> showMyQuestions()
                    2 -> showQuickNotes()
                }
            }
        }
    }


    private fun setupRecyclerView() {
        questionsListAdapter.onItemClickListener = { view, data, position ->
            if (view.id == R.id.sactive) {
                performActiveInActiveAction(data, position)
            } else if (view.id == R.id.llcDelete) {
                performDeleteAction(data, position)
            } else if (view.id == R.id.llcEdit) {
                val bundle = Bundle()
                bundle.putInt("question_tab_type", currentTabType.ordinal)
                bundle.putParcelable("question_info", data)
                findNavController().navigate(
                    R.id.action_evaQuestionsFragment_to_evaCreateQuestionFragment,
                    bundle
                )
            }
        }
        binding.recyclerViewOptions.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = questionsListAdapter
        }

        quickNoteAdapter.onItemClickListener = { note, action ->
            if (action == "update") {
                viewModel.updateQuickNote(note) {
                    fetchQuickNotesFromDb()
                }
            } else {
                viewModel.deleteNote(note) {
                    fetchQuickNotesFromDb()
                }
            }
        }

        binding.incQuickNote.rvQuickNotes.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = quickNoteAdapter
        }
    }

    private fun performActiveInActiveAction(
        data: QuestionInfo,
        position: Int
    ) {
        val status = data.status
        data.status = if (status == 1) 0 else 1
        viewModel.updateQuestionIntoDb(data)
    }

    private fun performDeleteAction(
        data: QuestionInfo,
        position: Int
    ) {
        data.isDeleted = 1
        viewModel.updateQuestionIntoDb(data)
    }

    private fun showQuestions() {
        currentTabType = QuestionTabType.QUESTIONS
        fetchQuestionFromDb()
        binding.llcQuestion.visibility = View.VISIBLE
        binding.addQuestion.visibility = View.GONE
        binding.incQuickNote.llcQuickNoteMain.visibility = View.GONE
    }

    private fun showMyQuestions() {
        currentTabType = QuestionTabType.MY_QUESTIONS
        fetchQuestionFromDb()
        binding.llcQuestion.visibility = View.VISIBLE
        binding.addQuestion.visibility = View.VISIBLE
        binding.incQuickNote.llcQuickNoteMain.visibility = View.GONE
        binding.addQuestion.text = "Add Question"
    }

    private fun showQuickNotes() {
        currentTabType = QuestionTabType.QUICK_NOTES
        updateRecyclerView(emptyList())
        binding.tvNoQuestion.visibility = View.GONE
        binding.llcQuestion.visibility = View.GONE
        binding.incQuickNote.llcQuickNoteMain.visibility = View.VISIBLE
        fetchQuickNotesFromDb()
    }

    private fun fetchQuestionFromDb() {
        lifecycleScope.launch {
            val type = when (currentTabType) {
                QuestionTabType.QUESTIONS -> "remote"
                QuestionTabType.MY_QUESTIONS -> "question"
                QuestionTabType.QUICK_NOTES -> "note"
            }

            val questionOptionList = viewModel.fetchQuestionWithOptions(type).firstOrNull()
            if (questionOptionList != null) {
                updateRecyclerView(questionOptionList)
            }
        }
    }

    private fun fetchQuickNotesFromDb() {
        lifecycleScope.launch {
            val quickNoteList = viewModel.getQuickNote().firstOrNull()
            if (quickNoteList != null) {
                showQuickNotesOnUI(quickNoteList)
            }
        }
    }

    private fun showQuickNotesOnUI(quickNoteList: List<QuickNote>) {
        quickNoteAdapter.setQuickNoteList(quickNoteList)
    }

    private fun updateRecyclerView(questionInfo: List<QuestionInfo>) {
        binding.tvTotalQuestion.text = "${questionInfo.size} Questions"
        binding.tvNoQuestion.visibility = if (questionInfo.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerViewOptions.visibility =
            if (questionInfo.isNotEmpty()) View.VISIBLE else View.GONE

        when (currentTabType) {
            QuestionTabType.QUESTIONS -> questionsListAdapter.updateData(
                questionInfo,
                currentTabType
            )

            QuestionTabType.MY_QUESTIONS -> questionsListAdapter.updateData(
                questionInfo,
                currentTabType
            )

            QuestionTabType.QUICK_NOTES -> questionsListAdapter.updateData(
                questionInfo,
                currentTabType
            )
        }
    }


    companion object {
        fun newInstance() = EvaQuestionsFragment()
    }
}
