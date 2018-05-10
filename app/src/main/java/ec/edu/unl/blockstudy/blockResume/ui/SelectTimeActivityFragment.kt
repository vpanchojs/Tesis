package ec.edu.unl.blockstudy.blockResume.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.NumberPicker
import ec.edu.unl.blockstudy.R
import kotlinx.android.synthetic.main.fragmente_select_time_activity.view.*

class SelectTimeActivityFragment : DialogFragment(), DialogInterface.OnShowListener {
    private var btn_action: Button? = null
    private var ib_close: ImageButton? = null
    private var cb_none: CheckBox? = null
    private var numberPicker: NumberPicker? = null
    private var time: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        time = arguments!!.getInt(TIME, -1)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var view = activity!!.layoutInflater.inflate(R.layout.fragmente_select_time_activity, null)
        builder.setView(view)
        ib_close = view.ib_back
        btn_action = view.btn_action
        cb_none = view.cb_none
        numberPicker = view.np

        numberPicker!!.minValue = 1
        numberPicker!!.maxValue = 20

        if (time > 0) {
            numberPicker!!.value = time
        } else {
            cb_none!!.isChecked = true
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setOnShowListener(this)

        return dialog
    }


    override fun onShow(dialog: DialogInterface?) {
        val dialogo = getDialog() as AlertDialog
        if (dialogo != null) {
            btn_action!!.setOnClickListener {
                if (cb_none!!.isChecked) {
                    (parentFragment as BlockResumeFragment).onSetTime(-1)
                } else {
                    (parentFragment as BlockResumeFragment).onSetTime(numberPicker!!.value)
                }
                dismiss()
            }
            ib_close!!.setOnClickListener {
                dismiss()
            }
        }
    }


    companion object {
        const val TIME = "time"
        fun newInstance(time: Int): SelectTimeActivityFragment {
            val fragment = SelectTimeActivityFragment()
            var args = Bundle()
            args.putInt(TIME, time)
            fragment.arguments = args
            return fragment
        }
    }

}
