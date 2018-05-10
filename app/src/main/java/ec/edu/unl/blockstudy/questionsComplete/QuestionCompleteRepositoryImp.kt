package ec.edu.unl.blockstudy.questionsComplete

import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.questionsComplete.events.QuestionCompleteEvents

/**
 * Created by victor on 5/3/18.
 */
class QuestionCompleteRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var objectBoxApi: ObjectBoxApi) : QuestionCompleteRepository {


    override fun onGetQuestionAll(idQuestionnaire: Any) {

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