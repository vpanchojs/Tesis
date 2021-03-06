package ec.com.dovic.aprendiendo.updateQuestionnaire

import ec.com.dovic.aprendiendo.entities.Questionaire

/**
 * Created by victor on 5/2/18.
 */
class UpdateQuestionaireInteractorImp(var repository: UpdateQuestionaireRepository) : UpdateQuestionaireInteractor {

    override fun onUploadQuestionaire(questionaire: Questionaire) {
        repository.onUploadQuestionaire(questionaire)
    }

}