package ec.com.dovic.aprendiendo.myrepository.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.util.BaseActivitys.Companion.onTextChangedListener
import kotlinx.android.synthetic.main.fragment_new_questionnaire.view.*

class NewQuestionarieFragment : DialogFragment(), DialogInterface.OnShowListener {


    private var btn_create: Button? = null
    private var ib_close: ImageButton? = null
    private var tie_description: TextInputEditText? = null
    private var tie_title: TextInputEditText? = null
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_new_questionnaire, null)
        btn_create = view.btn_create
        ib_close = view.ib_close
        tie_title = view.tie_title
        tie_description = view.tie_discription
        progress = view.progressbar
        setupFieldsValidation()
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }


    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_title!!)
        onTextChangedListener(fields, btn_create!!)
    }


    override fun onShow(dialog: DialogInterface?) {
        btn_create!!.setOnClickListener {
            (activity as MyRepositoryActivity).onCreateQuestionnaire(tie_description!!.text.toString(), tie_title!!.text.toString())
            btn_create!!.visibility = View.INVISIBLE
            progress.visibility = View.VISIBLE
        }

        ib_close!!.setOnClickListener {
            dismiss()
        }
    }

    fun showButtonCreate() {
        btn_create!!.visibility = View.VISIBLE
        progress.visibility = View.GONE
    }

    companion object {
        fun newInstance(): NewQuestionarieFragment {
            val fragment = NewQuestionarieFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}
