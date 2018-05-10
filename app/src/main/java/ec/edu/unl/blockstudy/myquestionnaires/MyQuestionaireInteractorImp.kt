package ec.edu.unl.blockstudy.myquestionnaires

import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 24/2/18.
 */
class MyQuestionaireInteractorImp(var repository: MyQuestionaireRepository) : MyQuestionaireInteractor {
    override fun onGetMyQuestionnaires() {
        repository.onGetMyQuestionnaires()
    }

    override fun onCreateQuestionaire(title: String, description: String) {
        var q = Questionaire()
        q.title = title
        q.description = description
        repository.onCreateQuestionaire(q)
    }
}