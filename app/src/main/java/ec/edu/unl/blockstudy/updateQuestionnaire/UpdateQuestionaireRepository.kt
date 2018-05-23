package ec.edu.unl.blockstudy.updateQuestionnaire

import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 5/2/18.
 */
interface UpdateQuestionaireRepository {
    fun onUploadQuestionaire(questionaire: Questionaire)
    fun onGetQuestionaire(any: Any)
}