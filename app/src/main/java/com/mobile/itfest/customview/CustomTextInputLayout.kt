package com.mobile.itfest.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import com.mobile.itfest.R

class CustomTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        editText?.let { editText ->
            if (editText.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD + 1) {
                editText.addTextChangedListener {
                    if (editText.text.toString().length < 8) {
                        val msg = context.getString(R.string.error_password)
                        isErrorEnabled = true
                        error = msg
                    } else {
                        error = null
                        isErrorEnabled = false
                    }
                }
            }
            if (editText.inputType == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS + 1) {
                editText.addTextChangedListener {
                    if (!isEmailValid(editText.text.toString())) {
                        val msg = context.getString(R.string.error_email)
                        isErrorEnabled = true
                        error = msg
                    } else {
                        error = null
                        isErrorEnabled = false
                    }
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
        return emailRegex.matches(email)
    }
}