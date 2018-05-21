package ec.edu.unl.blockstudy.questionsComplete

import ec.edu.unl.blockstudy.database.QuestionnaireBd
import ec.edu.unl.blockstudy.questionsComplete.events.QuestionCompleteEvents
import ec.edu.unl.blockstudy.util.Presenter

/**
 * Created by victor on 5/3/18.
 */
interface QuestionCompletePresenter : Presenter {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String)
    fun onEventThread(event: QuestionCompleteEvents)
    fun deleteQuestionnarie(questionnaireBd: QuestionnaireBd)
}