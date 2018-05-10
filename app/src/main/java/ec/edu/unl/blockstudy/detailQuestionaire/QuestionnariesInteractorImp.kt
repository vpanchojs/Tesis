package ec.edu.unl.blockstudy.detailQuestionaire

import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 25/2/18.
 */
class QuestionnariesInteractorImp(var repository: QuestionnariesRepository) : QuestionnariesInteractor {

    override fun onGetDataQuestionnaire(any: Any) {
        repository.onGetDataQuestionnaire(any)
    }

    override fun onSaveQuestion(idQuestionarie: Long, question: Question) {
        repository.onSaveQuestion(idQuestionarie, question)
    }

    override fun updateBasicQuestionnaire(questionaire: Questionaire) {
        repository.updateBasicQuestionnaire(questionaire)
    }

    override fun onDeleteQuestionnnaire(idQuestionaire: Any) {
        repository.onDeleteQuestionnnaire(idQuestionaire)
    }
}