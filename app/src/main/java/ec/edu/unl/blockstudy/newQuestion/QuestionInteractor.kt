package ec.edu.unl.blockstudy.newQuestion

import ec.edu.unl.blockstudy.entities.Answer

/**
 * Created by victor on 26/2/18.
 */
interface QuestionInteractor {
    fun onCreateQuestion(url: String, anserws: ArrayList<Answer>, statement: String, idQuestionario: String)
    fun onGetDataQuestion(idQuestion: Any, idQuesitonnaire: Any)
    //fun onUpdateQuestion(question: Question)
    fun updateQuestion(idQuestion: String, statament: String, photo_url: String, answerList: java.util.ArrayList<Answer>?, idQuestionnaire: String)

    fun onDeteleQuestion(idQuestion: String, idQuestionnaire: String)

}