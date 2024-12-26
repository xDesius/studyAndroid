package com.example.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    private val questionBank
        get() = listOf(
            Question(R.string.question_rus, true),
            Question(R.string.question_fr, true),
            Question(R.string.question_gr, false),
            Question(R.string.question_baikal,true)
        )
    var currentIndex = 0
    var listOfAnswered: ArrayList<Int> = ArrayList<Int>()
    var correctAnswers = 0
    var allAnswers = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    fun moveToNext(){
        currentIndex = (currentIndex + 1) % questionBank.size
    }
    fun moveToBack(){
        if( (currentIndex - 1 ) < 0){
            currentIndex = questionBank.size - 1
        } else{
            currentIndex--;
        }
    }
    fun getQuiestionBankSize() : Int{
        return questionBank.size
    }
}