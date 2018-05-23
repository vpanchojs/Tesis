package ec.edu.unl.blockstudy.profile

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.Academic
import ec.edu.unl.blockstudy.entities.Subject
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.profile.events.ProfileEvents

class ProfileRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, val sharePreferencesApi: SharePreferencesApi) : ProfileRepository {

    private val TAG="ProfileRepositorio"
    override fun updateInfo(user: User) {
        firebaseApi.updateUser(user, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(ProfileEvents.ON_UPDATE_USER_SUCCESS, "", user)
            }

            override fun onError(error: Any?) {
                postEvent(ProfileEvents.ON_UPDATE_USER_ERROR, error.toString(), Any())
            }
        })
    }

    override fun updateAcademic(academic: Academic) {
        firebaseApi.setAcademic(academic, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(ProfileEvents.ON_UPDATE_ACADEMIC_SUCCESS, "", academic)
            }

            override fun onError(error: Any?) {
                postEvent(ProfileEvents.ON_UPDATE_ACADEMIC_ERROR, error.toString(), Any())
            }
        })
    }

    override fun addSubject(subject: ArrayList<Subject>) {
        firebaseApi.setSubjects(subject, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(ProfileEvents.ON_SET_SUBJECTS_SUCCESS, "", subject)
            }

            override fun onError(error: Any?) {
                postEvent(ProfileEvents.ON_SET_SUBJECTS_ERROR, error.toString(), Any())
            }
        })
    }

    override fun removeSubject(subject: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updatePhotoUser(photo: String) {
        firebaseApi.updatePhoto(photo, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(ProfileEvents.ON_UPDATE_PHOTO_USER_SUCCESS, "", Any())
            }

            override fun onError(error: Any?) {
                postEvent(ProfileEvents.ON_UPDATE_PHOTO_USER_ERROR, error.toString(), Any())
            }
        })
    }

    override fun getAcademic() {
        firebaseApi.getAcademic(object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                var academics = java.util.ArrayList<Academic>()
                for (documentSnapshot in (response as QuerySnapshot).getDocuments()) {
                    Log.e("a", documentSnapshot.getData().toString())

                    val a = documentSnapshot.toObject(Academic::class.java!!)
                    a!!.id = documentSnapshot.getId()
                    academics.add(a)
                }
                postEvent(ProfileEvents.ON_GET_ACADEMIC_SUCCESS, "", academics)
            }

            override fun onError(error: Any?) {
                postEvent(ProfileEvents.ON_GET_ACADEMIC_ERROR, error.toString(), Any())
            }
        })
    }

    override fun getDataUser() {
        firebaseApi.getDataProfile(object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                var snapshot = response as DocumentSnapshot
                if (snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    Log.e(TAG, firebaseApi.mAuth.currentUser!!.email!! + "")
                    user!!.email = firebaseApi.mAuth.currentUser!!.email!!
                    user.photo = firebaseApi.mAuth.currentUser!!.photoUrl.toString()
                    postEvent(ProfileEvents.ON_GET_MY_PROFILE_SUCCESS, "", user)
                } else {
                    postEvent(ProfileEvents.ON_GET_MY_PROFILE_ERROR, "No se pudo obtener la informacion", Any())
                }
            }

            override fun onError(error: Any?) {
                postEvent(ProfileEvents.ON_GET_MY_PROFILE_ERROR, error.toString(), Any())
            }
        })
    }

    override fun getPreferences() {
        firebaseApi.getPreferencesUser(object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                var subjects = java.util.ArrayList<Subject>()
                for (documentSnapshot in (response as QuerySnapshot).getDocuments()) {
                    Log.e("S", documentSnapshot.getData().toString())
                    val a = documentSnapshot.toObject(Subject::class.java)
                    a!!.id = documentSnapshot.getId()
                    subjects.add(a)
                }
                postEvent(ProfileEvents.ON_GET_SUBJECTS_SUCCESS, "", subjects)
            }

            override fun onError(error: Any?) {
                postEvent(ProfileEvents.ON_GET_SUBJECTS_ERROR, error.toString(), Any())
            }
        })
    }

    private fun postEvent(type: Int, message: String, any: Any) {
        var event = ProfileEvents(type, any, message)
        eventBus.post(event)
    }
}