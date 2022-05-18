package com.bignerdranch.android.geoquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.android.geoquiz.CheatActivity.Companion.EXTRA_ANSWER_SHOWN

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextQuestionTextView: TextView
    private lateinit var nextQuestionLinearLayout: View

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        nextQuestionTextView = findViewById(R.id.question_text_view)
        nextQuestionLinearLayout = findViewById(R.id.question_constraint_layout)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateButtonsEnabledState()
            updateQuestion()
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateButtonsEnabledState()
            updateQuestion()
        }

        nextQuestionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateButtonsEnabledState()
            updateQuestion()
        }

        nextQuestionLinearLayout.setOnClickListener {
            quizViewModel.moveToNext()
            updateButtonsEnabledState()
            updateQuestion()
        }

        cheatButton.setOnClickListener {
            startCheatActivity()
        }

        updateQuestion()

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        nextQuestionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        trueButton.isEnabled = false
        falseButton.isEnabled = false
        quizViewModel.questionIsAnswered()

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        if (userAnswer == correctAnswer) {
            pointForCorrectAnswer()
        }

        val toastAnswerStatus = Toast.makeText(
            this,
            messageResId,
            Toast.LENGTH_SHORT
        )
        toastAnswerStatus.setGravity(Gravity.TOP, 0, 300)
        toastAnswerStatus.show()

        val scoreToast = Toast.makeText(
                this,
                "Twój wynik to " +
                        "${(quizViewModel.score / quizViewModel.questionBankSize * 100).toInt()}%",
                Toast.LENGTH_LONG
            )
        scoreToast.setGravity(Gravity.TOP, 0, 150)

        if (quizViewModel.questionBankAreAnswered) {
            toastAnswerStatus.show()
            scoreToast.show()
        } else {
            toastAnswerStatus.show()
        }
    }

    private fun updateButtonsEnabledState() {
        val isQuestionAnswered = quizViewModel.isCurrentQuestionAnswered
        trueButton.isEnabled = !isQuestionAnswered
        falseButton.isEnabled = !isQuestionAnswered
    }

    private fun pointForCorrectAnswer() {
        quizViewModel.score++
    }

    private fun startCheatActivity() {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val intent = CheatActivity.newIntent(this@MainActivity, correctAnswer)
        getResult.launch(intent)
    }

    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
       if (it.resultCode == Activity.RESULT_OK) {
           quizViewModel.isCheater = it.data?.getBooleanExtra(
               EXTRA_ANSWER_SHOWN,
               false
           ) ?: false
       }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_INDEX = "index"
    }
}