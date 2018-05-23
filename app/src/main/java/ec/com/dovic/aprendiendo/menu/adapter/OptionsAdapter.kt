package ec.com.dovic.aprendiendo.menu.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.util.OptionMenu
import kotlinx.android.synthetic.main.item_menu_option.view.*

class OptionsAdapter(var data: ArrayList<OptionMenu>, var callback: onOptionsAdapterListener) : RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_option, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tv_name_option.text = data.get(position).name
        holder.view.iv_option.setImageResource(data.get(position).icon)
        holder.onClickItemListener(position, callback)
    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onClickItemListener(position: Int, callback: onOptionsAdapterListener) {
            view.setOnClickListener({
                callback.onClick(position)
            })
        }
    }
}

interface onOptionsAdapterListener {
    fun onClick(position: Int)
}
