package ec.edu.unl.blockstudy.block.adapter;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Answer
import kotlinx.android.synthetic.main.item_answer_select.view.*


class AnswerSelectAdapter(var data: ArrayList<Answer>) : RecyclerView.Adapter<AnswerSelectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_answer_select, parent, false);
        return ViewHolder(view);
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var answer = data.get(position)
        holder!!.view.cb_correct.setText(answer.statement!!)
        holder!!.onActionListener(answer)
        holder!!.view.cb_correct.isChecked = answer.select!!
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onActionListener(answer: Answer) {
            view.cb_correct.setOnCheckedChangeListener { buttonView, isChecked ->
                answer.select = isChecked
            }
        }
    }
}
