package ec.edu.unl.blockstudy.newQuestion

import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question

/**
 * Created by victor on 26/2/18.
 */
class QuestionInteractorImp(var repository: QuestionRepository) : QuestionInteractor {

    override fun onCreateQuestion(url: String, anserws: ArrayList<Answer>, statement: String, idQuestionnaire: String) {
        var question = Question()
        question.statement = statement
        question.answers = anserws
        question.photoUrl = url
        repository.onCreateQuestion(question, idQuestionnaire)
    }

    override fun onGetDataQuestion(idQuestion: Any, idQuesitonnaire: Any) {
        repository.onGetDataQuestion(idQuestion, idQuesitonnaire)
    }

    override fun updateQuestion(idQuestion: String, statament: String, photo_url: String, answerList: java.util.ArrayList<Answer>?, idQuestionnaire: String) {
        var question = Question()
        question.idQuestionnnaire = idQuestionnaire
        question.idCloud = idQuestion
        question.answers = answerList!!
        question.photoUrl = photo_url
        question.statement = statament
        repository.onUpdateQuestion(question)
    }

    override fun onDeteleQuestion(idQuestion: String, idQuestionnaire: String) {
        repository.onDeteleQuestion(idQuestion, idQuestionnaire)
    }
}