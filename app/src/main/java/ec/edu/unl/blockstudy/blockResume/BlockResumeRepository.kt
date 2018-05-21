package ec.edu.unl.blockstudy.blockResume

import ec.edu.unl.blockstudy.database.Application
import ec.edu.unl.blockstudy.util.Repository

interface BlockResumeRepository : Repository {
    fun setTimeActivity(time: Int)

    fun setApplications(block: List<Application>)

    fun getDataBlock()

    fun getQuestionnaires()

    fun addQuestionnaireBlock(id: Long, idBlock: Long)

    fun removeQuestionnaireBlock(id: Long)
}
