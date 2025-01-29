package com.example.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"
private const val EXTRA_ANSWER_IS_SHOWN = "com.example.geoquiz.answer_is_shown"
private const val KEY_ANSWER_IS_SHOWN = "answerIsShown"

class CheatActivity : AppCompatActivity() {

    private lateinit var tvAnswer: TextView
    private lateinit var btnShowAnswer: Button
    private var answerIsTrue = false

    private val cheatViewModel: CheatViewModel by lazy {
        ViewModelProvider(this)[CheatViewModel::class.java]
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ANSWER_IS_SHOWN, cheatViewModel.isAnswerShown)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cheat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cheatViewModel.isAnswerShown = savedInstanceState?.getBoolean(KEY_ANSWER_IS_SHOWN, false) ?: false

        btnShowAnswer = findViewById(R.id.show_answer_btn)
        tvAnswer = findViewById(R.id.answer_text_view)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        if (cheatViewModel.isAnswerShown){
            setAnswerShownResult(true, EXTRA_ANSWER_IS_SHOWN)
        }

        btnShowAnswer.setOnClickListener {
            val answerText = when{
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }

            tvAnswer.setText(answerText)
            cheatViewModel.isAnswerShown = true
            setAnswerShownResult(cheatViewModel.isAnswerShown, EXTRA_ANSWER_IS_SHOWN)
        }

    }
    private fun setAnswerShownResult(isAnswerShown: Boolean, Extra: String){
        val data = Intent().apply {
            putExtra(Extra,isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object{
        fun newIntent(packageContext: Context, answerIsTrue: Boolean ): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}