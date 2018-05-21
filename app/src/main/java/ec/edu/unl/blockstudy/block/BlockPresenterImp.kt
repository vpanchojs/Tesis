package ec.edu.unl.blockstudy.block

import ec.edu.unl.blockstudy.block.events.BlockEvents
import ec.edu.unl.blockstudy.block.ui.BlockView
import ec.edu.unl.blockstudy.database.QuestionBd
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
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