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

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        nextQuestionTextView = findViewById(R.id.question_text_view)
        nextQuestionLinearLayout = findViewById(R.id.question_constraint_layout)

        trueButton.setOnClickListener {
            checkAnswer(
                true,
                quizViewModel.answerData
            )
        }

        falseButton.setOnClickListener {
            checkAnswer(
                false,
                quizViewModel.answerData
            )
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
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.getCurrentIndex)
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

    private fun checkAnswer(
        userAnswer: Boolean,
        data: QuizViewModel.AnswerData
    ) {

        quizViewModel.questionIsAnswered()
        updateButtonsEnabledState()

        if (userAnswer == data.correctAnswer) {
            pointForCorrectAnswer()
        }

        showToast(data.correctAnswer, data.isCheater, userAnswer)
    }

    private fun showToast(
        correctAnswer: Boolean,
        isCheater: Boolean,
        userAnswer: Boolean
    ) {
        if (quizViewModel.questionBankAreAnswered) {
            answerStatusToast(
                correctAnswer,
                isCheater,
                userAnswer
            )
            scoreToast()
        } else {
            answerStatusToast(
                correctAnswer,
                isCheater,
                userAnswer
            )
        }
    }

    private fun answerStatusToast(
        correctAnswer: Boolean,
        isCheater: Boolean,
        userAnswer: Boolean
    ) {
        val messageResId = when {
            isCheater -> JUDGEMENT_TOAST
            userAnswer == correctAnswer -> CORRECT_TOAST
            else -> INCORRECT_TOAST
        }

        val toastAnswerStatus = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toastAnswerStatus.setGravity(Gravity.TOP, 0, 300)
        toastAnswerStatus.show()
    }

    private fun scoreToast() {
        val toastScore = Toast.makeText(
            this,
            "Twój wynik to " +
                    "${(quizViewModel.getScore / quizViewModel.questionBankSize * 100).toInt()}%",
            Toast.LENGTH_LONG
        )
        toastScore.setGravity(Gravity.TOP, 0, 150)
        toastScore.show()
    }

    private fun updateButtonsEnabledState() {
        val isQuestionAnswered = quizViewModel.isCurrentQuestionAnswered
        trueButton.isEnabled = !isQuestionAnswered
        falseButton.isEnabled = !isQuestionAnswered
    }

    private fun pointForCorrectAnswer() {
        quizViewModel.updateScore()
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
           quizViewModel.updateCheaterStatus(
               it.data?.getBooleanExtra(
                   EXTRA_ANSWER_SHOWN,
                   false
               ) ?: false
           )
       }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_INDEX = "index"
        private const val JUDGEMENT_TOAST = R.string.judgment_toast
        private const val CORRECT_TOAST = R.string.correct_toast
        private const val INCORRECT_TOAST = R.string.incorrect_toast
    }
}