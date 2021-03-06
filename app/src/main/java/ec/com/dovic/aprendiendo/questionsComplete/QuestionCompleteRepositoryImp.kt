package ec.com.dovic.aprendiendo.questionsComplete

import com.google.firebase.firestore.QuerySnapshot
import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.entities.Answer
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.questionsComplete.events.QuestionCompleteEvents

/**
 * Created by victor on 5/3/18.
 */
class QuestionCompleteRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var dbApi: DbApi) : QuestionCompleteRepository {


    override fun deleteQuestionnarie(questionnaireBd: QuestionnaireBd) {
        dbApi.deleteQuestionnaire(questionnaireBd, object : OnCallbackApis<Int> {
            override fun onSuccess(response: Int) {
                postEvent(QuestionCompleteEvents.ON_DELETE_SUCCESS, response)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionCompleteEvents.ON_DELETE_ERROR, error!!)
            }
        })
    }

    override fun onGetQuestionAll(idQuestionnaire: Any) {
        dbApi.getQuestions(idQuestionnaire.toString().toLong(), object : OnCallbackApis<List<QuestionBd>> {
            override fun onSuccess(response: List<QuestionBd>) {
                postEvent(QuestionCompleteEvents.ON_GET_QUESTIONS_SUCCESS, response)
            }

            override fun onError(error: Any?) {

            }
        })

        /*
        firebaseApi.getQuestions(idQuestionnaire.toString(), object : OnCallbackApis<QuerySnapshot> {
            override fun onSuccess(response: QuerySnapshot) {
                //var aux = response as QuerySnapshot
                var questionsList = ArrayList<Question>()
                response.documents.forEach {
                    var question = it.toObject(Question::class.java)
                    question!!.idCloud = it.id
                    //question.answers = ArrayList<Answer>()
                    questionsList.add(question)
                }
                postEvent(QuestionCompleteEvents.ON_GET_QUESTIONS_SUCCESS, questionsList!!)
            }

            override fun onError(error: Any?) {

            }
        })
        */


        /*
        objectBoxApi.getDataQuestionnaire(idQuestionnaire as Long, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionCompleteEvents.ON_GET_QUESTIONS_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })
        */
    }

    override fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String) {
        firebaseApi.onGetAnswers(idCloud, idQuestionnaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                var aux = response as QuerySnapshot
                var answers = ArrayList<Answer>()
                aux.documents.forEach {
                    var answer = it.toObject(Answer::class.java)
                    // answer.idCloud = it.id
                    // answer.idQuestion = idCloud
                    answers.add(answer!!)
                }
                postEvent(QuestionCompleteEvents.ON_GET_ANSWERS_SUCCESS, answers)
            }

            override fun onError(error: Any?) {

            }
        })
    }

    override fun postEvent(type: Int, any: Any) {
        var event = QuestionCompleteEvents(type, any)
        eventBus.post(event)
    }
}