package ec.edu.unl.blockstudy.questionnaireResume.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatRatingBar
import android.widget.Button
import android.widget.ImageButton
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.onTextChangedListener
import kotlinx.android.synthetic.main.fragment_raiting.view.*

class RatingFragment : DialogFragment(), DialogInterface.OnShowListener {
    private var btn_action: Button? = null
    private var ib_close: ImageButton? = null
    private var rating: AppCompatRatingBar? = null
    private var tie_comment: TextInputEditText? = null
    private var callback: OnRatingListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_raiting, null)
        btn_action = view.btn_action
        ib_close = view.ib_close
        tie_comment = view.tie_comment
        rating = view.rating
        setupFieldsValidation()
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_comment!!)
        onTextChangedListener(fields, btn_action!!)
    }


    override fun onShow(dialog: DialogInterface?) {
        val dialogo = getDialog() as AlertDialog
        if (dialogo != null) {
            btn_action!!.setOnClickListener {
                //callback!!.onRecoveryPassword(tie_email!!.text.toString())
                callback!!.onSetRaiting(rating!!.rating.toDouble(), tie_comment!!.text.toString())
                dismiss()
            }

            ib_close!!.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRatingListener) {
            callback = context
        } else {
            throw  RuntimeException(context.toString()
                    + " must implement OnRatingListener");
        }
    }

    override fun onDetach() {
        super.onDetach();
        callback = null;
    }

    companion object {
        fun newInstance(): RatingFragment {
            val fragment = RatingFragment()
            return fragment
        }
    }

    interface OnRatingListener {
        fun onSetRaiting(value: Double, comment: String)
    }


}
