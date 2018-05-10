package ec.edu.unl.blockstudy.myquestionnaires.ui

import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 24/2/18.
 */
interface MyQuestionnariesView {
    fun hideProgressDialog();
    fun showProgressDialog(message: Any)
    fun showProgress(show: Boolean)
    fun showMessagge(message: Any)
    fun setQuestionnaries(questionaire: List<Questionaire>)
    fun none_results(show: Boolean)
    fun navigationManageQuestionnaire(questionaire: Questionaire)
    fun hideDialogNewQuestionnaire()
    fun showButtonCreateQuestionnaire()
}