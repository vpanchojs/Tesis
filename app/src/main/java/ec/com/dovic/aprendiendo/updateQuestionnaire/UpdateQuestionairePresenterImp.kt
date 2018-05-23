package ec.com.dovic.aprendiendo.updateQuestionnaire

import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.updateQuestionnaire.events.UpdateQuestionaireEvents
import ec.com.dovic.aprendiendo.updateQuestionnaire.ui.UpdateQuestionaireView
import org.greenrobot.eventbus.Subscribe

/**
 * Created by victor on 5/2/18.
 */
class UpdateQuestionairePresenterImp(var eventBus: EventBusInterface, var view: UpdateQuestionaireView, var interactor: UpdateQuestionaireInteractor) : UpdateQuestionairePresenter {

    override fun onResume() {
        eventBus.register(this)
    }

    override fun onPause() {
        eventBus.unregister(this)
    }

    override fun onUploadQuestionaire(questionaire: Questionaire) {
        view.showProgressDialog("Publicando Cuestionario")
        interactor.onUploadQuestionaire(questionaire)
    }


    override fun onGetQuestionaire(any: Any) {
        // interactor.onGetQuestionaire(any)
    }

    @Subscribe
    override fun onEventNewQuestionaireThread(event: UpdateQuestionaireEvents) {
        when (event.type) {
            UpdateQuestionaireEvents.ON_POST_QUESTIONAIRE_SUCCESS -> {
                view.hideProgressDialog()
                view.showMessagge("Cuestionario Publicado")
                view.navigationToQuestionaire()
            }
            UpdateQuestionaireEvents.ON_POST_QUESTIONAIRE_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.any.toString())
            }
        }
    }

}