package ec.com.dovic.aprendiendo.block

import ec.com.dovic.aprendiendo.block.events.BlockEvents
import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface

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
