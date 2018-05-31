package ec.com.dovic.aprendiendo.newQuestion

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.entities.Answer
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.newQuestion.events.QuestionEvents

class QuestionRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : QuestionRepository {
    override fun onCreateQuestion(question: Question, idQuestionnaire: String) {
        firebaseApi.onCreateQuestion(question, idQuestionnaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionEvents.ON_CREATE_QUESTION_SUCCESS, Any())
            }

            override fun onError(error: Any?) {
                postEvent(QuestionEvents.ON_CREATE_QUESTION_ERROR, error!!)
            }
        })

    }

    override fun onGetDataQuestion(idQuestion: Any, idQuesitonnaire: Any) {

        firebaseApi.onGetAnswers(idQuestion, idQuesitonnaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                var aux = response as QuerySnapshot
                var answers = ArrayList<Answer>()
                aux.documents.forEach {
                    var answer = it.toObject(Answer::class.java)
                    // answer.idCloud = it.id
                    answers.add(answer!!)
                }
                postEvent(QuestionEvents.ON_GET_ANSWERS_SUCCESS, answers)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionEvents.ON_GET_ANSWERS_ERROR, error!!)
            }
        })
        /*
        firebaseApi.onGetAnswers(idQuestion, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionEvents.ON_GET_ANSWERS_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionEvents.ON_GET_ANSWERS_ERROR, error!!)
            }
        })
        */
    }

    override fun onUpdateQuestion(question: Question) {
        firebaseApi.updateQuestion(question, object : OnCallbackApis<Unit> {
            override fun onSuccess(response: Unit) {
                Log.e("uppdate", "se ha actualizado")
                postEvent(QuestionEvents.ON_UPDATE_QUESTION_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })

        /*
        objectBoxApi.onUpdateQuestion(question, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                Log.e("uppdate", "se ha actualizado")
                postEvent(QuestionEvents.ON_UPDATE_QUESTION_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })
        */
    }

    override fun onDeteleQuestion(idQuestion: String, idQuestionnaire: String) {

        firebaseApi.onDeleteQuestion(idQuestion, idQuestionnaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionEvents.ON_DELETE_QUESTION_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })

        /*objectBoxApi.onDeleteQuestion(idQuestion, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionEvents.ON_DELETE_QUESTION_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })
        */
    }

    private fun postEvent(type: Int, any: Any) {
        var event = QuestionEvents(type, any)
        eventBus.post(event)
    }
}