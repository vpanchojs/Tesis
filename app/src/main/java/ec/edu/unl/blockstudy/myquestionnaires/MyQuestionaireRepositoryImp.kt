package ec.edu.unl.blockstudy.myquestionnaires

import android.util.Log
import ec.edu.unl.blockstudy.database.QuestionBd
import ec.edu.unl.blockstudy.database.QuestionnaireBd
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.services.DbApi
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.myquestionnaires.events.MyQuestionaireEvents

/**
 * Created by victor on 24/2/18.
 */
class MyQuestionaireRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var db: DbApi) : MyQuestionaireRepository {

    override fun onGetMyQuestionnaires() {

        db.getMyQuestionnaires(object : OnCallbackApis<List<QuestionnaireBd>> {
            override fun onSuccess(response: List<QuestionnaireBd>) {
                Log.e("myq", "AAAAA")

                /*
                db.getQuestions(object : OnCallbackApis<List<QuestionBd>> {
                    override fun onSuccess(response: List<QuestionBd>) {
                        Log.e("question","preguntas ${response.size}")
                    }

                    override fun onError(error: Any?) {

                    }
                })
                */
                postEvent(MyQuestionaireEvents.ON_GET_QUESTIONAIRE_SUCCESS, response)
            }

            override fun onError(error: Any?) {

            }
        })
        /*
        objectBoxApi.getMyQuestionnaries(object : OnCallbackApis<List<QuestionnaireBd>> {
            override fun onSuccess(response: List<QuestionnaireBd>) {
                postEvent(MyQuestionaireEvents.ON_GET_QUESTIONAIRE_SUCCESS, response)
            }

            override fun onError(error: Any?) {

            }
        })
       */
    }

    override fun onCreateQuestionaire(questionaire: Questionaire) {
        firebaseApi.createQuestionnaire(questionaire, object : OnCallbackApis<Questionaire> {
            override fun onSuccess(response: Questionaire) {
                postEvent(MyQuestionaireEvents.ON_CREATE_QUESTIONAIRE_SUCCESS, response)
            }

            override fun onError(error: Any?) {
                postEvent(MyQuestionaireEvents.ON_CREATE_QUESTIONAIRE_ERROR, error!!)
            }
        })

    }


    private fun postEvent(type: Int, any: Any) {
        var event = MyQuestionaireEvents(type, any)
        eventBus.post(event)
    }
}