package ec.com.dovic.aprendiendo.repository

import com.google.firebase.firestore.QuerySnapshot
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.repository.events.QuestionnaireRepositoryEvents

class QuestionnaireRepositoryRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : QuestionnaireRepositoryRepository {

    override fun postEvent(type: Int, any: Any) {
        val event = QuestionnaireRepositoryEvents(type, any)
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
                postEvent(QuestionnaireRepositoryEvents.ON_GET_QUESTIONAIRE_SUCCESS, questionnairesList)
            }

            override fun onError(error: Any?) {
                postEvent(QuestionnaireRepositoryEvents.ON_GET_QUESTIONAIRE_ERROR, error!!)
            }
        })
    }
}
