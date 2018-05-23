package ec.edu.unl.blockstudy.blockResume

import android.util.Log
import android.view.View
import ec.edu.unl.blockstudy.blockResume.events.BlockResumeEvents
import ec.edu.unl.blockstudy.blockResume.ui.BlockResumeView
import ec.edu.unl.blockstudy.database.Application
import ec.edu.unl.blockstudy.database.Block
import ec.edu.unl.blockstudy.database.QuestionnaireBd
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import org.greenrobot.eventbus.Subscribe

class BlockResumePresenterImp(var eventBus: EventBusInterface, var view: BlockResumeView, var interactor: BlockResumeInteractor) : BlockResumePresenter {


    override fun onSuscribe() {
        eventBus.register(this)
    }

    override fun onUnSuscribe() {
        eventBus.unregister(this)
    }


    override fun setTimeActivity(time: Int) {
        interactor.setTimeActivity(time)
    }

    override fun setApplications(apps: List<String>, id: Long) {
        interactor.setApplications(apps, id)
    }

    override fun getDataBlock() {
        interactor.getDataBlock()
    }

    override fun getQuestionnaires() {
        interactor.getQuestionnaires()
    }

    override fun addQuestionnaireBlock(id: Long, idBlock: Long) {
        interactor.addQuestionnaireBlock(id, idBlock)
    }

    override fun removeQuestionnaireBlock(id: Long) {
        interactor.removeQuestionnaireBlock(id)
    }

    @Subscribe
    fun onEventThread(event: BlockResumeEvents) {

        when (event.type) {
            BlockResumeEvents.ON_GET_BLOCKDATA_SUCCESS -> {
                Log.e("bien", "" + (event.any as Block).id)
                view.setBlockData(event.any as Block)
            }
            BlockResumeEvents.ON_GET_BLOCKDATA_ERROR -> {

            }
            BlockResumeEvents.ON_UPDATE_BLOCKDATA_SUCCESS -> {

            }
            BlockResumeEvents.ON_GET_QUESTIONAIRE_SUCCESS -> {
                view.showProgress(false)

                var questionnaire_list = event.any as List<QuestionnaireBd>
                if (questionnaire_list.size > 0) {
                    view.none_results(View.GONE)
                    view.setQuestionnaries(questionnaire_list)

                } else {
                    view.none_results(View.VISIBLE)
                }
            }

            BlockResumeEvents.ON_SET_APPLICATIONS_SUCCESS -> {
                view.setApplicationsSize(event.any as Int)

            }
            BlockResumeEvents.ON_GET_APPLICATIONS_SUCCESS -> {
                view.setApplicationsSelect(event.any as List<Application>)
            }

        }

    }
}


