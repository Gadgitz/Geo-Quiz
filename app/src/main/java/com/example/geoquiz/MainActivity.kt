package com.example.geoquiz

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.geoquiz.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val quizViewModel: QuizViewModel by viewModels()
    private var correctAnswers = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        binding.questionTextView.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            disableButtonsForCurrentQuestion()
        }

        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            disableButtonsForCurrentQuestion()
        }

        binding.nextButton.setOnClickListener {
            if (quizViewModel.allQuestionsAnswered()) {
                showPercentageScore()
                binding.nextButton.isEnabled = false
            } else {
                quizViewModel.moveToNext()
                updateQuestion()
            }
        }

        binding.previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
        }

        updateQuestion()
    }

    private fun disableButtonsForCurrentQuestion() {
        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false
    }

    private fun updateQuestion() {
        binding.questionTextView.setText(quizViewModel.currentQuestionText)
        binding.trueButton.isEnabled = !quizViewModel.isCurrentQuestionAnswered()
        binding.falseButton.isEnabled = !quizViewModel.isCurrentQuestionAnswered()
    }

    private fun showPercentageScore() {
        val percentageScore = (correctAnswers.toDouble() / quizViewModel.questionBankSize) * 100
        val message = getString(R.string.percentage_score, percentageScore.toInt())
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = if (userAnswer == correctAnswer) {
            correctAnswers++
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        quizViewModel.markCurrentQuestionAnswered()

        if (quizViewModel.allQuestionsAnswered()) {
            showPercentageScore()
            binding.nextButton.isEnabled = false
        }
    }
}