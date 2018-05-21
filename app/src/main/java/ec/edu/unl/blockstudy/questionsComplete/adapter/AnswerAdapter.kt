package ec.edu.unl.blockstudy.questionsComplete.adapter;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.database.AnswerBd
import kotlinx.android.synthetic.main.item_answer_complete.view.*


class AnswerAdapter(var data: ArrayList<AnswerBd>) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_answer_complete, parent, false);
        return ViewHolder(view);
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var answer = data.get(position)
        holder!!.view.tv_respuesta.text = answer.statement

        if (answer.correct!!) {
            holder!!.view.iv_correct.visibility = View.VISIBLE
        } else {
            holder!!.view.iv_correct.visibility = View.INVISIBLE
        }


    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    }

}

