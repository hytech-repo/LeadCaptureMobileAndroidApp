package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaQuestionsBinding
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.QuestionWithOptions
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.QuestionTabType
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaQuestionsFragment :
    BaseFragment<FragmentEvaQuestionsBinding, EvaQuestionsViewModel>(EvaQuestionsViewModel::class.java) {

    private var mContext: Context? = null
    private lateinit var questionsListAdapter: QuestionsListAdapter
    private var currentTabType = QuestionTabType.QUESTIONS


    private val questionList = listOf(
        QuestionWithOptions(
            question = QuestionInfo(
                question = "What solutions are you currently using?"
            )
        ),
        QuestionWithOptions(
            question = QuestionInfo(
                question = "What features do you value the most?"
            )
        ),
        QuestionWithOptions(
            question = QuestionInfo(
                question = "What is your decision timeline?"
            )
        )
    )

    private val myQuestionsList = listOf(
        "myquestion",
        "Do you want more info on pricing?",
        "Would you like a follow-up call?"
    )

    private val quickNotesList = listOf(
        "quicknotes",
        "Client was unsure about package benefits.",
        "Interested in automation features."
    )

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
        // Navigate to Add Question screen
        binding.addQuestion.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("question_tab_type", currentTabType.ordinal)
            findNavController().navigate(R.id.action_evaQuestionsFragment_to_evaCreateQuestionFragment, bundle)
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
        questionsListAdapter = QuestionsListAdapter(mContext!!, currentTabType)
        binding.recyclerViewOptions.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = questionsListAdapter
        }
    }

    private fun showQuestions() {
        currentTabType = QuestionTabType.QUESTIONS
        fetchQuestionFromDb()
        binding.addQuestion.visibility = View.GONE
    }

    private fun showMyQuestions() {
        currentTabType = QuestionTabType.MY_QUESTIONS
        fetchQuestionFromDb()
        binding.addQuestion.visibility = View.VISIBLE
        binding.addQuestion.text = "Add Question"
    }

    private fun showQuickNotes() {
        currentTabType = QuestionTabType.QUICK_NOTES
        fetchQuestionFromDb()
        binding.addQuestion.visibility = View.VISIBLE
        binding.addQuestion.text = "Add Quick Notes"
    }

    private fun fetchQuestionFromDb() {
        lifecycleScope.launch {
            if (currentTabType != QuestionTabType.QUESTIONS) {
                val type = if (currentTabType == QuestionTabType.MY_QUESTIONS) "question" else "note"
                val questionOptionList = viewModel.fetchQuestionWithOptions(type).firstOrNull()
                if (questionOptionList != null) {
                    updateRecyclerView(questionOptionList)
                }
            } else {
                updateRecyclerView(questionList)
            }
        }
    }

    private fun updateRecyclerView(questionOptionList: List<QuestionWithOptions>) {
        questionsListAdapter = QuestionsListAdapter(mContext!!, currentTabType)
        binding.recyclerViewOptions.adapter = questionsListAdapter

        when (currentTabType) {
            QuestionTabType.QUESTIONS -> questionsListAdapter.updateData(questionOptionList)
            QuestionTabType.MY_QUESTIONS -> questionsListAdapter.updateData(questionOptionList)
            QuestionTabType.QUICK_NOTES -> questionsListAdapter.updateData(questionOptionList)
        }
    }


    companion object {
        fun newInstance() = EvaQuestionsFragment()
    }
}
