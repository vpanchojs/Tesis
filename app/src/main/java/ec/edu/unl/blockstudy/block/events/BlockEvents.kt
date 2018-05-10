package ec.edu.unl.blockstudy.block.events

/**
 * Created by victor on 25/2/18.
 */
class BlockEvents(var type: Int, var any: Any) {

    companion object {
        val ON_GET_QUESTIONS_SUCCESS = 0
        val ON_GET_QUESTIONS_ERROR = 1
    }

}