package ec.edu.unl.blockstudy.blockResume

import ec.edu.unl.blockstudy.entities.Block
import ec.edu.unl.blockstudy.entities.QuestionnaireBlock
import ec.edu.unl.blockstudy.util.Repository

interface BlockResumeRepository : Repository {
    fun setTimeActivity(block: Block)

    fun setApplications(block: List<String>, id: Long)

    fun getDataBlock()

    fun getQuestionnaires()

    fun addQuestionnaire(questionaire: QuestionnaireBlock)

    fun removeQuestionnaire(idQuestionaire: Long)
}
