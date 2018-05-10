package ec.edu.unl.blockstudy.newQuestion.adapters;

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Answer
import kotlinx.android.synthetic.main.item_answer.view.*


class AnswerAdapter(var data: ArrayList<Answer>, var callback: onAnswerAdapterListener) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_answer, parent, false);
        return ViewHolder(view);
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun removeItem(any: Any) {
        var answer = any as Answer
        for (a in 0..(itemCount - 1)) {
            if (answer.equals(data.get(a))) {
                data.remove(answer)
                notifyItemRemoved(a)
                break
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var answer = data.get(position)
        holder!!.view.cb_correct.isChecked = answer.correct!!
        holder!!.view.tie_answer.setText(answer.statement!!)
        holder!!.onActionListener(answer, position, callback)
    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        fun onActionListener(answer: Answer, position: Int, callback: onAnswerAdapterListener) {
            view.ib_delete.setOnClickListener {
                callback.onRemoveAnswer(answer)
            }

            view.cb_correct.setOnCheckedChangeListener { buttonView, isChecked ->
                answer.correct = isChecked
            }

            view.tie_answer.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    answer.statement = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

}

interface onAnswerAdapterListener {
    fun onRemoveAnswer(position: Answer)
}
