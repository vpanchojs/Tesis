package ec.edu.unl.blockstudy.newQuestionnaire.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Keyword
import kotlinx.android.synthetic.main.item_subject_single.view.*

/**
 * Created by victor on 30/1/18.
 */
class KeywordAdapter(var data: ArrayList<Keyword>, var callback: onKeywordAdapterListener) : RecyclerView.Adapter<KeywordAdapter.ViewHolder>() {
    companion object {
        var lastCheckedRB: RadioButton? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_subject_single, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var keyword = data.get(position)
        holder!!.view.tv_keyword.text = keyword.description
        holder!!.onClickItemListener(position, callback)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onClickItemListener(position: Int, callback: onKeywordAdapterListener) {
            view.ib_remove.setOnClickListener({
                callback.onRemoveKeyword(position)
            })
        }

    }
}

interface onKeywordAdapterListener {
    fun onRemoveKeyword(position: Int)
}
