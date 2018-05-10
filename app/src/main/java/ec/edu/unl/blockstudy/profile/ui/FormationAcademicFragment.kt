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
import kotlinx.android.synthetic.main.fragment_formation_academic.view.*

class FormationAcademicFragment : DialogFragment(), DialogInterface.OnShowListener {


    private var btn_update: Button? = null
    private var ib_close: ImageButton? = null
    private var tie_school: TextInputEditText? = null
    private var tie_title: TextInputEditText? = null
    private var callback: OnUpdateFormationAcademicListener? = null
    private var school: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            school = arguments!!.getString(PARAM_SCHOOL)
            title = arguments!!.getString(PARAM_TITLE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_formation_academic, null)
        btn_update = view.btn_update
        ib_close = view.ib_close
        tie_school = view.tie_school
        tie_title = view.tie_title
        setupFieldsValidation()
        setData()
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }

    fun setData() {
        tie_school!!.setText(school)
        tie_title!!.setText(title)
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_school!!)
        fields.add(tie_title!!)
        onTextChangedListener(fields, btn_update!!)
    }


    override fun onShow(dialog: DialogInterface?) {
        val dialogo = getDialog() as AlertDialog
        if (dialogo != null) {
            btn_update!!.setOnClickListener {
                callback!!.onUpdateAcademic(tie_school!!.text.toString(), tie_title!!.text.toString())
                dismiss()
            }

            ib_close!!.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnUpdateFormationAcademicListener) {
            callback = context
        } else {
            throw  RuntimeException(context.toString()
                    + " must implement OnUpdateFormationAcademicListener");
        }
    }

    override fun onDetach() {
        super.onDetach();
        callback = null;
    }


    companion object {
        val PARAM_SCHOOL = "name"
        val PARAM_TITLE = "lastname"
        fun newInstance(school: String, title: String): FormationAcademicFragment {
            val fragment = FormationAcademicFragment()
            val args = Bundle()
            args.putString(PARAM_SCHOOL, school)
            args.putString(PARAM_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    interface OnUpdateFormationAcademicListener {
        fun onUpdateAcademic(school: String, title: String)
    }
}
