package ec.edu.unl.blockstudy.detailQuestionaire

import ec.edu.unl.blockstudy.detailQuestionaire.events.QuestionnaireEvents
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 25/2/18.
 */
interface QuestionnariesPresenter {

    fun onResume()

    fun onPause()

    fun onGetDataQuestionnaire(any: Any)

    fun onSaveQuestion(idQuestionarie: Long, question: Question)

    fun onEventThread(event: QuestionnaireEvents)

    fun updateBasicQuestionnaire(questionaire: Questionaire)

    fun onDeleteQuestionnnaire(idQuestionaire: Any)
}