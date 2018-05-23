package ec.com.dovic.aprendiendo.questionsComplete

import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.util.Repository

/**
 * Created by victor on 5/3/18.
 */
interface QuestionCompleteRepository : Repository {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String)
    fun deleteQuestionnarie(questionnaireBd: QuestionnaireBd)
}