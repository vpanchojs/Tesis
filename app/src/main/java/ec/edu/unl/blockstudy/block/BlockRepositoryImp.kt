package ec.edu.unl.blockstudy.block

import ec.edu.unl.blockstudy.block.events.BlockEvents
import ec.edu.unl.blockstudy.database.QuestionBd
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.services.DbApi
import ec.edu.unl.blockstudy.lib.base.EventBusInterface

class BlockRepositoryImp(var eventBus: EventBusInterface, var dbApi: DbApi) : BlockRepository {

    override fun getQuestion(ids: ArrayList<Long>) {
        dbApi.getQuestionsAllForQuestionnairesAll(ids, object : OnCallbackApis<List<QuestionBd>> {
            override fun onSuccess(response: List<QuestionBd>) {
                postEvent(BlockEvents.ON_GET_QUESTIONS_SUCCESS, response)
            }

            override fun onError(error: Any?) {
                postEvent(BlockEvents.ON_GET_QUESTIONS_ERROR, error.toString())
            }
        })
    }

    fun postEvent(type: Int, any: Any) {
        var event = BlockEvents(type, any)
        eventBus.post(event)
    }
}
