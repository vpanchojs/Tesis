package ec.com.dovic.aprendiendo.updateQuestionnaire.ui

/**
 * Created by victor on 5/2/18.
 */
interface UpdateQuestionaireView {
    fun showProgressDialog(message: Any)
    fun showMessagge(message: Any)
    fun hideProgressDialog();
    fun navigationToQuestionaire();
}