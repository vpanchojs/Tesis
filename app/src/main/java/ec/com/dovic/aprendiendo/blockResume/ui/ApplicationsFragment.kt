package ec.com.dovic.aprendiendo.blockResume.ui

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.blockResume.adapter.ApplicationAdapter
import kotlinx.android.synthetic.main.activity_applications.view.*

class ApplicationsFragment : DialogFragment(), DialogInterface.OnShowListener, ApplicationAdapter.onAplictionAdapterListener {

    lateinit var applications: ArrayList<String>
    private var btn_action: Button? = null
    private var ib_close: ImageButton? = null

    lateinit var appList: ArrayList<ApplicationInfo>
    lateinit var adapter: ApplicationAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applications = arguments!!.getStringArrayList(APPLICATIONS)
        appList = ArrayList<ApplicationInfo>()
        getApplications()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.activity_applications, null)
        builder.setView(view)
        btn_action = view.btn_action
        ib_close = view.ib_back
        setupRecyclerView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)

        return dialog
    }


    private fun setupRecyclerView(view: View) {
        Log.e("aplicaciones: ", appList.size.toString())
        adapter = ApplicationAdapter(appList, applications, this)
        view.rv_applications.layoutManager = LinearLayoutManager(context)
        view.rv_applications.adapter = adapter
    }

    private fun getApplications() {
        var intentAplicacion = Intent(Intent.ACTION_MAIN, null)
        var pm = context!!.packageManager
        intentAplicacion.addCategory(Intent.CATEGORY_LAUNCHER)
        val lista = pm.queryIntentActivities(intentAplicacion, PackageManager.PERMISSION_GRANTED)
        for (app in lista) {
            appList.add(app.activityInfo.applicationInfo)
        }
    }

    override fun onShow(dialog: DialogInterface?) {
        //val dialogo = getDialog() as AlertDialog
        btn_action!!.setOnClickListener {
            Log.e("select ", applications.size.toString())
            (parentFragment as BlockResumeFragment).setApplications()
            dismiss()
        }
        ib_close!!.setOnClickListener {
            dismiss()
        }

    }

    override fun addApp(app: ApplicationInfo) {
        Log.e("select ", app.toString())
        applications.add(app.packageName)
    }

    override fun removeApp(app: ApplicationInfo) {
        applications.remove(app.packageName)
    }

    companion object {
        const val APPLICATIONS = "applications"
        fun newInstance(applications: ArrayList<String>): ApplicationsFragment {
            val fragment = ApplicationsFragment()
            var args = Bundle()
            args.putStringArrayList(APPLICATIONS, applications)
            fragment.arguments = args
            return fragment
        }
    }

}
