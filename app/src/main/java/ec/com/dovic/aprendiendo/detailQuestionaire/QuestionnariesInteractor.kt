package ec.com.dovic.aprendiendo.detailQuestionaire

import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Questionaire

/**
 * Created by victor on 25/2/18.
 */
interface QuestionnariesInteractor {
    fun onGetDataQuestionnaire(any: Any)
    fun onSaveQuestion(idQuestionarie: Long, question: Question)
    fun updateBasicQuestionnaire(questionaire: Questionaire)
    fun onDeleteQuestionnnaire(idQuestionaire: Any)
}