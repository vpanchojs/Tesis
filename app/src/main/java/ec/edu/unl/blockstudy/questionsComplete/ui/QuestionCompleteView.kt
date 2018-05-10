package ec.edu.unl.blockstudy.questionsComplete.ui

import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question

/**
 * Created by victor on 5/3/18.
 */
interface QuestionCompleteView {
    fun showMessagge(message: Any)
    fun showProgressDialog(message: Any)
    fun hideProgressDialog();
    //fun setDataQuestion(questionnaire: Questionaire)
    fun setQuestions(questionList: List<Question>)

    fun none_results(show: Boolean)
    fun setAnswer(answerList: List<Answer>)
}