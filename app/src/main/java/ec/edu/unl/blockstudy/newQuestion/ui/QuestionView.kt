package ec.edu.unl.blockstudy.newQuestion.ui

import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question

/**
 * Created by victor on 26/2/18.
 */
interface QuestionView {
    fun showMessagge(message: Any)
    fun showProgressDialog(message: Any)
    fun hideProgressDialog();
    fun setDataQuestion(question: Question)
    fun setNavigationQuestionnnaire()
    fun setDataAnswers(anserws: List<Answer>)
}