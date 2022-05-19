package com.bignerdranch.android.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_noriaki, true),
        Question(R.string.question_audi, false),
        Question(R.string.question_kotlin, true),
        Question(R.string.question_deska, false),
        Question(R.string.question_btc, true)
    )

    private var score = 0.0
    private var isCheater = false

    private fun incrementScore() {
        score++
    }

    data class AnswerData(
        val correctAnswer: Boolean,
        val isCheater: Boolean,
        val isQuestionAnswered: Boolean
    )

    val answerData: AnswerData
        get() = AnswerData(
            questionBank[currentIndex].answer,
            isCheater,
            questionBank[currentIndex].isAnswered
        )

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val isCurrentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].isAnswered

    val questionBankSize: Int
        get() = questionBank.size

    val questionBankAreAnswered: Boolean
        get() = questionBank.all { it.isAnswered }

    val getScore: Double
        get() = score

    val isCheaterStatus: Boolean
        get() = isCheater

    var currentIndex = 0

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        if (currentIndex > 0) {
            currentIndex = (currentIndex - 1) % questionBank.size
        } else {
            currentIndex = questionBank.size - 1
        }
    }

    fun questionIsAnswered() {
        questionBank[currentIndex].isAnswered = true
    }

    fun updateScore() {
        incrementScore()
    }

    fun updateCheaterStatus(status: Boolean) {
        isCheater = status
    }

}

