package ec.edu.unl.blockstudy.newQuestionnaire

import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.newQuestionnaire.events.NewQuestionaireEvents

/**
 * Created by victor on 5/2/18.
 */
interface NewQuestionairePresenter {

    fun onResume()

    fun onPause()

    fun onUploadQuestionaire(questionaire: Questionaire)

    fun onGetQuestionaire(any: Any)

    fun onEventNewQuestionaireThread(event: NewQuestionaireEvents)
}