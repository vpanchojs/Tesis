package ec.edu.unl.blockstudy.repository

import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.repository.events.QuestionnaireRepositoryEvents

class QuestionnaireRepositoryRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : QuestionnaireRepositoryRepository {

    override fun postEvent(type: Int, any: Any) {
        var event = QuestionnaireRepositoryEvents(type, any)
        eventBus.post(event)
    }

    override fun onGetQuestionnaireRepo() {
        firebaseApi.getQuestionnairesRepo(object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                var querySnapshot = response as QuerySnapshot
                var questionnairesList = ArrayList<Questionaire>()

                querySnapshot.documents.forEach {
                    var questionaire = it.toObject(Questionaire::class.java)
                    questionaire!!.idCloud = it.id
                    questionnairesList.add(questionaire)
                }
                postEvent(QuestionnaireRepositoryEvents.ON_GET_QUESTIONAIRE_SUCCESS, questionnairesList!!)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionnaireRepositoryEvents.ON_GET_QUESTIONAIRE_ERROR, error!!)
            }
        })
    }
}
