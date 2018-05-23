package ec.com.dovic.aprendiendo.detailQuestionaire

import ec.com.dovic.aprendiendo.detailQuestionaire.events.QuestionnaireEvents
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Questionaire

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