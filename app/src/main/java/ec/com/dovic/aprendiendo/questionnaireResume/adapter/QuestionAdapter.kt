package ec.com.dovic.aprendiendo.questionnaireResume.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Question
import kotlinx.android.synthetic.main.item_my_question.view.*


class QuestionAdapter(var data: ArrayList<Question>) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_question, parent, false)
        return QuestionAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var question = data.get(position)
        holder.view.tv_description.setText(question.statement)
        holder.view.tv_num_question.setText("# " + (position + 1))
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    }


}
