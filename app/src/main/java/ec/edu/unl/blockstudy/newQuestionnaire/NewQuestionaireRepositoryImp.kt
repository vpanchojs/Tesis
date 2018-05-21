package ec.edu.unl.blockstudy.newQuestionnaire

import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.newQuestionnaire.events.NewQuestionaireEvents

/**
 * Created by victor on 5/2/18.
 */
class NewQuestionaireRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : NewQuestionaireRepository {

    override fun onUploadQuestionaire(questionaire: Questionaire) {
        firebaseApi.onUploadQuestionaire(questionaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(NewQuestionaireEvents.ON_POST_QUESTIONAIRE_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {
                postEvent(NewQuestionaireEvents.ON_POST_QUESTIONAIRE_ERROR, error!!)
            }
        })
    }


    override fun onGetQuestionaire(any: Any) {
        /*
        objectBoxApi.getDataQuestionnaire(any as Long, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(NewQuestionaireEvents.ON_GET_QUESTIONNAIRE_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })
        */
    }

    private fun postEvent(type: Int, any: Any) {
        var event = NewQuestionaireEvents(type, any)
        eventBus.post(event)
    }


}