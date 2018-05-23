package ec.edu.unl.blockstudy.blockResume.adapter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.util.GlideApp
import kotlinx.android.synthetic.main.item_aplicaciones.view.*

/**
 * Created by victor on 28/3/18.
 */
class ApplicationAdapter(var data: ArrayList<ApplicationInfo>, var appSelect: ArrayList<String>, var callback: onAplictionAdapterListener) : RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_aplicaciones, parent, false)
        return ApplicationAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = data.get(position)
        GlideApp.with(context)
                .load(app.loadIcon(context.packageManager))
                .centerCrop()
                .into(holder.view.im_AppIcono)

        holder.view.cb_app.isChecked = appSelect.contains(app.packageName)

        holder.view.tv_AppNombre.setText(app.loadLabel(context.packageManager))
        holder.onCheckListener(app, callback)


    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        fun onCheckListener(app: ApplicationInfo, callback: onAplictionAdapterListener) {
            view.setOnClickListener {
                if (it.cb_app.isChecked) {
                    it.cb_app.isChecked = false
                    callback.removeApp(app)
                } else {
                    it.cb_app.isChecked = true

                    callback.addApp(app)
                }
            }
        }

    }

    interface onAplictionAdapterListener {
        fun addApp(app: ApplicationInfo)
        fun removeApp(app: ApplicationInfo)
    }

}

