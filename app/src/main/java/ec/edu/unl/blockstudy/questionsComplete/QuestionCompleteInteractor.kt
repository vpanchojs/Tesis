package ec.edu.unl.blockstudy.questionsComplete

/**
 * Created by victor on 5/3/18.
 */
interface QuestionCompleteInteractor {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String)
}