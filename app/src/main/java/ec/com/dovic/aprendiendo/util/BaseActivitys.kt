package ec.com.dovic.aprendiendo.util

import android.app.ProgressDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import ec.com.dovic.aprendiendo.R

/**
 * Created by victor on 25/1/18.
 */
class BaseActivitys() {

    /*Metodo para validar el email*/
    companion object {
        var progressDialog: ProgressDialog? = null

        fun validateFieldEmail(context: Context, field: TextInputEditText): Boolean {
            if (Patterns.EMAIL_ADDRESS.matcher(field.text.toString()).matches()) {
                field.setError(null)
                return true;
            } else {
                field.setError(context.resources.getString(R.string.login_edittext_error_invalid_password))
                return false
            }
        }

        fun onTextChangedListener(fields: ArrayList<TextInputEditText>, button: Button) {
            fields.forEach { f ->
                f.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        button.isEnabled = validateBlank(fields)
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
            }
        }

        fun validateBlank(fields: ArrayList<TextInputEditText>): Boolean {
            var enable = true
            fields.forEach { f ->
                if (f.text.toString().isBlank())
                    enable = false
            }
            return enable
        }

        fun showToastMessage(context: Context, message: Any, duration: Int) {
            Toast.makeText(context, getStringMessage(context, message), duration).show()
        }

        fun getStringMessage(context: Context, message: Any): String {
            if (message is Int) {
                return context.getString(message)
            } else {
                return message as String
            }
        }

        fun showProgressDialog(context: Context, message: Any) {
            progressDialog = ProgressDialog(context)
            progressDialog!!.setMessage(getStringMessage(context, message));
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        fun hideProgressDialog() {
            progressDialog!!.dismiss()
            progressDialog = null
        }

    }
}