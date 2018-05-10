package ec.edu.unl.blockstudy.questionsComplete.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.ImageButton
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Questionaire
import kotlinx.android.synthetic.main.fragment_info_questionnaire.view.*

class InfoQuestionnaireFragment : DialogFragment(), DialogInterface.OnShowListener {
    private var ib_close: ImageButton? = null
    lateinit var questionaire: Questionaire

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!arguments!!.isEmpty) {
            questionaire = arguments!!.getParcelable(PARAM_QUESTIONNAIRE)
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_info_questionnaire, null)
        ib_close = view.ib_close
        view.tv_title_questionnaire.setText(questionaire.title)
        view.tv_description.setText(questionaire.description)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }


    override fun onShow(dialog: DialogInterface?) {
        val dialogo = getDialog() as AlertDialog
        if (dialogo != null) {
            ib_close!!.setOnClickListener {
                dismiss()
            }
        }
    }


    companion object {
        val PARAM_QUESTIONNAIRE = "questionnaire"
        fun newInstance(questionaire: Questionaire): InfoQuestionnaireFragment {
            val fragment = InfoQuestionnaireFragment()
            var b = Bundle()
            b.putParcelable(PARAM_QUESTIONNAIRE, questionaire)
            fragment.arguments = b
            return fragment
        }
    }


}
