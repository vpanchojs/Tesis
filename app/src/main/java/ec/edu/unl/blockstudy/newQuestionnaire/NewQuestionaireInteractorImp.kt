package ec.edu.unl.blockstudy.newQuestionnaire

import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 5/2/18.
 */
class NewQuestionaireInteractorImp(var repository: NewQuestionaireRepository) : NewQuestionaireInteractor {

    override fun onUploadQuestionaire(questionaire: Questionaire) {
        repository.onUploadQuestionaire(questionaire)
    }

}