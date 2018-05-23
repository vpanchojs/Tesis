package ec.com.dovic.aprendiendo.block.servicie

import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.block.ui.BlockActivity
import ec.com.dovic.aprendiendo.database.Application
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.services.DbApi
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

class ServicieBlock : Service() {

    val TAG = "Servicio"

    internal var activo_hilo1: Boolean = false
    var activo_hilo2: Boolean = false
    var temp = ""
    var aplicacionActual = ""
    lateinit var block: ec.com.dovic.aprendiendo.database.Block

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    val applicationsBlock = ArrayList<Application>()
    val questionnnairesBlock = ArrayList<QuestionnaireBd>()

    lateinit var bloqueo1: Thread
    var timeActivity = 0
    lateinit var application: MyApplication
    lateinit var firebaseApi: FirebaseApi
    lateinit var dbApi: DbApi


    companion object {
        val IDBLOCK = "idblock"
        var INSTANCE = false
    }


    override fun onCreate() {
        super.onCreate()
        setupInjection()
        INSTANCE = true
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        firebaseApi = application.domainModule!!.providesFirebaseApi()
        dbApi = application.domainModule!!.providesDbApi()

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getDataBlock()
        startForeground()
        bloqueo1 = Block()
        bloqueo1.start()

        return START_STICKY
    }

    fun getDataBlock() {
        doAsync {
            block = dbApi.db.blockDao().getBlockById(firebaseApi.getUid())
            Log.e(TAG, "el bloqueo id ${block.id}")
            applicationsBlock.addAll(dbApi.db.applicationDao().getApplicationByBlock(block.id))
            Log.e(TAG, "aplicaciones  ${applicationsBlock.size}")
            timeActivity = block.timeActivity * 60

            questionnnairesBlock.addAll(dbApi.db.questionnaireDao().getQuestionnaireByBlock(block.id))
            /*
             questionnnairesBlock.forEach {

             }*/
            Log.e(TAG, "cuestionarios  ${questionnnairesBlock.size}")
            uiThread {
                Log.e(TAG, "el bloqueo id ${block.id}")
            }
        }

    }


    fun startForeground() {
        val notification = NotificationCompat.Builder(this)
                .setContentTitle(resources.getString(R.string.app_name))
                .setTicker(resources.getString(R.string.app_name))
                .setContentText("Bloqueo Activo")
                .setContentInfo("Abre una aplicacion y estudia")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(null)
                .setOngoing(true)
                .build()
        startForeground(9999, notification)
    }

    private fun obtenerAplicacionEjecutandoseL(): String {
        val endCal = Calendar.getInstance()
        val beginCal = Calendar.getInstance()
        beginCal.add(Calendar.MINUTE, -1)
        val manager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                beginCal.timeInMillis, endCal.timeInMillis)

        stats.sortByDescending { it.lastTimeUsed }
        if (stats.size > 0)
            return stats[0].packageName
        else
            return ""

    }

    override fun onDestroy() {
        super.onDestroy()
        activo_hilo2 = false
        activo_hilo1 = false
    }


    inner class Block : Thread() {

        override fun run() {
            activo_hilo1 = true
            activo_hilo2 = true
            var cont = 0
            while (activo_hilo1) {
                try {

                    aplicacionActual = obtenerAplicacionEjecutandoseL()

                    //Log.d("HILO 1", "Aplicacion: " + aplicacionActual);
                    /*VERIFICAMOS QUE LA APLICACION ACTUAL NO SE IGUAL A LA APLICACION DE BLOQUEO*/
                    if (aplicacionActual != getPackageName()) {
                        /*VERIFICACION LA EXISTENCIA DE FRECUENCIA DE BLOQUEO Y QUE LA APLICACION SEA IGUAL PARA LANZAR NUEVAMENTE EL BLOQUEO*/
                        if (aplicacionActual == temp && timeActivity > 0) {
                            cont++
                            // Log.e("HILO 1", "Aplicacion contador: " + cont + " fre:" + timeActivity);
                            /*TIEMPO DE ESPERA PARA INCIAR NUEVAMENTE EL BLOQUEO MIENTRAS NOS ENCONTRAMOS EN LA APLICACION ANTERIOR BLOQUEADA*/
                            if (cont > timeActivity) {
                                cont = 0
                                activo_hilo2 = true

                            }
                        } else {
                            //if (!aplicacionActual.equals(temp) && frecuencia == 0) {
                            if (aplicacionActual != temp) {
                                cont = 0
                                activo_hilo2 = true
                            }
                        }
                    }
                    /*INICIO  HILO 2*/
                    //Este hilo busca la aplicacion a ejecutarse
                    while (activo_hilo2) {
                        Log.e(TAG, "bucando aplicacion")
                        try {

                            aplicacionActual = obtenerAplicacionEjecutandoseL()

                            // Log.d("HILO 2", "Aplicacion: " + aplicacionActual);
                            /*VERIFICAMOS QUE LA APLICACION EJECUTADA ACTUALMENTE, DEBA SER BLOQUEADA*/

                            var verify = applicationsBlock.any {
                                it.packagename.equals(aplicacionActual)
                            }

                            if (verify) {
                                /*ALMACENAMOS EN UNA VARIBLE TEMP LA APLICACION BLOQUEADA*/
                                temp = aplicacionActual
                                /*LANZAMOS UNA LLAMDA AL BROADCASTRECIVIR QUE SE ENCARA DE INCIAR LA ACTIVIDAD DEL BLOQUEO*/
                                //sendBroadcast(Intent("broadcast").putExtra(BlockActivity.QUESTIONNAIRE_PATH_PARAM, questionPathList!!))
                                Thread.sleep(1500)
                                val mIntent = Intent(applicationContext, BlockActivity::class.java)
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                mIntent.putExtra(BlockActivity.QUESTIONNAIRE_PATH_PARAM, questionnnairesBlock)
                                applicationContext!!.startActivity(mIntent)
                                /*TERMINAMOS EL HILO 2*/
                                Log.e("SERVICIE", "ACTIVIDAD LANZANDOSE")
                                activo_hilo2 = false
                                //   Log.d("HILO 2", "CANCELADOR");
                            }
                            /*Tiempo de Espera para descansar el hilo*/
                            Thread.sleep(300)
                        } catch (e: Exception) {
                            Log.e(TAG, e.toString())
                            e.printStackTrace()

                        }

                    }
                    /*Tiempo de Espera para descansar el hilo*/
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

        }

    }

}
