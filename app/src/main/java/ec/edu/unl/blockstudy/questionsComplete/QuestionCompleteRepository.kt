package ec.edu.unl.blockstudy.questionsComplete

import ec.edu.unl.blockstudy.util.Repository

/**
 * Created by victor on 5/3/18.
 */
interface QuestionCompleteRepository : Repository {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String)
}