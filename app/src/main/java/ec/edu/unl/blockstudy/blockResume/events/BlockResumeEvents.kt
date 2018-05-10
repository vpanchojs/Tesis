package ec.edu.unl.blockstudy.blockResume.events

/**
 * Created by victor on 25/2/18.
 */
class BlockResumeEvents(public var type: Int, public var any: Any) {

    companion object {
        val ON_GET_BLOCKDATA_SUCCESS = 0
        val ON_GET_BLOCKDATA_ERROR = 1
        val ON_UPDATE_BLOCKDATA_SUCCESS = 2
        val ON_GET_QUESTIONAIRE_SUCCESS = 3
        val ON_GET_QUESTIONAIRE_ERROR = 4
    }

}