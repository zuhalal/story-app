package com.zuhal.storyapp.customView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.zuhal.storyapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.password_hint)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isEmpty()) {
                    error = context.getString(R.string.password_empty)
                } else if (s.toString().length < 8) {
                    error = context.getString(R.string.invalid_password)
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }
}