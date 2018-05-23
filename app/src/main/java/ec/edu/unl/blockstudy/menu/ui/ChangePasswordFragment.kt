package ec.edu.unl.blockstudy.menu.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.ImageButton
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.onTextChangedListener
import kotlinx.android.synthetic.main.fragment_change_password.view.*

class ChangePasswordFragment : DialogFragment(), DialogInterface.OnShowListener {
    private var btn_change: Button? = null
    private var ib_close: ImageButton? = null
    private var tie_password: TextInputEditText? = null
    private var tie_passwordOdl: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_change_password, null)
        btn_change = view.btn_change
        ib_close = view.ib_close
        tie_password = view.tie_password
        tie_passwordOdl = view.tie_passwordOdl
        setupFieldsValidation()
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_password!!)
        fields.add(tie_passwordOdl!!)
        onTextChangedListener(fields, btn_change!!)
    }


    override fun onShow(dialog: DialogInterface?) {
            btn_change!!.setOnClickListener {
                (parentFragment as MenuFragment).onUpdatePassword(tie_password!!.text.toString(), tie_passwordOdl!!.text.toString())
                dismiss()
            }

            ib_close!!.setOnClickListener {
                dismiss()
            }
    }


    companion object {
        fun newInstance(): ChangePasswordFragment {
            val fragment = ChangePasswordFragment()
            return fragment
        }
    }
}
