package ec.com.dovic.aprendiendo.questionsComplete

import ec.com.dovic.aprendiendo.database.QuestionnaireBd

/**
 * Created by victor on 5/3/18.
 */
class QuestionCompleteInteractorImp(var repository: QuestionCompleteRepository) : QuestionCompleteInteractor {

    override fun onGetQuestionAll(idQuestionnaire: Any) {
        repository.onGetQuestionAll(idQuestionnaire)
    }

    override fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String) {
        repository.onGetAnswersQuestion(idQuestionnaire, idCloud)
    }

    override fun deleteQuestionnarie(questionnaireBd: QuestionnaireBd) {
        repository.deleteQuestionnarie(questionnaireBd)
    }
}