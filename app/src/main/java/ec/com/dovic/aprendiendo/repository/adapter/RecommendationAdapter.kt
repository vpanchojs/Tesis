package ec.com.dovic.aprendiendo.repository.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.repository.ui.QuestionnaireRepositoryFragment
import kotlinx.android.synthetic.main.item_questionnarie_repository.view.*

/**
 * Created by victor on 24/2/18.
 */
class RecommendationAdapter(var data: ArrayList<Questionaire>, var callback: onRecommendationAdapterListener) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_questionnarie_recomendation, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val questionaire = data.get(position)
        holder.view.tv_title.text = questionaire.title
        holder.onClickListener(questionaire, callback)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onClickListener(questionaire: Questionaire, callback: onRecommendationAdapterListener) {
            view.setOnClickListener {
                callback.navigationToDetailQuestionnarie(questionaire)
            }
        }
    }
}

interface onRecommendationAdapterListener {
    fun navigationToDetailQuestionnarie(any: Any)
}