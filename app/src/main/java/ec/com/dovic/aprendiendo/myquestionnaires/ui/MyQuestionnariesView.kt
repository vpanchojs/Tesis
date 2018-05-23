package ec.com.dovic.aprendiendo.myquestionnaires.ui

import ec.com.dovic.aprendiendo.database.QuestionnaireBd

/**
 * Created by victor on 24/2/18.
 */
interface MyQuestionnariesView {
    fun hideProgressDialog();
    fun showProgressDialog(message: Any)
    fun showProgress(show: Boolean)
    fun showMessagge(message: Any)
    fun setQuestionnaries(questionaire: List<QuestionnaireBd>)
    fun none_results(show: Boolean)
    fun navigationManageQuestionnaire(questionaire: QuestionnaireBd)
    fun hideDialogNewQuestionnaire()
    fun showButtonCreateQuestionnaire()
}