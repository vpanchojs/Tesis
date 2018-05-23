package ec.com.dovic.aprendiendo.myquestionnaires

/**
 * Created by victor on 24/2/18.
 */
interface MyQuestionaireInteractor {
    fun onGetMyQuestionnaires()
    fun onCreateQuestionaire(title: String, description: String)
}