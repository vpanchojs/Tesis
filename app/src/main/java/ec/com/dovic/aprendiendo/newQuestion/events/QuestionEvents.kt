package ec.com.dovic.aprendiendo.newQuestion.events

/**
 * Created by victor on 26/2/18.
 */
class QuestionEvents(var type: Int, var any: Any) {

    companion object {
        val ON_CREATE_QUESTION_SUCCESS = 0
        val ON_CREATE_QUESTION_ERROR = 1
        val ON_GET_ANSWERS_SUCCESS = 2
        val ON_GET_ANSWERS_ERROR = 3
        val ON_DELETE_QUESTION_SUCCESS = 4
        val ON_UPDATE_QUESTION_SUCCESS = 5
    }
}