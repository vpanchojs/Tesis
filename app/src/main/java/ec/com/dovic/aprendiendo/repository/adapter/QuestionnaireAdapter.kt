package ec.com.dovic.aprendiendo.repository.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Questionaire
import kotlinx.android.synthetic.main.item_questionnarie_repository.view.*

/**
 * Created by victor on 24/2/18.
 */
class QuestionnaireRepositoryAdapter(var data: ArrayList<Questionaire>, var callback: onQuestionnaireAdapterListener) : RecyclerView.Adapter<QuestionnaireRepositoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_questionnarie_repository, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val questionaire = data.get(position)
        holder.view.tv_title.text = questionaire.title
        holder.view.tv_description.text = questionaire.description
        holder.view.tv_raiting.text = questionaire.assessment.toString()
        holder.view.tv_num_question.text = String.format(holder.view.context.resources.getString(R.string.number_questions), questionaire.numberQuest)
        holder.view.tv_subject.text = questionaire.subject
        holder.onClickListener(questionaire, callback)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onClickListener(questionaire: Questionaire, callback: onQuestionnaireAdapterListener) {
            view.setOnClickListener {
                callback.navigationToDetailQuestionnarie(questionaire)
            }
        }
    }
}

interface onQuestionnaireAdapterListener {
    fun navigationToDetailQuestionnarie(any: Any)
}