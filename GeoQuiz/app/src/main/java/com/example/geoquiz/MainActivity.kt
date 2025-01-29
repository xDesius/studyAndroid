package com.example.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

private const val KEY_INDEX = "index"
private const val KEY_CORRECT_ANSWERS = "correctAnswers"
private const val KEY_ALL_ANSWERS = "allAnswers"
private const val KEY_ANSWERED_ARRAY = "arrayAnswered"
private const val REQUEST_CODE_CHEAT = 0
private const val EXTRA_ANSWER_IS_SHOWN = "com.example.geoquiz.answer_is_shown"

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var btnTrue: Button
    private lateinit var btnFalse: Button
    private lateinit var tvQuestion: TextView
    private lateinit var btnNext: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var btnCheat: Button


    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    private val cheatActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            val resultData = data?.getStringExtra(EXTRA_ANSWER_IS_SHOWN)
            quizViewModel.isCheater = true
            checkAnswer(!(quizViewModel.currentQuestionAnswer))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX,  quizViewModel.currentIndex)
        outState.putInt(KEY_CORRECT_ANSWERS, quizViewModel.correctAnswers)
        outState.putInt(KEY_ALL_ANSWERS, quizViewModel.allAnswers)
        outState.putIntegerArrayList(KEY_ANSWERED_ARRAY, quizViewModel.listOfAnswered)
    }

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

        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0)  ?: 0
        quizViewModel.allAnswers = savedInstanceState?.getInt(KEY_ALL_ANSWERS, 0) ?: 0
        quizViewModel.correctAnswers = savedInstanceState?.getInt(KEY_CORRECT_ANSWERS, 0) ?: 0
        quizViewModel.listOfAnswered = savedInstanceState?.getIntegerArrayList(KEY_ANSWERED_ARRAY) ?: ArrayList()

        btnTrue = findViewById(R.id.btnTrue)
        btnFalse = findViewById(R.id.btnFalse)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)
        tvQuestion = findViewById(R.id.tvQuestion)
        btnCheat = findViewById(R.id.cheat_button)

        btnCheat.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatActivityResultLauncher.launch(intent)
        }

        btnNext.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        btnBack.setOnClickListener {
            quizViewModel.moveToBack()
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
        val questionTextResId = quizViewModel.currentQuestionText
        tvQuestion.setText(questionTextResId)
    }
    @SuppressLint("ShowToast")
    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when{
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        quizViewModel.isCheater = false
        if( userAnswer == correctAnswer){
            if(quizViewModel.currentIndex !in quizViewModel.listOfAnswered){
                quizViewModel.listOfAnswered.add(quizViewModel.currentIndex)
                quizViewModel.correctAnswers++
                quizViewModel.allAnswers++
            }
            quizViewModel.currentIndex = (quizViewModel.currentIndex + 1) % quizViewModel.getQuiestionBankSize()
            updateQuestion()
        } else{
            if (quizViewModel.currentIndex !in quizViewModel.listOfAnswered) {
                quizViewModel.listOfAnswered.add(quizViewModel.currentIndex)
                quizViewModel.allAnswers++
            }
            quizViewModel.currentIndex = (quizViewModel.currentIndex + 1) % quizViewModel.getQuiestionBankSize()
            updateQuestion()
        }

        if (quizViewModel.allAnswers == quizViewModel.getQuiestionBankSize()){
            resumeResults()
        } else{
            val toastAnswer = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            toastAnswer.addCallback(object: Toast.Callback(){
                override fun onToastShown() {
                    super.onToastShown()
                    btnTrue.isClickable=false
                    btnFalse.isClickable=false
                }

                override fun onToastHidden() {
                    super.onToastHidden()
                    btnFalse.isClickable=true
                    btnTrue.isClickable=true
                }
            })
            toastAnswer.show()
        }

    }

    private fun resumeResults(){
        val result = if(quizViewModel.correctAnswers != 0){
            (quizViewModel.correctAnswers * 100) / quizViewModel.allAnswers
        } else{ 0}
        val toastResult = Toast.makeText(this,"Ваш результат: $result %", Toast.LENGTH_SHORT)
        quizViewModel.correctAnswers = 0
        quizViewModel.allAnswers = 0
        toastResult.show()
        quizViewModel.listOfAnswered = ArrayList()
    }

}