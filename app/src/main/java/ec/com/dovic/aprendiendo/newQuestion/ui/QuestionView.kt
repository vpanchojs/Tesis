package ec.com.dovic.aprendiendo.newQuestion.ui

import ec.com.dovic.aprendiendo.entities.Answer
import ec.com.dovic.aprendiendo.entities.Question

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