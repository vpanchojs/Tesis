package ec.com.dovic.aprendiendo.blockResume

import ec.com.dovic.aprendiendo.database.Application
import ec.com.dovic.aprendiendo.util.Repository

interface BlockResumeRepository : Repository {
    fun setTimeActivity(time: Int)

    fun setApplications(block: List<Application>)

    fun getDataBlock()

    fun getQuestionnaires()

    fun addQuestionnaireBlock(id: Long, idBlock: Long)

    fun removeQuestionnaireBlock(id: Long)
}
