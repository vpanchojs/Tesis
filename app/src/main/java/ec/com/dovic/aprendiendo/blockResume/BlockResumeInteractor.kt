package ec.com.dovic.aprendiendo.blockResume

interface BlockResumeInteractor {
    fun setTimeActivity(time: Int)

    fun setApplications(apps: List<String>, id: Long)

    fun getDataBlock()

    fun getQuestionnaires()

    fun addQuestionnaireBlock(id: Long, idBlock: Long)

    fun removeQuestionnaireBlock(id: Long)
}
