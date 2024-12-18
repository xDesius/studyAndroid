package com.example.geoquiz

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var btnTrue: Button
    private lateinit var btnFalse: Button
    private lateinit var tvQuestion: TextView
    private lateinit var btnNext: ImageButton
    private lateinit var btnBack: ImageButton

    private val questionBank
        get() = listOf(
            Question(R.string.question_rus, true),
            Question(R.string.question_fr, true),
            Question(R.string.question_gr, false),
            Question(R.string.question_baikal,true)
        )
    private var currentIndex = 0

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        btnTrue = findViewById(R.id.btnTrue)
        btnFalse = findViewById(R.id.btnFalse)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)
        tvQuestion = findViewById(R.id.tvQuestion)


        btnNext.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        }
        btnBack.setOnClickListener {
            if( (currentIndex - 1 ) < 0){
                currentIndex = questionBank.size - 1
            } else{
                currentIndex--;
            }
            updateQuestion()
        }

        btnTrue.setOnClickListener {
            checkAnswer(true)
        }
        btnFalse.setOnClickListener {
            checkAnswer(false)
        }
        updateQuestion()
    }

    private fun updateQuestion(){
        val questionTextResId = questionBank[currentIndex].textResId
        tvQuestion.setText(questionTextResId)
    }
    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = questionBank[currentIndex].answer

        val messageResId: Int
        if( userAnswer == correctAnswer){
            messageResId = R.string.correct_toast
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        } else{
            messageResId = R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()



    }
}