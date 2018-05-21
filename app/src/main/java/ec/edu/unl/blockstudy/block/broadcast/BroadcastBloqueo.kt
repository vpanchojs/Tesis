package ec.edu.unl.blockstudy.block.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ec.edu.unl.blockstudy.block.ui.BlockActivity

class BroadcastBloqueo : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("AA","ACTIVIDAD LANZANDOSE")
        val mIntent = Intent(p0, BlockActivity::class.java)
        //mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //mIntent.putExtra(BlockActivity.QUESTIONNAIRE_PATH_PARAM, p1!!.getParcelableArrayListExtra<QuestionPath>(BlockActivity.QUESTIONNAIRE_PATH_PARAM))
        p0!!.startActivity(mIntent)
    }
}