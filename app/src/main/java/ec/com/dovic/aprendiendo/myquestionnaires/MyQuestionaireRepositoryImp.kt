package ec.com.dovic.aprendiendo.myquestionnaires

import android.util.Log
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.myquestionnaires.events.MyQuestionaireEvents

/**
 * Created by victor on 24/2/18.
 */
class MyQuestionaireRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var db: DbApi) : MyQuestionaireRepository {

    override fun onGetMyQuestionnaires() {

        db.getMyQuestionnaires(firebaseApi.getUid(), object : OnCallbackApis<List<QuestionnaireBd>> {
            override fun onSuccess(response: List<QuestionnaireBd>) {
                Log.e("myq", "AAAAA")
                /*
                response.forEach {
                    it.me = it.pk.equals(firebaseApi.getUid())
                }
                */
                postEvent(MyQuestionaireEvents.ON_GET_QUESTIONAIRE_SUCCESS, response)
            }

            override fun onError(error: Any?) {

            }
        })
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