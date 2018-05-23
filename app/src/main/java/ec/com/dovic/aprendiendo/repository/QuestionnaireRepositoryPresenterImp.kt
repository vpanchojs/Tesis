package ec.com.dovic.aprendiendo.repository

import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.repository.events.QuestionnaireRepositoryEvents
import ec.com.dovic.aprendiendo.repository.ui.QuestionnaireRepositoryView
import org.greenrobot.eventbus.Subscribe

class QuestionnaireRepositoryPresenterImp(var eventBus: EventBusInterface, var view: QuestionnaireRepositoryView, var interactor: QuestionnaireRepositoryInteractor) : QuestionnaireRepositoryPresenter {

    override fun onGetQuestionnaireRepo() {
        view.none_results(false)
        view.showProgress(true)
        interactor.onGetQuestionnaireRepo()
    }

    override fun onSuscribe() {
        eventBus.register(this)
    }

    override fun onUnSuscribe() {
        eventBus.unregister(this)
    }

    @Subscribe
    fun onEventThread(event: QuestionnaireRepositoryEvents) {
        view.showProgress(false)
        when (event.type) {

            QuestionnaireRepositoryEvents.ON_GET_QUESTIONAIRE_SUCCESS -> {

                var questionnaire_list = event.any as List<Questionaire>
                if (questionnaire_list.size > 0)
                    view.setQuestionnaries(questionnaire_list)
                else
                    view.none_results(true)

            }
            QuestionnaireRepositoryEvents.ON_GET_QUESTIONAIRE_ERROR -> {

            }
        }
    }

}
