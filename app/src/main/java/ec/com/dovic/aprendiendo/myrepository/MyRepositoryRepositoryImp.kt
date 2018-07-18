package ec.com.dovic.aprendiendo.myrepository

import com.google.firebase.firestore.QuerySnapshot
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.myrepository.events.MyRepositoryEvents

/**
 * Created by victor on 24/2/18.
 */
class MyRepositoryRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : MyRepositoryRepository {

    override fun onGetmyrepository() {
        firebaseApi.getMyQuestionnaries(firebaseApi.getUid(), object : OnCallbackApis<QuerySnapshot> {
            override fun onSuccess(response: QuerySnapshot) {
                response.metadata.isFromCache
                val questionnairesList = ArrayList<Questionaire>()

                response.documents.forEach {
                    val questionaire = it.toObject(Questionaire::class.java)
                    questionaire!!.idCloud = it.id
                    questionnairesList.add(questionaire)
                }
                postEvent(MyRepositoryEvents.ON_GET_QUESTIONAIRE_SUCCESS, Pair(questionnairesList, response.metadata.isFromCache))
            }

            override fun onError(error: Any?) {

            }
        })

    }

    override fun onCreateQuestionaire(questionaire: Questionaire) {
        firebaseApi.createQuestionnaire(questionaire, object : OnCallbackApis<Questionaire> {
            override fun onSuccess(response: Questionaire) {
                postEvent(MyRepositoryEvents.ON_CREATE_QUESTIONAIRE_SUCCESS, response)
            }

            override fun onError(error: Any?) {
                postEvent(MyRepositoryEvents.ON_CREATE_QUESTIONAIRE_ERROR, error!!)
            }
        })

    }


    private fun postEvent(type: Int, any: Any) {
        var event = MyRepositoryEvents(type, any)
        eventBus.post(event)
    }
}