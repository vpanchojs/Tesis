package ec.com.dovic.aprendiendo.domain.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by victor on 15/3/18.
 */
class QuestionnaireServices : Service() {


    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}