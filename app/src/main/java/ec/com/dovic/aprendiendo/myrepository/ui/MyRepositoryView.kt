package ec.com.dovic.aprendiendo.myrepository.ui

import ec.com.dovic.aprendiendo.entities.Questionaire

/**
 * Created by victor on 24/2/18.
 */
interface MyRepositoryView {
    fun hideProgressDialog();
    fun showProgressDialog(message: Any)
    fun showProgress(show: Boolean)
    fun showMessagge(message: Any)
    fun setQuestionnaries(questionaire: List<Questionaire>)
    fun none_results(show: Boolean)
    fun navigationManageQuestionnaire(questionaire: Questionaire)
    fun hideDialogNewQuestionnaire()
    fun showButtonCreateQuestionnaire()
    fun showSnackbar(message: String)
    fun clearResults()
}