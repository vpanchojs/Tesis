package ec.com.dovic.aprendiendo.blockResume

import android.util.Log
import ec.com.dovic.aprendiendo.blockResume.events.BlockResumeEvents
import ec.com.dovic.aprendiendo.database.Application
import ec.com.dovic.aprendiendo.database.Block
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.services.DbApi
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface

class BlockResumeRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var db: DbApi) : BlockResumeRepository {

    override fun setTimeActivity(time: Int) {
        db.updateTimeBlock(time, object : OnCallbackApis<Int>{
            override fun onSuccess(response: Int) {

            }

            override fun onError(error: Any?) {

            }
        })

        /*
         objectBoxApi.onUpdateBlock(block, object : onDomainApiActionListener {
             override fun onSuccess(response: Any?) {
                 postEvent(BlockResumeEvents.ON_GET_BLOCKDATA_SUCCESS, response!!)
             }

             override fun onError(error: Any?) {

             }
         })
         */
    }

    override fun getQuestionnaires() {
        db.getMyQuestionnaires(object : OnCallbackApis<List<QuestionnaireBd>> {
            override fun onSuccess(response: List<QuestionnaireBd>) {
                postEvent(BlockResumeEvents.ON_GET_QUESTIONAIRE_SUCCESS, response)
            }

            override fun onError(error: Any?) {
                postEvent(BlockResumeEvents.ON_GET_QUESTIONAIRE_ERROR, error!!)
            }
        })
    }

    override fun setApplications(apps: List<Application>) {
        db.setApplications(apps, object : OnCallbackApis<Int> {
            override fun onSuccess(response: Int) {
                Log.e("brr", "se guardaron $response")
                postEvent(BlockResumeEvents.ON_SET_APPLICATIONS_SUCCESS, response)
            }

            override fun onError(error: Any?) {
                postEvent(BlockResumeEvents.ON_SET_APPLICATIONS_ERROR, error!!)
            }
        })
    }

    override fun getDataBlock() {
        db.getBlock(firebaseApi.getUid(), object : OnCallbackApis<Block> {
            override fun onSuccess(response: Block) {
                postEvent(BlockResumeEvents.ON_GET_BLOCKDATA_SUCCESS, response)
                db.getApplicationsbyIdBlock(response.id, object : OnCallbackApis<List<Application>> {
                    override fun onSuccess(response: List<Application>) {
                        postEvent(BlockResumeEvents.ON_GET_APPLICATIONS_SUCCESS, response)
                    }

                    override fun onError(error: Any?) {
                        //postEvent(BlockResumeEvents.ON_GET_APPLICATIONS_SUCCES, response)
                    }
                })
            }

            override fun onError(error: Any?) {
                postEvent(BlockResumeEvents.ON_GET_BLOCKDATA_ERROR, error!!)
            }
        })
    }


    override fun addQuestionnaireBlock(id: Long, idBlock: Long) {

        db.addorRemoveQuestionnaireBlock(id, idBlock, object : OnCallbackApis<Unit> {
            override fun onSuccess(response: Unit) {
                Log.e("a", "se guardo")
            }

            override fun onError(error: Any?) {

            }
        })

    }

    override fun removeQuestionnaireBlock(id: Long) {

        db.addorRemoveQuestionnaireBlock(id, 0, object : OnCallbackApis<Unit> {
            override fun onSuccess(response: Unit) {
                Log.e("a", "se guardo")
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
