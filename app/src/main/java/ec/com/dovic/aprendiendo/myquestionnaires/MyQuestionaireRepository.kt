package ec.com.dovic.aprendiendo.myquestionnaires

import ec.com.dovic.aprendiendo.entities.Questionaire

/**
 * Created by victor on 24/2/18.
 */
interface MyQuestionaireRepository {
    fun onGetMyQuestionnaires()
    fun onCreateQuestionaire(questionaire: Questionaire)
}