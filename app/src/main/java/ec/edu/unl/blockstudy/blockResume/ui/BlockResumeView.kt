package ec.edu.unl.blockstudy.blockResume.ui

import ec.edu.unl.blockstudy.database.Application
import ec.edu.unl.blockstudy.database.Block
import ec.edu.unl.blockstudy.database.QuestionnaireBd

interface BlockResumeView {
    fun showMessagge(message: Any)
    fun setTimeActivity(time: Int)
    fun setBlockData(block: Block)
    fun setApplicationsSize(size: Int)
    fun setApplicationsSelect(applicationsList: List<Application>)
    fun showProgress(show: Boolean)
    fun setQuestionnaries(questionnaire_list: List<QuestionnaireBd>)
    fun none_results(show: Boolean)
}
