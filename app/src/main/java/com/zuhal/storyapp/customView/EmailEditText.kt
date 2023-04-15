package com.zuhal.storyapp.customView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.zuhal.storyapp.R

class EmailEditText : AppCompatEditText, View.OnTouchListener {
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

        hint = context.getString(R.string.email_hint)

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
                    error = context.getString(R.string.empty_email)
                } else if (!isValidEmail(s)) {
                    error = context.getString(R.string.invalid_email)
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }
}