package ec.edu.unl.blockstudy.detailQuestionaire.ui

import ec.edu.unl.blockstudy.entities.Question

/**
 * Created by victor on 25/2/18.
 */
interface QuestionnaireView {
    fun hideProgressDialog();
    fun showProgressDialog(message: Any)
    fun showMessagge(message: Any)
    fun showProgress(visibility: Int)
    fun none_results(show: Boolean)
    fun navigationBack()
    fun setQuestions(questionList: List<Question>)
}