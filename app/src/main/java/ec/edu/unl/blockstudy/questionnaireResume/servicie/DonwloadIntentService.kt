package ec.edu.unl.blockstudy.questionnaireResume.servicie

import android.app.IntentService
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.entities.objectBox.AnswerBd
import ec.edu.unl.blockstudy.entities.objectBox.QuestionBd
import ec.edu.unl.blockstudy.entities.objectBox.QuestionnaireBd
import ec.edu.unl.blockstudy.util.BaseActivitys


/**
 * Created by victor on 28/3/18.
 */
class DonwloadIntentService : IntentService("DonwloadIntentService") {

    lateinit var application: MyApplication
    lateinit var firebaseApi: FirebaseApi
    lateinit var objectBoxApi: ObjectBoxApi

    companion object {
        val IDQUESTIONNAIRE = "id"
        val TAG = "DowloadServicie"
    }

    override fun onCreate() {
        super.onCreate()
        setupInjection()
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        firebaseApi = application.domainModule!!.providesFirebaseApi()
        objectBoxApi = application.domainModule!!.providesObjectBoxApi()
    }

    override fun onHandleIntent(p0: Intent?) {
        var idQuestionnaire = p0!!.getStringExtra(IDQUESTIONNAIRE)
        Log.e("servicie", idQuestionnaire)

        /*Solicitamos el cuestionario al servidor*/
        firebaseApi.getQuestionnarie(idQuestionnaire, object : OnCallbackApis<DocumentSnapshot> {
            override fun onSuccess(response: DocumentSnapshot) {

                val questionaire = response.toObject(Questionaire::class.java)
                /*Llenamos el objecto de cuestionara bd*/
                var questionnaireBd = QuestionnaireBd()
                questionnaireBd.idCloud = response.id
                questionnaireBd.description = questionaire!!.description
                questionnaireBd.idUser = questionaire!!.idUser
                questionnaireBd.title = questionaire!!.title

                /*Solicitamos las preguntas al servidor*/
                firebaseApi.getQuestions(idQuestionnaire, object : OnCallbackApis<QuerySnapshot> {
                    override fun onSuccess(response: QuerySnapshot) {
                        var questListBd = ArrayList<QuestionBd>()
                        /*Recorremos toda la lista de preguntas*/
                        response.forEach {
                            val question = it.toObject(Question::class.java)

                            /*Llenamos una pregunta*/
                            var quest = QuestionBd()
                            quest.statement = quest.statement
                            quest.photoUrl = quest.photoUrl

                            /*Llenamos las respuestas de esa pregunta*/
                            var ansListBd = ArrayList<AnswerBd>()
                            quest.answers.forEach {
                                var ans = AnswerBd()
                                ans.statement = it.statement
                                ans.correct = it.correct
                                ansListBd.add(ans)
                            }
                            quest.answers.addAll(ansListBd)
                            questListBd.add(quest)
                        }
                        questionnaireBd.questions.addAll(questListBd)

                        objectBoxApi.createQuestionaire(questionnaireBd, object : OnCallbackApis<Unit> {
                            override fun onSuccess(response: Unit) {
                                BaseActivitys.showToastMessage(applicationContext, "Cuestionario Descargador", Toast.LENGTH_LONG)
                            }

                            override fun onError(error: Any?) {
                                BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
                            }
                        })
                    }

                    override fun onError(error: Any?) {
                        BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
                    }
                })

            }

            override fun onError(error: Any?) {
                BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
            }
        })

    }

}