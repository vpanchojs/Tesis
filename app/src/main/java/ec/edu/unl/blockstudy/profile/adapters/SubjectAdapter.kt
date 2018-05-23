package ec.edu.unl.blockstudy.profile.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Subject
import kotlinx.android.synthetic.main.item_subject.view.*

/**
 * Created by victor on 30/1/18.
 */
class SubjectAdapter(var data: ArrayList<Subject>, var callback: onSubjectAdapterListener) : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tv_name.text = data.get(position).name
        holder.view.cb_subject.isChecked = data.get(position).active
        holder.onClickItemListener(data.get(position), callback)
    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun onClickItemListener(subject: Subject, callback: onSubjectAdapterListener) {
            view.cb_subject.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("CHECK", subject.name)
                subject.active = isChecked
                if (isChecked)
                    callback.onAddSubject(subject)
                else
                    callback.onRemoveSubject(subject)

            }

        }

    }
}

interface onSubjectAdapterListener {
    fun onAddSubject(subject: Subject)
    fun onRemoveSubject(subject: Subject)
}
