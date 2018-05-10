package ec.edu.unl.blockstudy.block.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ec.edu.unl.blockstudy.block.ui.BlockActivity
import ec.edu.unl.blockstudy.entities.QuestionPath

class BroadcastBloqueo : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val mIntent = Intent(p0, BlockActivity::class.java)
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mIntent.putExtra(BlockActivity.QUESTIONS_PATH_PARAM, p1!!.getParcelableArrayListExtra<QuestionPath>(BlockActivity.QUESTIONS_PATH_PARAM))
        p0!!.startActivity(mIntent)
    }
}