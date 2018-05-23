package ec.com.dovic.aprendiendo.blockResume.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import kotlinx.android.synthetic.main.item_my_questionnarie_select.view.*

/**
 * Created by victor on 24/2/18.
 */
class QuestionnaireSelectAdapter(var data: ArrayList<QuestionnaireBd>, var callback: onQuestionnaireAdapterListener<QuestionnaireBd>) : RecyclerView.Adapter<QuestionnaireSelectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_questionnarie_select, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var questionaire = data.get(position)
        holder.view.tv_title.text = questionaire.title

        holder.view.cb_select.isChecked = questionaire.blockId > 0

        holder.onCheckListener(questionaire, callback)


    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        fun onCheckListener(questionaire: QuestionnaireBd, callback: onQuestionnaireAdapterListener<QuestionnaireBd>) {
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

interface onQuestionnaireAdapterListener<T> {
    //  fun navigationToDetailQuestionnarie(any: Any)
    fun addQuestionnaire(questionaire: T)

    fun removeQuestionnaire(questionaire: T)
}