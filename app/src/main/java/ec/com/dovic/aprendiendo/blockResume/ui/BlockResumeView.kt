package ec.com.dovic.aprendiendo.blockResume.ui

import ec.com.dovic.aprendiendo.database.Application
import ec.com.dovic.aprendiendo.database.Block
import ec.com.dovic.aprendiendo.database.QuestionnaireBd

interface BlockResumeView {
    fun showMessagge(message: Any)
    fun setTimeActivity(time: Int)
    fun setBlockData(block: Block)
    fun setApplicationsSize(size: Int)
    fun setApplicationsSelect(applicationsList: List<Application>)
    fun showProgress(show: Boolean)
    fun setQuestionnaries(questionnaire_list: List<QuestionnaireBd>)
    fun none_results(visibility: Int)
    fun reloadServicie()
}
