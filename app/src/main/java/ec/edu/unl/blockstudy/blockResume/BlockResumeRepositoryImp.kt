package ec.edu.unl.blockstudy.blockResume

import android.util.Log
import ec.edu.unl.blockstudy.blockResume.events.BlockResumeEvents
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Block
import ec.edu.unl.blockstudy.entities.QuestionnaireBlock
import ec.edu.unl.blockstudy.entities.objectBox.QuestionnaireBd
import ec.edu.unl.blockstudy.lib.base.EventBusInterface

class BlockResumeRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var objectBoxApi: ObjectBoxApi, var sharePreferencesApi: SharePreferencesApi) : BlockResumeRepository {

    override fun setTimeActivity(block: Block) {
        objectBoxApi.onUpdateBlock(block, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(BlockResumeEvents.ON_GET_BLOCKDATA_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {

            }
        })
    }

    override fun getQuestionnaires() {
        objectBoxApi.getMyQuestionnaries(object : OnCallbackApis<List<QuestionnaireBd>> {
            override fun onSuccess(response: List<QuestionnaireBd>) {
                postEvent(BlockResumeEvents.ON_GET_QUESTIONAIRE_SUCCESS, response)
            }

            override fun onError(error: Any?) {
                postEvent(BlockResumeEvents.ON_GET_QUESTIONAIRE_ERROR, error!!)
            }
        })
        /*
        firebaseApi.getMyQuestionnaries(firebaseApi.getUid(), object : OnCallbackApis<QuerySnapshot> {
            override fun onSuccess(response: QuerySnapshot) {
                var questionnairesList = ArrayList<Questionaire>()

                response.documents.forEach {
                    var questionaire = it.toObject(Questionaire::class.java)
                    questionaire!!.idCloud = it.id
                    questionnairesList.add(questionaire)
                }

                postEvent(BlockResumeEvents.ON_GET_QUESTIONAIRE_SUCCESS, questionnairesList!!)
            }

            override fun onError(error: Any?) {
                postEvent(BlockResumeEvents.ON_GET_QUESTIONAIRE_ERROR, error!!)
            }
        })
        */
    }

    override fun setApplications(apps: List<String>, id: Long) {
        objectBoxApi.setApplications(apps, id, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                getDataBlock()
            }

            override fun onError(error: Any?) {

            }
        })
    }

    override fun getDataBlock() {
        objectBoxApi.getBlockData(firebaseApi.getUid(), object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(BlockResumeEvents.ON_GET_BLOCKDATA_SUCCESS, response!!)
            }

            override fun onError(error: Any?) {
                postEvent(BlockResumeEvents.ON_GET_BLOCKDATA_ERROR, error!!)
            }
        })
    }

    override fun addQuestionnaire(questionaire: QuestionnaireBlock) {
        objectBoxApi.addQuestionnaireBlock(questionaire, object : OnCallbackApis<Unit> {
            override fun onSuccess(response: Unit) {
                Log.e("a", "se guardo")
            }

            override fun onError(error: Any?) {

            }
        })
    }

    override fun removeQuestionnaire(idQuestionaire: Long) {
        objectBoxApi.removeQuestionnaireBlock(idQuestionaire, object : OnCallbackApis<Unit> {
            override fun onSuccess(response: Unit) {

            }

            override fun onError(error: Any?) {

            }
        })
    }

    override fun postEvent(type: Int, any: Any) {
        var event = BlockResumeEvents(type, any)
        eventBus.post(event)
    }

}
