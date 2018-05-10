package ec.edu.unl.blockstudy.profile.events

class ProfileEvents(var type: Int, var any: Any, var message: String) {

    companion object {
        val ON_UPDATE_USER_SUCCESS = 0
        val ON_UPDATE_USER_ERROR = 1
        val ON_UPDATE_PHOTO_USER_SUCCESS = 2
        val ON_UPDATE_PHOTO_USER_ERROR = 3
        val ON_UPDATE_ACADEMIC_SUCCESS = 4
        val ON_UPDATE_ACADEMIC_ERROR = 5
        val ON_GET_MY_PROFILE_SUCCESS = 6
        val ON_GET_MY_PROFILE_ERROR = 7
        val ON_SET_SUBJECTS_SUCCESS = 8
        val ON_SET_SUBJECTS_ERROR = 9
        val ON_GET_ACADEMIC_SUCCESS = 10
        val ON_GET_ACADEMIC_ERROR = 11
        val ON_GET_SUBJECTS_SUCCESS = 12
        val ON_GET_SUBJECTS_ERROR = 13

    }

}