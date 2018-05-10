package ec.edu.unl.blockstudy.newQuestionnaire.ui

/**
 * Created by victor on 5/2/18.
 */
interface NewQuestionaireView {
    fun showProgressDialog(message: Any)
    fun showMessagge(message: Any)
    fun hideProgressDialog();
    fun navigationToQuestionaire();
}