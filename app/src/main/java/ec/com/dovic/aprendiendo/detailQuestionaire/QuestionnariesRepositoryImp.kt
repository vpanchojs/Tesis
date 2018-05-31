package ec.com.dovic.aprendiendo.detailQuestionaire

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import ec.com.dovic.aprendiendo.detailQuestionaire.events.QuestionnaireEvents
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface

/**
 * Created by victor on 25/2/18.
 */
class QuestionnariesRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var dbApi: DbApi) : QuestionnariesRepository {


    override fun isExistQuestionnnaireLocal(idCloud: String) {
        dbApi.isExistQuestionnaire(firebaseApi.getUid(), idCloud, object : OnCallbackApis<Boolean> {
            override fun onSuccess(response: Boolean) {
                Log.e("res", "repondio")
                postEvent(QuestionnaireEvents.ON_IS_EXIST_QUESTIONNNAIRE_LOCAL, response)
            }

            override fun onError(error: Any?) {

            }
        })
    }


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
    }

    override fun onSaveQuestion(idQuestionarie: Long, question: Question) {

    }

    override fun updateBasicQuestionnaire(questionaire: Questionaire) {

        firebaseApi.updateBasicInfoQuestionnnaire(questionaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionnaireEvents.ON_UPDATE_BASIC_QUESTIONNAIRE, response!!)
            }

            override fun onError(error: Any?) {

            }
        })

    }

    override fun onDeleteQuestionnnaire(idQuestionaire: Any) {

        firebaseApi.deleteQuestionnnaire(idQuestionaire.toString(), object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(QuestionnaireEvents.ON_DELETE_QUESTIONNAIRE_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })

    }

    private fun postEvent(type: Int, any: Any) {
        eventBus.post(QuestionnaireEvents(type, any))
    }
}