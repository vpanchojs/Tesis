package ec.edu.unl.blockstudy.newQuestion

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.newQuestion.events.QuestionEvents

class QuestionRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var sharePreferencesApi: SharePreferencesApi, var objectBoxApi: ObjectBoxApi) : QuestionRepository {
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
                postEvent(QuestionEvents.ON_GET_ANSWERS_SUCCESS, answers!!)
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
        firebaseApi.updateQuestion(question, object : OnCallbackApis<Void> {
            override fun onSuccess(response: Void) {
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