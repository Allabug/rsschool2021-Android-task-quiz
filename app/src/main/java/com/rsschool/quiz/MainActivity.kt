package com.rsschool.quiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.rsschool.quiz.databinding.ActivityMainBinding
import java.lang.StringBuilder


class MainActivity : AppCompatActivity(), QuizFragment.DataPassListener,
    ResultFragment.DataListenerResult {

    private lateinit var binding: ActivityMainBinding

    private var currentIndex: Int = 0

    private var questionBank = arrayListOf(
        Question(
            index = 0,
            question = R.string.question1,
            answers = listOf("Меркурий", "Марс", "Венера", "Земля", "Сатурн"),
            answerPosition = 3,
            checkedRadioButtonId = 0,
            indexCheckedRadioButton = 0,
            title = "Question 1"
        ),
        Question(
            index = 1,
            question = R.string.question2,
            answers = listOf("На трех китах", "Плоскоземельная", "Солнечная", "Лунная", "Звездная"),
            answerPosition = 2,
            checkedRadioButtonId = 0,
            indexCheckedRadioButton = 0,
            title = "Question 2"
        ),
        Question(
            index = 2,
            question = R.string.question3,
            answers = listOf("За год", "За неделю", "За месяц", "За сутки", "За час"),
            answerPosition = 0,
            checkedRadioButtonId = 0,
            indexCheckedRadioButton = 0,
            title = "Question 3"
        ),
        Question(
            index = 3,
            question = R.string.question4,
            answers = listOf("10", "1", "0", "4", "2"),
            answerPosition = 1,
            checkedRadioButtonId = 0,
            indexCheckedRadioButton = 0,
            title = "Question 4"
        ),
        Question(
            index = 4,
            question = R.string.question5,
            answers = listOf("Нептун", "Земля", "Меркурий", "Венера", "Марс"),
            answerPosition = 0,
            checkedRadioButtonId = 0,
            indexCheckedRadioButton = 0,
            title = "Question 5"
        ),
        Question(
            index = 5,
            question = R.string.question6,
            answers = listOf("Земля", "Марс", "Венера", "Юпитер", "Меркурий"),
            answerPosition = 4,
            checkedRadioButtonId = 0,
            indexCheckedRadioButton = 0,
            title = "Question 6"
        ),
    )

    private val results = arrayOfNulls<Boolean>(questionBank.size)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bundle = Bundle()
        bundle.putParcelableArrayList("productList", questionBank)
        openNextFragment()
    }

    private fun openNextFragment() {
        val question = getNextQuestionOrNull()
        val fragment: Fragment

        if (question == null) {
            val numberOfQuestions = questionBank.size.toString()
            val result = results.count { it == true }.toString()
            // open results
            fragment = ResultFragment.newInstance(result, numberOfQuestions)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        } else {
            fragment = QuizFragment.newInstance(question, questionBank.lastIndex)
            if (currentIndex == 0) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack("")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
        }
        currentIndex++
    }

    private fun getNextQuestionOrNull(): Question? {
        if (currentIndex <= questionBank.size - 1) {
            return questionBank[currentIndex]
        }
        return null
    }

    override fun pressNextButton(
        index: Int,
        checkedRadioButtonId: Int,
        indexCheckedRadioButton: Int,
        isAnswerCorrect: Boolean
    ) {
        results[index] = isAnswerCorrect
        questionBank[index].checkedRadioButtonId = checkedRadioButtonId
        questionBank[index].indexCheckedRadioButton = indexCheckedRadioButton
        openNextFragment()
    }

    override fun pressPreviousButton(
        index: Int,
        checkedRadioButtonId: Int,
        indexCheckedRadioButton: Int
    ) {
        questionBank[index].checkedRadioButtonId = checkedRadioButtonId
        questionBank[index].indexCheckedRadioButton = indexCheckedRadioButton
        supportFragmentManager.popBackStack()
        currentIndex--
    }

    override fun shareResult(result: String?) {
        val resultsForSend =
            "Результаты теста \nВаш результат: $result из ${questionBank.size} \n${generateMessage()}".trimMargin()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, resultsForSend)
            type = "text/plain"
        }
        startActivity(sendIntent)
    }

    private fun generateMessage(): String {
        val resultString = StringBuilder()
        for (i in questionBank.indices) {
            val str = "${i + 1}) ${resources.getString(questionBank[i].question)} \n " +
                    "Ваш ответ: ${questionBank[i].answers[questionBank[i].indexCheckedRadioButton]} \n "
            resultString.append(str)

        }
        return resultString.toString()
    }

    override fun backToStart() {
        currentIndex = 0
        questionBank.forEach { it.checkedRadioButtonId = 0 }
        openNextFragment()

    }

    override fun closeQuizApp() {
        finish()
    }
}
