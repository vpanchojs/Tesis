package ec.edu.unl.blockstudy.block.servicie

import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.block.ui.BlockActivity
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.entities.Application
import ec.edu.unl.blockstudy.entities.QuestionPath
import ec.edu.unl.blockstudy.entities.QuestionnaireBlock
import java.util.*
import kotlin.collections.ArrayList

class ServicieBlock : Service() {

    val TAG = "Servicio"

    internal var activo_hilo1: Boolean = false
    var activo_hilo2: Boolean = false
    var temp = ""
    var aplicacionActual = ""
    lateinit var block: ec.edu.unl.blockstudy.entities.Block

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    val applicationsBlock = ArrayList<Application>()
    val questionnnairesBlock = ArrayList<QuestionnaireBlock>()
    val questionPathList = ArrayList<QuestionPath>()
    lateinit var bloqueo1: Thread
    var timeActivity = 0
    lateinit var application: MyApplication
    lateinit var firebaseApi: FirebaseApi
    lateinit var objectBoxApi: ObjectBoxApi


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
        objectBoxApi = application.domainModule!!.providesObjectBoxApi()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //val idBlock = intent!!.getLongExtra(IDBLOCK, -1)
        //Log.e(TAG, "id block $idBlock")
        getDataBlock(1)
        startForeground()
        bloqueo1 = Block()
        bloqueo1.start()

        return START_STICKY
    }


    fun getDataBlock(idBlock: Long) {
        block = objectBoxApi.blockBox.get(idBlock)
        applicationsBlock.addAll(block.apps)
        questionnnairesBlock.addAll(block.questionaire)
        timeActivity = block.timeActivity * 60
        getQuestionsPath()
        Log.e(TAG, "paths de preguntas ${questionPathList.size}")
    }

    fun getQuestionsPath() {
        questionPathList.clear()
        questionnnairesBlock.forEach {
            questionPathList.addAll(it.questionsPath)
        }
    }


    fun startForeground() {
        val notification = NotificationCompat.Builder(this)
                .setContentTitle(resources.getString(R.string.app_name))
                .setTicker(resources.getString(R.string.app_name))
                .setContentText("BLOQUEO ACTIVO")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(null)
                .setOngoing(true)
                .build()
        startForeground(9999, notification)
    }


    fun obtenerAplicacionesEjecutandoseK(): String {
        var aplicacion = ""
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            aplicacion = am.runningAppProcesses[0].processName
            Log.d(TAG, aplicacion)
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            aplicacion = componentInfo.packageName
        }
        return aplicacion
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun obtenerAplicacionEjecutandoseL(): String {
        val endCal = Calendar.getInstance()
        val beginCal = Calendar.getInstance()
        beginCal.add(Calendar.MINUTE, -1)
        val manager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                beginCal.timeInMillis, endCal.timeInMillis)

        stats.sortByDescending { it.lastTimeUsed }

        //Log.e(TAG, "app activa ${stats[0].packageName}");
        return stats[0].packageName

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
                    /*CONDICION QUE SE ENCARGA DE ENCONTRA LA APLICACION QUE SE ESTA EJECUTANDO ACTUALMENTE*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        aplicacionActual = obtenerAplicacionEjecutandoseL()
                    } else {
                        aplicacionActual = obtenerAplicacionesEjecutandoseK()
                    }
                    //Log.d("HILO 1", "Aplicacion: " + aplicacionActual);
                    /*VERIFICAMOS QUE LA APLICACION ACTUAL NO SE IGUAL A LA APLICACION DE BLOQUEO*/
                    if (aplicacionActual != getPackageName()) {
                        /*VERIFICACION LA EXISTENCIA DE FRECUENCIA DE BLOQUEO Y QUE LA APLICACION SEA IGUAL PARA LANZAR NUEVAMENTE EL BLOQUEO*/
                        if (aplicacionActual == temp && timeActivity > 0) {
                            cont++
                            Log.e("HILO 1", "Aplicacion contador: " + cont + " fre:" + timeActivity);
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                //Log.d(TAG,"5.0 mayor");
                                aplicacionActual = obtenerAplicacionEjecutandoseL()
                            } else {
                                aplicacionActual = obtenerAplicacionesEjecutandoseK()
                                //Log.d(TAG,"menor a 5.0");
                                //Log.d(TAG,aplicacionActual);
                            }

                            // Log.d("HILO 2", "Aplicacion: " + aplicacionActual);
                            /*VERIFICAMOS QUE LA APLICACION EJECUTADA ACTUALMENTE, DEBA SER BLOQUEADA*/

                            var verify = applicationsBlock.any {
                                it.app.equals(aplicacionActual)
                            }

                            if (verify) {
                                /*ALMACENAMOS EN UNA VARIBLE TEMP LA APLICACION BLOQUEADA*/
                                temp = aplicacionActual
                                /*LANZAMOS UNA LLAMDA AL BROADCASTRECIVIR QUE SE ENCARA DE INCIAR LA ACTIVIDAD DEL BLOQUEO*/
                                sendBroadcast(Intent("MI_ESPECIFICA").putExtra(BlockActivity.QUESTIONS_PATH_PARAM, questionPathList!!))
                                /*TERMINAMOS EL HILO 2*/
                                activo_hilo2 = false
                                //   Log.d("HILO 2", "CANCELADOR");
                            }
                            /*Tiempo de Espera para descansar el hilo*/
                            Thread.sleep(300)
                        } catch (e: InterruptedException) {
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
