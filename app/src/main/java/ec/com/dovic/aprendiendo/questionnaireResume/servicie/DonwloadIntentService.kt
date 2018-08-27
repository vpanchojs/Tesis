package ec.com.dovic.aprendiendo.questionnaireResume.servicie

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.questionnaireResume.ui.QuestionnaireResumeActivity
import ec.com.dovic.aprendiendo.util.BaseActivitys


/**
 * Created by victor on 28/3/18.
 */
class DonwloadIntentService : IntentService("DonwloadIntentService") {

    lateinit var application: MyApplication
    lateinit var firebaseApi: FirebaseApi
    lateinit var dbApi: DbApi
    lateinit var title: String
    var isDownLoad = false

    companion object {
        val IDQUESTIONNAIRE = "id"
        val TAG = "DowloadServicie"
        val ISDOWNLOAD = "isdownload"
    }

    override fun onCreate() {
        super.onCreate()
        setupInjection()
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        firebaseApi = application.domainModule!!.providesFirebaseApi()
        dbApi = application.domainModule!!.providesDbApi()

    }

    override fun onHandleIntent(p0: Intent?) {
        var correct = true
        var cicle = true
        var cont = 0
        var questionsSize = -1
        val idQuestionnaire = p0!!.getStringExtra(IDQUESTIONNAIRE)
        val isDownLoad = p0!!.getBooleanExtra(ISDOWNLOAD, false)
        Log.e("servicie", idQuestionnaire)

        /*Solicitamos el cuestionario al servidor*/
        firebaseApi.getQuestionnaireComplete(idQuestionnaire, isDownLoad, object : OnCallbackApis<QuestionnaireBd> {
            override fun onSuccess(questionnaire: QuestionnaireBd) {
                if (!questionnaire.idUser!!.equals(firebaseApi.getUid())) {
                    //application.domainModule!!.providesRetofitApi().generateRecommendations(firebaseApi.getUid(), questionnaire.idCloud)
                }
                title = questionnaire.title!!
                questionnaire.idUserLocal = firebaseApi.getUid()
                dbApi.insertQuestionnaire(questionnaire, object : OnCallbackApis<Long> {
                    override fun onSuccess(idQuestionnaire: Long) {
                        questionnaire.questions!!.forEach { question ->

                            question.questionnaireId = idQuestionnaire

                            dbApi.insertQuestion(question, object : OnCallbackApis<Long> {
                                override fun onSuccess(idQuestion: Long) {

                                    question.answers.forEach { answer ->
                                        answer.questionId = idQuestion
                                        dbApi.insertAnswer(answer, object : OnCallbackApis<Long> {
                                            override fun onSuccess(response: Long) {

                                            }

                                            override fun onError(error: Any?) {

                                            }
                                        })

                                    }
                                }

                                override fun onError(error: Any?) {

                                }
                            })
                        }
                    }

                    override fun onError(error: Any?) {

                    }
                })

                cicle = false
                correct = true
            }

            override fun onError(error: Any?) {
                cicle = false
                correct = false
                BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
            }
        })



        while (cicle) {
            Log.e(TAG, "descargando cuestionario")
            //cicle = !(cont == questionsSize)
            Thread.sleep(600)
        }

        if (correct) {
            setNotification(title)
            sendStatusBroadcast(true)
        }
    }


    fun setNotification(titleQuestionnnaire: String) {

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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Cuestionario descargado")
                .setContentText(titleQuestionnnaire)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

        val notificationManager = NotificationManagerCompat.from(this)


        notificationManager.notify(1, notification)
        Log.e("DES", "si llegue")
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

    private fun sendStatusBroadcast(success: Boolean) {
        val intent = Intent(QuestionnaireResumeActivity.ACTION_NOTIFY_DOWNLOAD)
        intent.putExtra("success", success)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

}