package ec.edu.unl.blockstudy.block

import com.google.firebase.firestore.DocumentSnapshot
import ec.edu.unl.blockstudy.block.events.BlockEvents
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.lib.base.EventBusInterface

class BlockRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var sharePreferencesApi: SharePreferencesApi, var objectBoxApi: ObjectBoxApi) : BlockRepository {

    override fun getQuestion(questionPath: String) {
        firebaseApi.getQuestionsByPath(questionPath, object : OnCallbackApis<DocumentSnapshot> {
            override fun onSuccess(response: DocumentSnapshot) {
                var question = response.toObject(Question::class.java)
                question!!.idCloud = response.id
                postEvent(BlockEvents.ON_GET_QUESTIONS_SUCCESS, question)

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
