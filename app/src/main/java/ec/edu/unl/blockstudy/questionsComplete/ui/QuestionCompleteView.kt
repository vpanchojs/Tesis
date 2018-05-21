package ec.edu.unl.blockstudy.questionsComplete.ui

import ec.edu.unl.blockstudy.database.QuestionBd
import ec.edu.unl.blockstudy.entities.Answer

/**
 * Created by victor on 5/3/18.
 */
interface QuestionCompleteView {
    fun showMessagge(message: Any)
    fun showProgressDialog(message: Any)
    fun hideProgressDialog();
    //fun setDataQuestion(questionnaire: Questionaire)
    fun setQuestions(questionList: List<QuestionBd>)

    fun none_results(show: Boolean)
    fun setAnswer(answerList: List<Answer>)
    fun closeActivity()
}