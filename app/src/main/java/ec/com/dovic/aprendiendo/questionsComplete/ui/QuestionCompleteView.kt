package ec.com.dovic.aprendiendo.questionsComplete.ui

import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.entities.Answer

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