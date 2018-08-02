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
import com.google.firebase.firestore.QuerySnapshot
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.database.AnswerBd
import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.questionnaireResume.ui.QuestionnaireResumeActivity
import ec.com.dovic.aprendiendo.util.BaseActivitys


/**
 * Created by victor on 28/3/18.
 */
class DonwloadIntentService : IntentService("DonwloadIntentService") {

    lateinit var application: MyApplication
    lateinit var firebaseApi: FirebaseApi
    lateinit var dbApi: DbApi
    lateinit var questionaire: Questionaire
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
        firebaseApi.getQuestionnaireComplete(idQuestionnaire, isDownLoad, object : OnCallbackApis<Questionaire> {
            override fun onSuccess(response: Questionaire) {

                /*
                questionaire = response

                var questionnaireBd = QuestionnaireBd()
                questionnaireBd.idCloud = response.idCloud
                questionnaireBd.description = questionaire.description
                questionnaireBd.idUser = questionaire.idUser
                questionnaireBd.idUserLocal = firebaseApi.getUid()
                questionnaireBd.me = questionaire.idUser.equals(firebaseApi.getUid())
                questionnaireBd.title = questionaire.title
                questionnaireBd.numberQuest = questionaire.numberQuest


                dbApi.insertQuestionnaire(questionnaireBd, object : OnCallbackApis<Long> {
                    override fun onSuccess(id: Long) {
                        Log.e("cues", "se guardo cuestionario")

                        firebaseApi.getQuestions(idQuestionnaire, object : OnCallbackApis<QuerySnapshot> {
                            override fun onSuccess(response: QuerySnapshot) {

                                //CONTROL
                                questionsSize = response.size()

                                /*Recorremos toda la lista de preguntas*/
                                response.forEach {
                                    var question = it.toObject(Question::class.java)
                                    question.idCloud = it.id


                                    /*Llenamos una pregunta*/
                                    var quest = QuestionBd()
                                    quest.statement = question.statement
                                    quest.photoUrl = question.photoUrl
                                    quest.questionnaireId = id

                                    dbApi.insertQuestion(quest, object : OnCallbackApis<Long> {
                                        override fun onSuccess(response: Long) {
                                            //CONTROL
                                            cont++

                                            Log.e("cues", "se guardo pregunta")
                                            question.answers.forEach {

                                                var answer = AnswerBd()
                                                answer.statement = it.statement
                                                answer.correct = it.correct
                                                answer.questionId = response

                                                dbApi.insertAnswer(answer, object : OnCallbackApis<Long> {
                                                    override fun onSuccess(response: Long) {
                                                        Log.e("cues", "se guardo respuesta")

                                                    }

                                                    override fun onError(error: Any?) {
                                                        cicle = false
                                                        correct = false

                                                        BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
                                                    }
                                                })

                                            }

                                        }

                                        override fun onError(error: Any?) {
                                            cicle = false
                                            correct = false
                                            BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
                                        }
                                    })

                                }


                            }

                            override fun onError(error: Any?) {
                                cicle = false
                                correct = false
                                BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
                            }
                        })

                    }

                    override fun onError(error: Any?) {
                        cicle = false
                        correct = false
                        BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
                    }
                })

*/
            }

            override fun onError(error: Any?) {
                /*
                cicle = false
                correct = false
                BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
                */
            }
        })


        /*
        while (cicle) {
            Log.e(TAG, "descargando cuestionario")
            cicle = !(cont == questionsSize)
            Thread.sleep(300)
        }


        if (correct) {
            setNotification(questionaire.title!!)
            sendStatusBroadcast(true)
        }
        */
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