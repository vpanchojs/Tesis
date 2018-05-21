package ec.edu.unl.blockstudy.questionsComplete

import ec.edu.unl.blockstudy.database.QuestionnaireBd

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