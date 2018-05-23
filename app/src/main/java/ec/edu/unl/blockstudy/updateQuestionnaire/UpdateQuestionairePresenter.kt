package ec.edu.unl.blockstudy.updateQuestionnaire

import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.updateQuestionnaire.events.UpdateQuestionaireEvents

/**
 * Created by victor on 5/2/18.
 */
interface UpdateQuestionairePresenter {

    fun onResume()

    fun onPause()

    fun onUploadQuestionaire(questionaire: Questionaire)

    fun onGetQuestionaire(any: Any)

    fun onEventNewQuestionaireThread(event: UpdateQuestionaireEvents)
}