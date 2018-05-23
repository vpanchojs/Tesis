package ec.com.dovic.aprendiendo.block

import ec.com.dovic.aprendiendo.block.events.BlockEvents
import ec.com.dovic.aprendiendo.block.ui.BlockView
import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import org.greenrobot.eventbus.Subscribe

class BlockPresenterImp(var eventBus: EventBusInterface, var view: BlockView, var interactor: BlockInteractor) : BlockPresenter {

    override fun onSuscribe() {
        eventBus.register(this)
    }

    override fun onUnSuscribe() {
        eventBus.unregister(this)
    }

    override fun getQuestion(ids: ArrayList<Long>) {
        interactor.getQuestion(ids)
    }

    @Subscribe
    fun onEventThread(event: BlockEvents) {
        when (event.type) {
            BlockEvents.ON_GET_QUESTIONS_SUCCESS -> {
                view.setQuestionsAll(event.any as ArrayList<QuestionBd>)
            }

            BlockEvents.ON_GET_QUESTIONS_ERROR -> {

            }
        }
    }
}