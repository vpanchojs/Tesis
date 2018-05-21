package ec.edu.unl.blockstudy.myrepository

import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.myrepository.events.MyRepositoryEvents

/**
 * Created by victor on 24/2/18.
 */
class MyRepositoryRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : MyRepositoryRepository {

    override fun onGetmyrepository() {
        firebaseApi.getMyQuestionnaries(firebaseApi.getUid(), object : OnCallbackApis<QuerySnapshot> {
            override fun onSuccess(response: QuerySnapshot) {
                var questionnairesList = ArrayList<Questionaire>()

                response.documents.forEach {
                    var questionaire = it.toObject(Questionaire::class.java)
                    questionaire!!.idCloud = it.id
                    questionnairesList.add(questionaire)
                }

                postEvent(MyRepositoryEvents.ON_GET_QUESTIONAIRE_SUCCESS, questionnairesList!!)

            }

            override fun onError(error: Any?) {

            }
        })

    }

    override fun onCreateQuestionaire(questionaire: Questionaire) {
        firebaseApi.createQuestionnaire(questionaire, object : OnCallbackApis<Questionaire> {
            override fun onSuccess(response: Questionaire) {
                postEvent(MyRepositoryEvents.ON_CREATE_QUESTIONAIRE_SUCCESS, response!!)
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