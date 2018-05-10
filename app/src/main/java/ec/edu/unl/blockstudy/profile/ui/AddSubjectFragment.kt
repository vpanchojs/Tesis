package ec.edu.unl.blockstudy.profile.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Subject
import ec.edu.unl.blockstudy.profile.adapters.SubjectAdapter
import ec.edu.unl.blockstudy.profile.adapters.onSubjectAdapterListener
import kotlinx.android.synthetic.main.fragment_add_subject.view.*

class AddSubjectFragment : DialogFragment(), DialogInterface.OnShowListener, onSubjectAdapterListener {
    private val TAG = "AddSujectFragment"
    private var btn_add: Button? = null
    private var ib_close: ImageButton? = null
    private var tie_subject: TextInputEditText? = null
    private var callback: OnAddSubjectListener? = null
    private var data: ArrayList<Subject>? = ArrayList()
    private var subjectAdapter: SubjectAdapter? = null
    private var subjects: ArrayList<Subject>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            subjects = arguments!!.getParcelableArrayList(PARAM_PREFERENCES)
        }

        data!!.add(Subject("1", "Base de Datos", false))
        data!!.add(Subject("2", "Programación Móvil", false))
        data!!.add(Subject("3", "Programación Web", false))
        data!!.add(Subject("4", "Programación Básica", false))
        data!!.add(Subject("5", "Sistemas Expertos", false))
        data!!.add(Subject("6", "Inteligencia Artificial", false))
        data!!.add(Subject("7", "Arquitectura de Computadores", false))
        data!!.add(Subject("8", "Redes", false))
        data!!.add(Subject("9", "Matematicas Discretas", false))
        data!!.add(Subject("10", "Fundamentos Informaticos", false))

        data!!.forEach {
            subjects!!.forEach { s ->
                if (it.id.equals(s.id)) {
                    it.active = true
                }
            }
        }

        subjectAdapter = SubjectAdapter(data!!, this)

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_add_subject, null)
        btn_add = view.btn_add
        ib_close = view.ib_close
        view.rv_subjects.layoutManager = LinearLayoutManager(context)
        view.rv_subjects.adapter = subjectAdapter
        builder.setView(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)
        return dialog
    }


    override fun onShow(dialog: DialogInterface?) {
        val dialogo = getDialog() as AlertDialog
        if (dialogo != null) {
            btn_add!!.setOnClickListener {
                callback!!.onAddSubjects(subjects!!)
                dismiss()
            }

            ib_close!!.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnAddSubjectListener) {
            callback = context
        } else {
            throw  RuntimeException(context.toString()
                    + " must implement OnRecoveryPasswordListener");
        }
    }

    override fun onDetach() {
        super.onDetach();
        callback = null;
    }

    companion object {
        val PARAM_PREFERENCES = "preferences"
        fun newInstance(preferences: java.util.ArrayList<Subject>?): AddSubjectFragment {
            val fragment = AddSubjectFragment()
            var args = Bundle()
            args.putParcelableArrayList(PARAM_PREFERENCES, preferences)
            fragment.arguments = args
            return fragment
        }
    }

    interface OnAddSubjectListener {
        fun onAddSubjects(subjects: ArrayList<Subject>)
    }

    override fun onAddSubject(subject: Subject) {
        Log.e(TAG, "add " + subject.name)
        subjects!!.add(subject)


    }

    override fun onRemoveSubject(subject: Subject) {

        subjects!!.forEach {
            Log.e(TAG, "compare " + it.id + "=" + subject.id)
            if (it.id.equals(subject.id)) {
                Log.e(TAG, "remove " + subject.name)
                subjects!!.remove(it)
                return
            }


        }

    }
}
