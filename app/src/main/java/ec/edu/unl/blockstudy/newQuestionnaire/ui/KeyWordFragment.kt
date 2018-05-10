package ec.edu.unl.blockstudy.newQuestionnaire.ui

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
import ec.edu.unl.blockstudy.entities.Keyword
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.onTextChangedListener
import kotlinx.android.synthetic.main.fragment_keyword.view.*

class KeyWordFragment : DialogFragment(), DialogInterface.OnShowListener {
    private var btn_action: Button? = null
    private var ib_close: ImageButton? = null
    private var tie_keyword: TextInputEditText? = null
    private var callback: OnkeyWordListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_keyword, null)
        btn_action = view.btn_action
        ib_close = view.ib_close
        tie_keyword = view.tie_keyword
        setupFieldsValidation()
        builder.setView(view)
        val dialog = builder.create()
        //dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_keyword!!)
        onTextChangedListener(fields, btn_action!!)
    }


    override fun onShow(dialog: DialogInterface?) {
        val dialogo = getDialog() as AlertDialog
        if (dialogo != null) {
            btn_action!!.setOnClickListener {
                var keyword = Keyword()
                keyword.description = tie_keyword!!.text.toString()
                callback!!.onAddKeyword(keyword)
                dismiss()
            }

            ib_close!!.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnkeyWordListener) {
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
        fun newInstance(): KeyWordFragment {
            val fragment = KeyWordFragment()
            return fragment
        }
    }

    interface OnkeyWordListener {
        fun onAddKeyword(keyword: Keyword)
    }


}
