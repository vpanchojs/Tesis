package ec.edu.unl.blockstudy.detailQuestionaire

import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.detailQuestionaire.events.QuestionnaireEvents
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.lib.base.EventBusInterface

/**
 * Created by victor on 25/2/18.
 */
class QuestionnariesRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : QuestionnariesRepository {
    override fun onGetDataQuestionnaire(any: Any) {
        firebaseApi.getQuestions(any.toString(), object : OnCallbackApis<QuerySnapshot> {
            override fun onSuccess(response: QuerySnapshot) {

                var questionsList = ArrayList<Question>()
                response.documents.forEach {
                    var question = it.toObject(Question::class.java)
                    question!!.idCloud = it.id
                    questionsList.add(question)
                }

                postEvent(QuestionnaireEvents.ON_GET_QUESTIONS_SUCCESS, questionsList)
            }

            override fun onError(error: Any?) {

            }
        })
        /*
        firebaseApi.getQuestions(any as String, object : FirebaseEventListenerCallback {

            override fun onDocumentRemoved(snapshot: DocumentSnapshot) {}

            override fun onError(error: Any) {

            }

            override fun onDocumentAdded(snapshot: DocumentSnapshot) {
                var question = snapshot.toObject(Question::class.java)
                question.idCloud = snapshot.id
                postEvent(QuestionnaireEvents.ON_GET_QUESTIONS_SUCCESS, question!!)
            }

            override fun onDocumentModified(snapshot: DocumentSnapshot) {}
        })
        */
    }

    override fun onSaveQuestion(idQuestionarie: Long, question: Question) {
        /* objectBoxApi.onSaveQuestion(idQuestionarie, question, object : onDomainApiActionListener {
             override fun onSuccess(response: Any?) {
                 Log.e("Pregunta", (response as Question).idQuestion.toString())
             }

             override fun onError(error: Any?) {

             }
         })
         */
    }

    override fun updateBasicQuestionnaire(questionaire: Questionaire) {

        firebaseApi.updateBasicInfoQuestionnnaire(questionaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionnaireEvents.ON_UPDATE_BASIC_QUESTIONNAIRE, response!!)
            }

            override fun onError(error: Any?) {

            }
        })

        /*objectBoxApi.updateQuestionnaire(questionaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {

                postEvent(QuestionnaireEvents.ON_UPDATE_BASIC_QUESTIONNAIRE, response!!)

            }

            override fun onError(error: Any?) {

            }
        })
        */
    }

    override fun onDeleteQuestionnnaire(idQuestionaire: Any) {

        firebaseApi.deleteQuestionnnaire(idQuestionaire.toString(), object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionnaireEvents.ON_DELETE_QUESTIONNAIRE_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })

        /*
        objectBoxApi.deleteQuestionnnaire(idQuestionaire as Long, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionnaireEvents.ON_DELETE_QUESTIONNAIRE_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })
        */
    }

    private fun postEvent(type: Int, any: Any) {
        var event = QuestionnaireEvents(type, any)
        eventBus.post(event)
    }
}