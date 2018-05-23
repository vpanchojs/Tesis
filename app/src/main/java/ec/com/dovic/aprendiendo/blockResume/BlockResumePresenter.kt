package ec.com.dovic.aprendiendo.blockResume

import ec.com.dovic.aprendiendo.util.Presenter

interface BlockResumePresenter : Presenter {

    fun setTimeActivity(time: Int)

    fun setApplications(apps: List<String>, id: Long)

    fun getDataBlock()

    fun getQuestionnaires()

    fun addQuestionnaireBlock(id: Long, idBlock: Long)

    fun removeQuestionnaireBlock(id: Long)


}
