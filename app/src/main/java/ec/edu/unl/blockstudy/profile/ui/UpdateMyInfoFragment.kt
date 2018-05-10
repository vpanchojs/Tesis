package ec.edu.unl.blockstudy.profile.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.ImageButton
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.onTextChangedListener
import kotlinx.android.synthetic.main.fragment_update_my_info.view.*

class UpdateMyInfoFragment : DialogFragment(), DialogInterface.OnShowListener {


    private var btn_update: Button? = null
    private var ib_close: ImageButton? = null
    private var tie_name: TextInputEditText? = null
    private var tie_lastname: TextInputEditText? = null
    private var tie_email: TextInputEditText? = null
    private var callback: OnUpdateInfoListener? = null
    private var name: String = ""
    private var lastname: String = ""
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            name = arguments!!.getString(PARAM_NAME)
            lastname = arguments!!.getString(PARAM_LASTNAME)
            email = arguments!!.getString(PARAM_EMAIL)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_update_my_info, null)
        btn_update = view.btn_update
        ib_close = view.ib_close
        tie_name = view.tie_name
        tie_lastname = view.tie_lastname
        tie_email = view.tie_comment
        setupFieldsValidation()
        setData()
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }

    fun setData() {
        tie_name!!.setText(name)
        tie_lastname!!.setText(lastname)
        tie_email!!.setText(email)
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_email!!)
        fields.add(tie_lastname!!)
        fields.add(tie_name!!)
        onTextChangedListener(fields, btn_update!!)
    }


    override fun onShow(dialog: DialogInterface?) {
        val dialogo = getDialog() as AlertDialog
        if (dialogo != null) {
            btn_update!!.setOnClickListener {
                callback!!.onUpdateInfor(tie_name!!.text.toString(), tie_lastname!!.text.toString(), tie_email!!.text.toString())
                dismiss()
            }

            ib_close!!.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnUpdateInfoListener) {
            callback = context
        } else {
            throw  RuntimeException(context.toString()
                    + " must implement OnUpdateInfoListener");
        }
    }

    override fun onDetach() {
        super.onDetach();
        callback = null;
    }


    companion object {
        val PARAM_NAME = "name"
        val PARAM_LASTNAME = "lastname"
        val PARAM_EMAIL = "email"
        fun newInstance(name: String, lastname: String, email: String): UpdateMyInfoFragment {
            val fragment = UpdateMyInfoFragment()
            val args = Bundle()
            args.putString(PARAM_NAME, name)
            args.putString(PARAM_LASTNAME, lastname)
            args.putString(PARAM_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }

    interface OnUpdateInfoListener {
        fun onUpdateInfor(name: String, lastname: String, email: String)
    }
}
