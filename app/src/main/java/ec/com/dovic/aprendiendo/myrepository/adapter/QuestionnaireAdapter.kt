package ec.com.dovic.aprendiendo.myrepository.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Questionaire
import kotlinx.android.synthetic.main.item_my_questionnarie.view.*

/**
 * Created by victor on 24/2/18.
 */
class QuestionnaireAdapter(var data: ArrayList<Questionaire>, var callback: onQuestionnaireAdapterListener) : RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_questionnarie, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var questionaire = data.get(position)
        holder.view.tv_title.text = questionaire.title
        holder.view.tv_description.text = questionaire.description
        holder.view.tv_num_question.text = "${questionaire.numberQuest} preg"
        holder.onClickListener(questionaire, callback)
    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onClickListener(questionaire: Questionaire, callback: onQuestionnaireAdapterListener) {
            view.setOnClickListener({
                callback.navigationToDetailQuestionnarie(questionaire)
            })
        }
    }
}

interface onQuestionnaireAdapterListener {
    fun navigationToDetailQuestionnarie(any: Any)
}