package com.rsschool.quiz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    interface DataListenerResult {
        fun shareResult(result: String?)
        fun backToStart()
        fun closeQuizApp()
    }

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private var dataListenerResult: DataListenerResult? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DataListenerResult)
            dataListenerResult = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val result = arguments?.getString(RESULT)
        val numberOfQuestion = arguments?.get(NUMBER_OF_QUESTION)
        val resultTextView = "Result: $result out of $numberOfQuestion"
        binding.textViewResult.text = resultTextView

        binding.imageButtonShare.setOnClickListener {
            dataListenerResult?.shareResult(result)
        }

        binding.imageButtonBack.setOnClickListener {
            dataListenerResult?.backToStart()
        }

        binding.imageButtonCloseApp.setOnClickListener {
            dataListenerResult?.closeQuizApp()
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        dataListenerResult = null
    }

    companion object {
        private const val RESULT = "resultQuiz"
        private const val NUMBER_OF_QUESTION = "numberOfQuestion"
        fun newInstance(result: String, numberOfQuestion: String): Fragment {
            return ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(RESULT, result)
                    putString(NUMBER_OF_QUESTION, numberOfQuestion)
                }
            }
        }
    }
}