package ec.com.dovic.aprendiendo.login.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.ImageButton
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.util.BaseActivitys.Companion.onTextChangedListener
import kotlinx.android.synthetic.main.fragment_recovery_password.view.*

class RecoveryPasswordFragment : DialogFragment(), DialogInterface.OnShowListener {
    private var btn_recovery: Button? = null
    private var ib_close: ImageButton? = null
    private var tie_email: TextInputEditText? = null
    private var callback: OnRecoveryPasswordListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_recovery_password, null)
        btn_recovery = view.btn_recovery
        ib_close = view.ib_close
        tie_email = view.tie_comment
        setupFieldsValidation()
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_email!!)
        onTextChangedListener(fields, btn_recovery!!)
    }


    override fun onShow(dialog: DialogInterface?) {
        val dialogo = getDialog() as AlertDialog
        if (dialogo != null) {
            btn_recovery!!.setOnClickListener {
                callback!!.onRecoveryPassword(tie_email!!.text.toString())
                dismiss()
            }

            ib_close!!.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRecoveryPasswordListener) {
            callback = context
        } else {
            throw  RuntimeException(context.toString()
                    + " must implement OnRecoveryPasswordListener");
        }
    }

    override fun onDetach() {
        super.onDetach();
        callback = null;
    }

    companion object {
        fun newInstance(): RecoveryPasswordFragment {
            val fragment = RecoveryPasswordFragment()
            return fragment
        }
    }

    interface OnRecoveryPasswordListener {
        fun onRecoveryPassword(email: String)
    }


}
