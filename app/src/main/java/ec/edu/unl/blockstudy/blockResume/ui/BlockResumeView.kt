package ec.edu.unl.blockstudy.blockResume.ui

import ec.edu.unl.blockstudy.entities.Block
import ec.edu.unl.blockstudy.entities.Questionaire

interface BlockResumeView {
    fun showMessagge(message: Any)
    fun setTimeActivity(time: Int)
    fun setBlockData(block: Block)
    fun setApplicationsSelect(size: Int)
    fun showProgress(show: Boolean)
    fun setQuestionnaries(questionnaire_list: List<Questionaire>)
    fun none_results(show: Boolean)
}
