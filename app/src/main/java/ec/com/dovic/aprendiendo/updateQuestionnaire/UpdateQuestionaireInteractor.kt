package ec.com.dovic.aprendiendo.updateQuestionnaire

import ec.com.dovic.aprendiendo.entities.Questionaire

/**
 * Created by victor on 5/2/18.
 */
interface UpdateQuestionaireInteractor {
    fun onUploadQuestionaire(questionaire: Questionaire)
}