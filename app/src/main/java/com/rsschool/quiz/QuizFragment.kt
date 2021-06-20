package com.rsschool.quiz


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var dataPassListener: DataPassListener? = null
    private lateinit var questionQuiz: Question
    private val index by lazy { questionQuiz.index }
    private val backListener = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onPreviousClicked(index)
        }
    }

    interface DataPassListener {
        fun pressNextButton(
            index: Int,
            checkedRadioButtonId: Int,
            indexCheckedRadioButton: Int,
            isAnswerCorrect: Boolean
        )

        fun pressPreviousButton(index: Int, checkedRadioButtonId: Int, indexCheckedRadioButton: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DataPassListener)
            dataPassListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionQuiz = getQuestion(this)
        requireActivity().onBackPressedDispatcher.addCallback(this, backListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        changeStyle(questionQuiz.index)
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentRadioButtonId = questionQuiz.checkedRadioButtonId

        with(binding.toolbar) {
            title = questionQuiz.title
            if (index != 0) setNavigationIcon(R.drawable.ic_baseline_chevron_left_24)
            setNavigationOnClickListener {
                onPreviousClicked(index)
            }
        }

        val maxIndex = arguments?.getInt("MaxIndex") ?: -1

        if (index == maxIndex) binding.nextButton.text = getString(R.string.submit_button)

        if (index == 0) {
            //если мы на первом экране
            binding.nextButton.isEnabled = false
            binding.previousButton.isEnabled = false
            backListener.isEnabled = false
        } else {
            binding.nextButton.isEnabled = false
            binding.previousButton.isEnabled = true
            backListener.isEnabled = true
        }

        binding.question.text = resources.getString(questionQuiz.question)
        binding.optionOne.text = questionQuiz.answers[0]
        binding.optionTwo.text = questionQuiz.answers[1]
        binding.optionThree.text = questionQuiz.answers[2]
        binding.optionFour.text = questionQuiz.answers[3]
        binding.optionFive.text = questionQuiz.answers[4]

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            currentRadioButtonId = id
            binding.nextButton.isEnabled = true
        }

        val radioButton = binding.radioGroup.findViewById<RadioButton>(currentRadioButtonId)
        if (radioButton != null) {
            radioButton.isChecked = true
            binding.nextButton.isEnabled = true
        }

        binding.nextButton.setOnClickListener {
            //переключиться на следующий вопрос
            val checkedRadioButtonId = binding.radioGroup.checkedRadioButtonId
            val correctRadioButton =
                binding.radioGroup[questionQuiz.answerPosition] as RadioButton
            val positionCheckedRadioButton = getPositionRadioButton()
            val isAnswerCorrect = correctRadioButton.isChecked

            dataPassListener?.pressNextButton(
                index,
                checkedRadioButtonId,
                positionCheckedRadioButton,
                isAnswerCorrect
            )
        }
        binding.previousButton.setOnClickListener {
            //переключиться на предыдущий вопрос
            onPreviousClicked(index)
        }
    }

    private fun onPreviousClicked(index: Int) {
        val checkedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        val positionCheckedRadioButton = getPositionRadioButton()
        val indexForStyle = index - 1
        changeStyle(indexForStyle)
        dataPassListener?.pressPreviousButton(
            index,
            checkedRadioButtonId,
            positionCheckedRadioButton
        )
    }

    private fun getPositionRadioButton(): Int {
        return when (binding.radioGroup.checkedRadioButtonId) {
            binding.optionOne.id -> 0
            binding.optionTwo.id -> 1
            binding.optionThree.id -> 2
            binding.optionFour.id -> 3
            binding.optionFive.id -> 4
            else -> -1
        }
    }

    private fun changeStyle(index: Int) {
        val window: Window = requireActivity().window
        val color: Int
        val theme: Int
        when (index) {
            0 -> {
                color = R.color.deep_orange_100_dark
                theme = R.style.Theme_Quiz_First
            }
            1 -> {
                color = R.color.yellow_100_dark
                theme = R.style.Theme_Quiz_Second
            }
            2 -> {
                color = R.color.cyan_100_dark
                theme = R.style.Theme_Quiz_Third
            }
            3 -> {
                color = R.color.deep_purple_100_dark
                theme = R.style.Theme_Quiz_Fourth
            }
            4 -> {
                color = R.color.light_green_100_dark
                theme = R.style.Theme_Quiz_Fifth
            }
            5 -> {
                color = R.color.pink_100_dark
                theme = R.style.Theme_Quiz_Sixth
            }
            else -> {
                color = R.color.deep_orange_100_dark
                theme = R.style.Theme_Quiz
            }
        }
        window.statusBarColor =
            ContextCompat.getColor(requireContext(), color)
        requireContext().theme.applyStyle(theme, true)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        dataPassListener = null
    }


    companion object {
        private const val KEY_QUESTION = "question"
        private const val MAX_INDEX = "MaxIndex"

        fun newInstance(question: Question, maxIndex: Int): Fragment {
            return QuizFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_QUESTION, question)
                    putInt(MAX_INDEX, maxIndex)
                }
            }
        }

        private fun getQuestion(fragment: QuizFragment): Question {
            return fragment.requireArguments().get(KEY_QUESTION) as Question
        }

    }
}
