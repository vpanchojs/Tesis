package ec.edu.unl.blockstudy.myrepository

import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 24/2/18.
 */
interface MyRepositoryRepository {
    fun onGetmyrepository()
    fun onCreateQuestionaire(questionaire: Questionaire)
}