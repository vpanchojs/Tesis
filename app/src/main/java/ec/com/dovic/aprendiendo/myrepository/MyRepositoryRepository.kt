package ec.com.dovic.aprendiendo.myrepository

import ec.com.dovic.aprendiendo.entities.Questionaire

/**
 * Created by victor on 24/2/18.
 */
interface MyRepositoryRepository {
    fun onGetmyrepository()
    fun onCreateQuestionaire(questionaire: Questionaire)
}