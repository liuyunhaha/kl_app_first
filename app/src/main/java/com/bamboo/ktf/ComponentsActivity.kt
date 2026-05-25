package com.bamboo.ktf


import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ComponentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_components)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews() {
        val textView: TextView = findViewById(R.id.textView)
        val editText: EditText = findViewById(R.id.editText)
        val button: Button = findViewById(R.id.button)
        val checkBox: CheckBox = findViewById(R.id.checkBox)
        val radioButton1: RadioButton = findViewById(R.id.radioButton1)
        val radioButton2: RadioButton = findViewById(R.id.radioButton2)
        val switch: Switch = findViewById(R.id.switch1)

        button.setOnClickListener {
            val inputText = editText.text.toString()
            if (inputText.isNotEmpty()) {
                textView.text = "输入的内容：$inputText"
                Toast.makeText(this, "按钮被点击", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show()
            }
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "选中" else "未选中"
            Toast.makeText(this, "复选框：$status", Toast.LENGTH_SHORT).show()
        }

        radioButton1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "选项1被选中", Toast.LENGTH_SHORT).show()
            }
        }

        radioButton2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "选项2被选中", Toast.LENGTH_SHORT).show()
            }
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "开启" else "关闭"
            Toast.makeText(this, "开关：$status", Toast.LENGTH_SHORT).show()
        }
    }
}
