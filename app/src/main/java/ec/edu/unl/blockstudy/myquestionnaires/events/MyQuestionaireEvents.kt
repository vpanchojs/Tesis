package ec.edu.unl.blockstudy.myquestionnaires.events

/**
 * Created by victor on 5/2/18.
 */
class MyQuestionaireEvents(var type: Int, var any: Any) {

    companion object {
        val ON_GET_QUESTIONAIRE_SUCCESS = 0
        val ON_GET_QUESTIONAIRE_ERROR = 1
        val ON_CREATE_QUESTIONAIRE_SUCCESS = 2
        val ON_CREATE_QUESTIONAIRE_ERROR = 3
    }

}