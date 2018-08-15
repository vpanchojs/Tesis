package ec.com.dovic.aprendiendo.questionnaireResume

import ec.com.dovic.aprendiendo.entities.Raiting


class QuestionnaireResumeInteractorImp(var repository: QuestionnaireResumeRepository) : QuestionnaireResumeInteractor {


    override fun isExistQuestionnnaireLocal(idCloud: String) {
        repository.isExistQuestionnnaireLocal(idCloud)
    }

    override fun isDownloaded(idQuestionnaire: String) {
        repository.isDownloaded(idQuestionnaire)
    }

    override fun getQuestionnaire(idQuestionnaire: String) {
        repository.getQuestionnaire(idQuestionnaire)
    }

    override fun onGetQuestionAll(idQuestionnaire: Any) {
        repository.onGetQuestionAll(idQuestionnaire)
    }

    override fun onGetRaitingsAll(idQuestionnaire: Any) {
        repository.onGetRaitingsAll(idQuestionnaire)
    }

    override fun setRaiting(idQuestionnaire: Any, value: Double, message: String, update: Boolean, oldRaiting: Double) {
        var rating = Raiting()
        rating.comment = message
        rating.value = value
        rating.idQuestionaire = idQuestionnaire.toString()
        repository.setRaiting(rating, update, oldRaiting)

    }

    override fun onGetUser(idUser: Any) {
        repository.onGetUser(idUser)
    }
}
