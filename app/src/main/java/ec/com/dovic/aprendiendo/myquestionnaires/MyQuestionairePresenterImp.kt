package ec.com.dovic.aprendiendo.myquestionnaires

import android.util.Log
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.myquestionnaires.events.MyQuestionaireEvents
import ec.com.dovic.aprendiendo.myquestionnaires.ui.MyQuestionnariesView
import org.greenrobot.eventbus.Subscribe

/**
 * Created by victor on 24/2/18.
 */
class MyQuestionairePresenterImp(var eventBus: EventBusInterface, var view: MyQuestionnariesView, var interactor: MyQuestionaireInteractor) : MyQuestionairePresenter {

    override fun onResume() {
        Log.e("PR", "REGISTRE")
        eventBus.register(this)
    }

    override fun onPause() {
        eventBus.unregister(this)
        Log.e("PR", "unregistre")
    }

    override fun onGetMyQuestionnaires() {
        view.none_results(false)
        view.showProgress(true)
        interactor.onGetMyQuestionnaires()
    }

    override fun onCreateQuestionaire(title: String, description: String) {
        interactor.onCreateQuestionaire(title, description)
    }

    @Subscribe
    override fun onEventThread(event: MyQuestionaireEvents) {
        when (event.type) {
            MyQuestionaireEvents.ON_GET_QUESTIONAIRE_SUCCESS -> {
                view.showProgress(false)
                var questionnaire_list = event.any as List<QuestionnaireBd>
                if (questionnaire_list.size > 0)
                    view.setQuestionnaries(questionnaire_list)
                else
                    view.none_results(true)
            }
            MyQuestionaireEvents.ON_GET_QUESTIONAIRE_ERROR -> {
                view.showMessagge(event.any.toString())
                view.showButtonCreateQuestionnaire()

            }
            MyQuestionaireEvents.ON_CREATE_QUESTIONAIRE_SUCCESS -> {
                view.navigationManageQuestionnaire(event.any as QuestionnaireBd)
                view.hideDialogNewQuestionnaire()
                view.showMessagge("Cuestionario Creado")
            }
        }
    }


}