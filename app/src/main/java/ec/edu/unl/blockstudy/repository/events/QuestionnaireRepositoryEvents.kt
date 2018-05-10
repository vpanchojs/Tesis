package ec.edu.unl.blockstudy.repository.events

/**
 * Created by victor on 5/2/18.
 */
class QuestionnaireRepositoryEvents(var type: Int, var any: Any) {

    companion object {
        val ON_GET_QUESTIONAIRE_SUCCESS = 0
        val ON_GET_QUESTIONAIRE_ERROR = 1

    }

}