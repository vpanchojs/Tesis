package ec.edu.unl.blockstudy.newQuestionnaire

import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.newQuestionnaire.events.NewQuestionaireEvents
import ec.edu.unl.blockstudy.newQuestionnaire.ui.NewQuestionaireView
import org.greenrobot.eventbus.Subscribe

/**
 * Created by victor on 5/2/18.
 */
class NewQuestionairePresenterImp(var eventBus: EventBusInterface, var view: NewQuestionaireView, var interactor: NewQuestionaireInteractor) : NewQuestionairePresenter {

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
    override fun onEventNewQuestionaireThread(event: NewQuestionaireEvents) {
        when (event.type) {
            NewQuestionaireEvents.ON_POST_QUESTIONAIRE_SUCCESS -> {
                view.hideProgressDialog()
                view.showMessagge("Cuestionario Publicado")
                view.navigationToQuestionaire()
            }
            NewQuestionaireEvents.ON_POST_QUESTIONAIRE_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.any.toString())
            }
        }
    }

}