package ec.edu.unl.blockstudy.questionnaireResume

import ec.edu.unl.blockstudy.entities.Raiting


class QuestionnaireResumeInteractorImp(var repository: QuestionnaireResumeRepository) : QuestionnaireResumeInteractor {

    override fun getQuestionnaire(idQuestionnaire: String) {

    }

    override fun onGetQuestionAll(idQuestionnaire: Any) {
        repository.onGetQuestionAll(idQuestionnaire)
    }

    override fun onGetRaitingsAll(idQuestionnaire: Any) {
        repository.onGetRaitingsAll(idQuestionnaire)
    }

    override fun setRaiting(idQuestionnaire: Any, value: Double, message: String) {
        var rating = Raiting()
        rating.comment = message
        rating.value = value
        rating.idQuestionaire = idQuestionnaire.toString()
        repository.setRaiting(rating)

    }

    override fun onGetUser(idUser: Any) {
        repository.onGetUser(idUser)
    }
}
