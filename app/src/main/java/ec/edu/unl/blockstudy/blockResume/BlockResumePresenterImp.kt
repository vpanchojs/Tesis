package ec.edu.unl.blockstudy.blockResume

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import ec.edu.unl.blockstudy.blockResume.events.BlockResumeEvents
import ec.edu.unl.blockstudy.blockResume.ui.BlockResumeView
import ec.edu.unl.blockstudy.entities.Block
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.entities.objectBox.QuestionnaireBd
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import org.greenrobot.eventbus.Subscribe

class BlockResumePresenterImp(var eventBus: EventBusInterface, var view: BlockResumeView, var interactor: BlockResumeInteractor) : BlockResumePresenter {


    override fun onSuscribe() {
        eventBus.register(this)
    }

    override fun onUnSuscribe() {
        eventBus.unregister(this)
    }


    override fun setTimeActivity(block: Block) {
        interactor.setTimeActivity(block)
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

    override fun addQuestionnaire(idQuestionaire: Long, idCloud: String, idBlock: Long, refQuestions: ArrayList<DocumentReference>) {
        interactor.addQuestionnaire(idQuestionaire, idCloud, idBlock, refQuestions)
    }

    override fun removeQuestionnaire(idQuestionaire: Long) {
        interactor.removeQuestionnaire(idQuestionaire)
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
//                view.setQuestionnaries(event.any as Questionaire)


                var questionnaire_list = event.any as List<QuestionnaireBd>
                if (questionnaire_list.size > 0)
                    view.setQuestionnaries(questionnaire_list)
                else
                    view.none_results(true)
            }

        }

    }
}


