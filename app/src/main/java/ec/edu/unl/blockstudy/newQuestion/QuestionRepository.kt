package ec.edu.unl.blockstudy.newQuestion

import ec.edu.unl.blockstudy.entities.Question

/**
 * Created by victor on 26/2/18.
 */
interface QuestionRepository {
    fun onCreateQuestion(question: Question, idQuestionnaire: String)
    fun onGetDataQuestion(idQuestion: Any, idQuesitonnaire: Any)
    fun onUpdateQuestion(question: Question)
    fun onDeteleQuestion(idQuestion: String, idQuestionnaire: String)
}