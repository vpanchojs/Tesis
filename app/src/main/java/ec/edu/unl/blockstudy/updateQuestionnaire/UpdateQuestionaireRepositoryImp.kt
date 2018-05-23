package ec.edu.unl.blockstudy.updateQuestionnaire

import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.updateQuestionnaire.events.UpdateQuestionaireEvents

/**
 * Created by victor on 5/2/18.
 */
class UpdateQuestionaireRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : UpdateQuestionaireRepository {

    override fun onUploadQuestionaire(questionaire: Questionaire) {
        firebaseApi.onUploadQuestionaire(questionaire, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(UpdateQuestionaireEvents.ON_POST_QUESTIONAIRE_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {
                postEvent(UpdateQuestionaireEvents.ON_POST_QUESTIONAIRE_ERROR, error!!)
            }
        })
    }


    override fun onGetQuestionaire(any: Any) {
        /*
        objectBoxApi.getDataQuestionnaire(any as Long, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(UpdateQuestionaireEvents.ON_GET_QUESTIONNAIRE_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })
        */
    }

    private fun postEvent(type: Int, any: Any) {
        var event = UpdateQuestionaireEvents(type, any)
        eventBus.post(event)
    }


}