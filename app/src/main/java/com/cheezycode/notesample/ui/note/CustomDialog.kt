package com.cheezycode.notesample.ui.note

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.cheezycode.notesample.R

class CustomDialog(context: Context) : Dialog(context) {

    private lateinit var titleTextView: TextView
    private lateinit var messageTextView: TextView
    private lateinit var negativeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog)



        titleTextView = findViewById(R.id.titleTextView)
        messageTextView = findViewById(R.id.messageTextView)
        negativeButton = findViewById(R.id.btn_okay)

        negativeButton.setOnClickListener {
            Dialog(context).dismiss()
        }
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setMessage(message: String) {
        messageTextView.text = message
    }

}
