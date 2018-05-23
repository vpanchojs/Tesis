package ec.com.dovic.aprendiendo.questionsComplete

import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.questionsComplete.events.QuestionCompleteEvents
import ec.com.dovic.aprendiendo.util.Presenter

/**
 * Created by victor on 5/3/18.
 */
interface QuestionCompletePresenter : Presenter {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String)
    fun onEventThread(event: QuestionCompleteEvents)
    fun deleteQuestionnarie(questionnaireBd: QuestionnaireBd)
}