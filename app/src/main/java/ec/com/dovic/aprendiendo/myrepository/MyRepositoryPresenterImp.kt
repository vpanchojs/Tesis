package ec.com.dovic.aprendiendo.myrepository

import android.util.Log
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.myrepository.events.MyRepositoryEvents
import ec.com.dovic.aprendiendo.myrepository.ui.MyRepositoryView
import org.greenrobot.eventbus.Subscribe

/**
 * Created by victor on 24/2/18.
 */
class MyRepositoryPresenterImp(var eventBus: EventBusInterface, var view: MyRepositoryView, var interactor: MyRepositoryInteractor) : MyRepositoryPresenter {

    override fun onResume() {
        Log.e("PR", "REGISTRE")
        eventBus.register(this)
    }

    override fun onPause() {
        eventBus.unregister(this)
        Log.e("PR", "unregistre")
    }

    override fun onGetmyrepository() {
        view.none_results(false)
        view.showProgress(true)
        interactor.onGetmyrepository()
    }

    override fun onCreateQuestionaire(title: String, description: String) {
        interactor.onCreateQuestionaire(title, description)
    }

    @Subscribe
    override fun onEventThread(event: MyRepositoryEvents) {
        when (event.type) {
            MyRepositoryEvents.ON_GET_QUESTIONAIRE_SUCCESS -> {
                view.showProgress(false)
                var questionnaire_list = event.any as List<Questionaire>
                if (questionnaire_list.size > 0)
                    view.setQuestionnaries(questionnaire_list)
                else
                    view.none_results(true)
            }
            MyRepositoryEvents.ON_GET_QUESTIONAIRE_ERROR -> {
                view.showMessagge(event.any.toString())
                view.showButtonCreateQuestionnaire()

            }
            MyRepositoryEvents.ON_CREATE_QUESTIONAIRE_SUCCESS -> {
                view.navigationManageQuestionnaire(event.any as Questionaire)
                view.hideDialogNewQuestionnaire()
                view.showMessagge("Cuestionario Creado")
            }
        }
    }


}