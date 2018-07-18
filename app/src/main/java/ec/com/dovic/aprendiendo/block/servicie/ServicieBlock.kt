package ec.com.dovic.aprendiendo.block.servicie

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_HIGH
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.block.adapter.AnswerSelectAdapter
import ec.com.dovic.aprendiendo.database.AnswerBd
import ec.com.dovic.aprendiendo.database.Application
import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.util.GlideApp
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList


class ServicieBlock : Service(), View.OnClickListener {

    val TAG = "Servicio"
    var mWindowManager: WindowManager? = null
    var mblockView: View? = null
    lateinit var cl_body: ConstraintLayout
    lateinit var rv_answer: RecyclerView
    lateinit var btnUnlock: Button
    lateinit var tv_statament: TextView
    lateinit var iv_photo_question: ImageView
    lateinit var cl_windows_info: ConstraintLayout
    lateinit var ib_change_question: ImageButton
    lateinit var btn_new_question: Button


    val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            plataform(),
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)


    fun plataform(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
    }


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

    var questionsList = ArrayList<QuestionBd>()
    var answersList = ArrayList<AnswerBd>()
    var numQuestion: Int = 0

    lateinit var adapter: AnswerSelectAdapter


    lateinit var bloqueo1: Thread
    var timeActivity = 0
    lateinit var application: MyApplication
    lateinit var firebaseApi: FirebaseApi
    lateinit var dbApi: DbApi


    private var handlerTimeActivity: Handler? = null
    private var runnableTiemActivity: Runnable? = null

    companion object {
        val IDBLOCK = "idblock"
        var INSTANCE = false
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = true
        setupInjection()
        getDataBlock()
        setupViewBlock()

        handlerTimeActivity = Handler()

        runnableTiemActivity = Runnable {
            //Activar la busqueda de aplicaciones
            Log.e("runable", "se debe volver a bloquear")
        }

        handlerTimeActivity!!.postDelayed(runnableTiemActivity, 10000)

        /*
        val handlerThread = HandlerThread("Bloqueo")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)
        handlerThread.quit();
        */
    }

    fun stoptHandler() {
        if (handlerTimeActivity != null) {
            handlerTimeActivity?.removeCallbacks(runnableTiemActivity)
            handlerTimeActivity = null
        }
    }


    private fun setupInjection() {
        application = getApplication() as MyApplication
        firebaseApi = application.domainModule!!.providesFirebaseApi()
        dbApi = application.domainModule!!.providesDbApi()

    }


    /*Metodos para manejar la vista del bloqueo*/


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_unlock -> {
                if (validateAnswers()) {
                    removeView()
                } else {
                    visibilyWindowsInfo(View.VISIBLE)
                    //setDataQuestion(randomQuestions())
                }
            }
            R.id.ib_change_question -> {
                setDataQuestion(randomQuestions())
            }
            R.id.btn_new_question -> {
                setDataQuestion(randomQuestions())
                visibilyWindowsInfo(View.GONE)
            }
        }
    }

    private fun setupViewBlock() {
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mblockView = LayoutInflater.from(this).inflate(R.layout.block_widget, null)
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        mWindowManager!!.addView(mblockView, params)
        getElementsView()
        setupEvents()
        setupRecycler()
        //addView()
    }

    fun addView() {
        Log.e(TAG, "agregando vista")
        // mWindowManager!!.addView(mblockView, params)
        runOnUiThread {
            cl_body.visibility = View.VISIBLE
            setDataQuestion(randomQuestions())
        }
    }

    private fun removeView() {
        if (cl_body.visibility == View.VISIBLE) {
            runOnUiThread {
                cl_body.visibility = View.GONE
            }
        }
    }


    fun getElementsView() {
        cl_body = mblockView!!.findViewById(R.id.cl_body)
        btnUnlock = mblockView!!.findViewById(R.id.btn_unlock)
        rv_answer = mblockView!!.findViewById(R.id.rv_answer)
        tv_statament = mblockView!!.findViewById(R.id.tv_statament)
        iv_photo_question = mblockView!!.findViewById(R.id.iv_photo_question)
        cl_windows_info = mblockView!!.findViewById(R.id.cl_windows_info)
        ib_change_question = mblockView!!.findViewById(R.id.ib_change_question)
        btn_new_question = mblockView!!.findViewById(R.id.btn_new_question)

    }

    fun setupEvents() {
        btnUnlock.setOnClickListener(this)
        ib_change_question.setOnClickListener(this)
        btn_new_question.setOnClickListener(this)
    }


    /*
    fun getQuestion() {
        var idsQuestionnaire = ArrayList<Long>()
        questionnnairesBlock.forEach {
            idsQuestionnaire.add(it.id)
        }
        //presenter.getQuestion(idsQuestionnaire)

    }
    */

    fun randomQuestions(): QuestionBd {
        if (questionsList.size > 1) {
            val aux = numQuestion
            while (aux == numQuestion) {
                numQuestion = (Math.random() * questionsList.size).toInt()
            }
        } else if (questionsList.size > 0) {

        }
        return if (questionsList.size > 0) questionsList.get(numQuestion) else QuestionBd()
    }

    private fun setupRecycler() {
        adapter = AnswerSelectAdapter(answersList)
        rv_answer.layoutManager = LinearLayoutManager(this)

        val mDividerItemDecoration = DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        rv_answer.addItemDecoration(mDividerItemDecoration)
        rv_answer.adapter = adapter
    }


    fun setDataQuestion(question: QuestionBd) {
        tv_statament.setText(question.statement)
        if (question.photoUrl.isBlank()) {
            iv_photo_question.visibility = View.GONE
        } else {
            iv_photo_question.visibility = View.VISIBLE
            GlideApp.with(this)
                    .load(question.photoUrl)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .centerCrop()
                    .error(R.drawable.ic_person_black_24dp)
                    .into(iv_photo_question)
        }

        Log.e("Bloqueo", "la pregunta es ${question.statement}")
        answersList.clear()
        question.answers.forEach {
            it.select = false
        }
        answersList.addAll(question.answers)
        adapter.notifyDataSetChanged()
    }

    private fun visibilyWindowsInfo(visible: Int) {
        cl_windows_info.visibility = visible
    }

    private fun validateAnswers(): Boolean {
        var validated = true
        adapter.data.forEach {
            if (it.correct != it.select)
                validated = false
        }
        return validated
    }

    /*Fin metodos para controlar la vista bloqueo*/


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
            timeActivity = block.timeActivity * 600

            questionnnairesBlock.addAll(dbApi.db.questionnaireDao().getQuestionnaireByBlock(block.id))

            questionnnairesBlock.forEach {
                var questions = dbApi.db.questionDao().getQuestionOfQuestionnaire(it.id)
                questions.forEach {
                    it.answers.addAll(dbApi.db.answerDao().getAnswerOfQuestion(it.id))
                    Log.e("db", "respuestas ${it.answers.size}")
                }
                questionsList.addAll(questions)
            }
            Log.e(TAG, "cuestionarios  ${questionnnairesBlock.size}")
            uiThread {
                Log.e(TAG, "el bloqueo id ${block.id}")
            }
        }

    }


    fun startForeground() {

        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel()
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)

        val notification = notificationBuilder
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle(resources.getString(R.string.app_name))
                .setTicker(resources.getString(R.string.app_name))
                .setContentText("Bloqueo Activo")
                .setContentInfo("Abre una aplicacion y estudia")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_HIGH)
                .build()
        startForeground(9999, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "my_service"
        val channelName = "My Background Service"
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    private fun obtenerAplicacionEjecutandoseL(): String {
        val endCal = Calendar.getInstance()
        val beginCal = Calendar.getInstance()
        beginCal.add(Calendar.MINUTE, -5)
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
        INSTANCE = false
        if (mblockView != null) mWindowManager!!.removeView(mblockView);
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
                        removeView()
                        try {

                            aplicacionActual = obtenerAplicacionEjecutandoseL()

                            //Log.e("HILO 2", "Aplicacion: " + aplicacionActual);
                            /*VERIFICAMOS QUE LA APLICACION EJECUTADA ACTUALMENTE, DEBA SER BLOQUEADA*/

                            var verify = applicationsBlock.any {
                                it.packagename.equals(aplicacionActual)
                            }

                            if (verify) {
                                /*ALMACENAMOS EN UNA VARIBLE TEMP LA APLICACION BLOQUEADA*/
                                temp = aplicacionActual
                                addView()
                                Log.e("SERVICIE", "ACTIVIDAD LANZANDOSE")
                                activo_hilo2 = false
                                //   Log.d("HILO 2", "CANCELADOR");
                            }
                            /*Tiempo de Espera para descansar el hilo*/
                            Thread.sleep(500)
                        } catch (e: Exception) {
                            Log.e(TAG, e.toString())
                            e.printStackTrace()

                        }

                    }
                    /*Tiempo de Espera para descansar el hilo*/
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

        }

    }

}
