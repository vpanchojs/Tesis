package ec.edu.unl.blockstudy.repository

import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.repository.events.QuestionnaireRepositoryEvents
import ec.edu.unl.blockstudy.repository.ui.QuestionnaireRepositoryView
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
