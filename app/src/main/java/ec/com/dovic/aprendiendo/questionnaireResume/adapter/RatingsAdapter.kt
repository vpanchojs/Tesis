package ec.com.dovic.aprendiendo.questionnaireResume.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Raiting
import kotlinx.android.synthetic.main.item_rating.view.*


class RatingsAdapter(var data: ArrayList<Raiting>) : RecyclerView.Adapter<RatingsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating, parent, false)
        return RatingsAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = data.get(position)
        holder.view.tv_name_user.setText(rating.nameUser)
        holder.view.tv_comment.setText(rating.comment)
        holder.view.rating_value.rating = rating.value.toFloat()

    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    }


}
