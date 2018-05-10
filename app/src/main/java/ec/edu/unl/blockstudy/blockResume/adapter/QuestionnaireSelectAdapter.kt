package ec.edu.unl.blockstudy.blockResume.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.entities.QuestionnaireBlock
import kotlinx.android.synthetic.main.item_my_questionnarie_select.view.*

/**
 * Created by victor on 24/2/18.
 */
class QuestionnaireSelectAdapter(var data: ArrayList<Questionaire>, var questionnaireBlock: ArrayList<QuestionnaireBlock>, var callback: onQuestionnaireAdapterListener) : RecyclerView.Adapter<QuestionnaireSelectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_my_questionnarie_select, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var questionaire = data.get(position)
        holder!!.view.tv_title.text = questionaire.title

        holder!!.view.cb_select.isChecked = verificate(questionaire)

        holder!!.onCheckListener(questionaire, callback)


    }

    fun verificate(questionaire: Questionaire): Boolean {
        questionnaireBlock.forEach {
            if (it.idCloud.equals(questionaire.idCloud)) {
                questionaire.idQuestionaire = it.id
                return true;
            }
        }
        return false
    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onClickListener(questionaire: Questionaire, callback: onQuestionnaireAdapterListener) {
            view.setOnClickListener({
                //callback.navigationToDetailQuestionnarie(questionaire)
            })
        }

        fun onCheckListener(questionaire: Questionaire, callback: onQuestionnaireAdapterListener) {
            view.setOnClickListener {
                if (it.cb_select.isChecked) {
                    it.cb_select.isChecked = false
                    callback.removeQuestionnaire(questionaire)
                } else {
                    it.cb_select.isChecked = true

                    callback.addQuestionnaire(questionaire)
                }
            }
        }
    }
}

interface onQuestionnaireAdapterListener {
    //  fun navigationToDetailQuestionnarie(any: Any)
    fun addQuestionnaire(questionaire: Questionaire)

    fun removeQuestionnaire(questionaire: Questionaire)
}