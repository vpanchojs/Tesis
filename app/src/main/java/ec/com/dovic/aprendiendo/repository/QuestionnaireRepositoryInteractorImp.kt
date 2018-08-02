package ec.com.dovic.aprendiendo.repository

class QuestionnaireRepositoryInteractorImp(var repository: QuestionnaireRepositoryRepository) : QuestionnaireRepositoryInteractor {

    override fun onGetQuestionnaireRepo() {
        repository.onGetQuestionnaireRepo()
    }

    override fun onGetRecomendations() {
        repository.onGetRecomendations()
    }
}
