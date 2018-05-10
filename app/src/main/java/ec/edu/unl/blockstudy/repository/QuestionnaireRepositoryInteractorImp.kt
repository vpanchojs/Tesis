package ec.edu.unl.blockstudy.repository

class QuestionnaireRepositoryInteractorImp(var repository: QuestionnaireRepositoryRepository) : QuestionnaireRepositoryInteractor {

    override fun onGetQuestionnaireRepo() {
        repository.onGetQuestionnaireRepo()
    }
}
