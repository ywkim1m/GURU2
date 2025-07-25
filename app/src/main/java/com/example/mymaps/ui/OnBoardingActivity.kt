package com.example.mymaps.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaps.R
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText


class OnBoardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val editTextName = findViewById<EditText>(R.id.etName)
        val btnStart = findViewById<Button>(R.id.btnStart)

        // EditText에 입력이 있으면 버튼 활성화
        editTextName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnStart.isEnabled = !s.isNullOrBlank()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnStart.setOnClickListener {
            val name = editTextName.text.toString()
            // name 저장 후 화면 이동
        }
    }
}
