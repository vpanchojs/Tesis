package ec.edu.unl.blockstudy.questionnaireResume.servicie

import android.app.IntentService
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.database.AnswerBd
import ec.edu.unl.blockstudy.database.QuestionBd
import ec.edu.unl.blockstudy.database.QuestionnaireBd
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.services.DbApi
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.util.BaseActivitys


/**
 * Created by victor on 28/3/18.
 */
class DonwloadIntentService : IntentService("DonwloadIntentService") {

    lateinit var application: MyApplication
    lateinit var firebaseApi: FirebaseApi
    lateinit var dbApi: DbApi

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
        dbApi = application.domainModule!!.providesDbApi()
    }

    override fun onHandleIntent(p0: Intent?) {
        var idQuestionnaire = p0!!.getStringExtra(IDQUESTIONNAIRE)
        Log.e("servicie", idQuestionnaire)

        /*Solicitamos el cuestionario al servidor*/
        firebaseApi.getQuestionnarie(idQuestionnaire, object : OnCallbackApis<DocumentSnapshot> {
            override fun onSuccess(responseQuestionnaire: DocumentSnapshot) {

                val questionaire = responseQuestionnaire.toObject(Questionaire::class.java)
                /*Llenamos el objecto de cuestionara bd*/
                var questionnaireBd = QuestionnaireBd()
                questionnaireBd.idCloud = responseQuestionnaire.id
                questionnaireBd.description = questionaire!!.description
                questionnaireBd.idUser = questionaire.idUser
                questionnaireBd.title = questionaire.title


                dbApi.insertQuestionnaire(questionnaireBd, object : OnCallbackApis<Long> {
                    override fun onSuccess(id: Long) {
                        Log.e("cues", "se guardo cuestionario")

                        firebaseApi.getQuestions(idQuestionnaire, object : OnCallbackApis<QuerySnapshot> {
                            override fun onSuccess(response: QuerySnapshot) {
                                var questionsList = ArrayList<QuestionBd>()


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
                                            Log.e("cues", "se guardo pregunta")
                                            question.answers.forEach {

                                                var answer = AnswerBd()
                                                answer.statement = it.statement
                                                answer.correct = it.correct
                                                answer.questionId = response

                                                //arrayListAnswers.add(answer)

                                                dbApi.insertAnswer(answer, object : OnCallbackApis<Long> {
                                                    override fun onSuccess(response: Long) {
                                                        Log.e("cues", "se guardo respuesta")

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
                                //questionnaireBd.questions = questionsList


                            }

                            override fun onError(error: Any?) {
                                BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
                            }
                        })

                    }

                    override fun onError(error: Any?) {

                    }
                })


            }

            override fun onError(error: Any?) {
                BaseActivitys.showToastMessage(applicationContext, "Error descargando", Toast.LENGTH_LONG)
            }
        })

    }

}