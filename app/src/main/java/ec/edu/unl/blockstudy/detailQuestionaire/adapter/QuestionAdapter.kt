package ec.edu.unl.blockstudy.detailQuestionaire.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question
import kotlinx.android.synthetic.main.item_my_question.view.*


class QuestionAdapter(var data: ArrayList<Question>, var callback: onQuestionAdapterListener) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_my_question, parent, false)
        return QuestionAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var question = data.get(position)
        holder!!.view.tv_description.setText(question.statement)
        holder!!.view.tv_num_question.setText("# " + (position + 1))
        holder!!.onClickListener(question, position, callback)
    }

    /*
    private fun getAnswer(answer: ToMany<Answer>): String {
        for (a in answer) {
            if (a.correct!!) {
                return a.statement!!
            }
        }
        return ""

    }
    */

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onClickListener(question: Question, position: Int, callback: onQuestionAdapterListener) {
            view.setOnClickListener({
                callback.navigationToDetailQuestion(question, position)
            })
        }
    }


}

interface onQuestionAdapterListener {
    fun navigationToDetailQuestion(any: Any, position: Int)
}