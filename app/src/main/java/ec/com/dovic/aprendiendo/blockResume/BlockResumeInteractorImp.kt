package ec.com.dovic.aprendiendo.blockResume

import ec.com.dovic.aprendiendo.database.Application

class BlockResumeInteractorImp(var repository: BlockResumeRepository) : BlockResumeInteractor {

    override fun setTimeActivity(time: Int) {
        repository.setTimeActivity(time)
    }

    override fun setApplications(apps: List<String>, id: Long) {
        var applicationsList = arrayListOf<Application>()

        apps.forEach {
            applicationsList.add(Application(packagename = it, blockId = id))
        }

        repository.setApplications(applicationsList)
    }

    override fun getDataBlock() {
        repository.getDataBlock()
    }

    override fun getQuestionnaires() {
        repository.getQuestionnaires()
    }


    override fun addQuestionnaireBlock(id: Long, idBlock: Long) {
        repository.addQuestionnaireBlock(id, idBlock)
    }

    override fun removeQuestionnaireBlock(id: Long) {
        repository.removeQuestionnaireBlock(id)
    }


}
