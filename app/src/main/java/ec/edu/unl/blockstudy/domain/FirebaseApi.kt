package ec.edu.unl.blockstudy.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.WorkerThread
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import ec.edu.unl.blockstudy.domain.listeners.OnCallbackApis
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.*


/**
 * Created by victor on 23/1/18.
 */
class FirebaseApi(val db: FirebaseFirestore, var mAuth: FirebaseAuth, var storage: StorageReference) {

    private val TAG = "FirebaseApi"
    private val USERS_PATH = "users"

    private val RATING_PATH = "ratings"

    private val QUESTIONNAIRE_PATH = "questionnaires"
    private val QUESTIONS_PATH = "questions"
    private val ANSWER_PATH = "answers"
    private val STORAGE_USER_PHOTO_PATH = "user-photos"


    private val STORAGE_QUESTIONNAIRE_PHOTO = "questionnnaire-photos"

    private val SUBJECTS_PATH = "subjects"
    private val ACADEMICS_PATH = "academics"
    var mAuthListener: FirebaseAuth.AuthStateListener? = null

    private val EXECUTOR = ThreadPoolExecutor(2, 4,
            60, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>())

    fun signIn(email: String, password: String, callback: onDomainApiActionListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    callback.onSuccess(null)
                }
                .addOnFailureListener { e ->
                    callback.onError(e.toString())
                }
    }

    fun signUp(user: User, callback: onDomainApiActionListener) {
        mAuth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener {
                    saveUser(user, it.user.uid, object : onDomainApiActionListener {
                        override fun onSuccess(response: Any?) {
                            updateUser(user, object : onDomainApiActionListener {
                                override fun onSuccess(response: Any?) {
                                    callback.onSuccess(null)
                                }

                                override fun onError(error: Any?) {
                                    callback.onError(error.toString())
                                }
                            })
                        }

                        override fun onError(error: Any?) {
                            callback.onError(error)
                        }
                    })
                }
                .addOnFailureListener { e ->
                    callback.onError(e.toString())
                }
    }


    fun getResumeProfile(callback: onDomainApiActionListener) {
        if (mAuth.currentUser != null) {
            Log.e(TAG, "SI EXISTE")
            callback.onSuccess(mAuth.currentUser)
        } else {
            Log.e(TAG, "SI EXISTE")
            callback.onError("No se encuentra logeado")
        }

    }


    fun getDataUserSuscribe(callback: onDomainApiActionListener) {
        Log.e(TAG, "" + mAuth.currentUser!!.uid)
        db.collection(USERS_PATH).document(mAuth.currentUser!!.uid).get()
                .addOnSuccessListener({
                    // Log.e(TAG, it.toString())
                    callback.onSuccess(it)
                })
                .addOnFailureListener(OnFailureListener {
                    Log.e(TAG, it.toString())
                    callback.onError(it.toString())
                })

    }

    fun saveUser(user: User, id: String, callback: onDomainApiActionListener) {
        db.collection(USERS_PATH).document(id)
                .set(user.toMapPost())
                .addOnSuccessListener {
                    Log.e(TAG, "Se guardo")
                    callback.onSuccess(null)
                }
                .addOnFailureListener {
                    callback.onError("No se pudo registrar su información, intentelo nuevamente")
                }
    }


    fun suscribeAuth(callback: onDomainApiActionListener) {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.e(TAG, "EL display" + mAuth.currentUser!!.displayName.toString())
                callback.onSuccess(null)
            } else {
                callback.onError("No user is signed in.")
            }
        }
        mAuth.addAuthStateListener(mAuthListener!!)
    }


    fun unSuscribeAuth() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener!!)
        }
    }

    fun signOut() {
        mAuth.signOut()
    }

    fun updatePassword(password: String, passwordOdl: String, callback: onDomainApiActionListener) {
        val credential = EmailAuthProvider.getCredential(mAuth.currentUser!!.email!!, passwordOdl)

        mAuth.currentUser!!.reauthenticate(credential)
                .addOnSuccessListener {
                    mAuth.currentUser!!.updatePassword(password)
                            .addOnSuccessListener {
                                callback.onSuccess("Contraseña Actualizada")
                            }
                            .addOnFailureListener {
                                callback.onError(it.toString())
                            }
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun recoveryPasword(email: String, callback: onDomainApiActionListener) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    callback.onSuccess("El enlace de recuperación se envio a su cuenta de correo")
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun updateUser(user: User, callback: onDomainApiActionListener) {

        var profileUpdate: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(user.name + " " + user.lastname).build()

        mAuth.currentUser!!.updateProfile(profileUpdate)
                .addOnSuccessListener {
                    Log.e(TAG, "SE ACTUALIZO EL display" + mAuth.currentUser!!.displayName.toString())
                    callback.onSuccess(null)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.toString())
                    callback.onError(it.toString())
                }

    }

    fun updatePhoto(photo: String, callback: onDomainApiActionListener) {

        val bmp = BitmapFactory.decodeFile(photo)

        val bos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, bos)

        val data = bos.toByteArray()


        storage.child(STORAGE_USER_PHOTO_PATH).child(mAuth.currentUser!!.uid).putBytes(data)
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        val downloadUrl = taskSnapshot.downloadUrl
                        /*db.collection(USERS_PATH).document(mAuth.currentUser!!.uid).update("url_photo", downloadUrl!!.toString())
                                .addOnSuccessListener {
                                    Log.e(TAG, "foto actualizada")
                                    callback.onSuccess(null)
                                }.addOnFailureListener {
                                    Log.e(TAG, "error actualizando")
                                    callback.onError(it.toString())
                                }*/

                        var profileUpdate: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUrl).build()

                        mAuth.currentUser!!.updateProfile(profileUpdate)
                                .addOnSuccessListener {
                                    Log.e(TAG, "Se actualizo la photo")
                                    callback.onSuccess(null)
                                }
                                .addOnFailureListener {
                                    Log.e(TAG, it.toString())
                                    callback.onError(it.toString())
                                }
                    }
                })

    }

    fun setAcademic(academic: Academic, callback: onDomainApiActionListener) {
        db.collection(USERS_PATH).document(mAuth.currentUser!!.uid).collection(ACADEMICS_PATH).document()
                .set(academic.toMapPost())
                .addOnSuccessListener {
                    callback.onSuccess(null)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.toString())
                    callback.onError(it.toString())
                }
    }

    fun setSubjects(subject: ArrayList<Subject>, callback: onDomainApiActionListener) {
        deleteCollection(EXECUTOR).addOnCompleteListener {
            if (it.isSuccessful) {
                val batch = db.batch()

                var refUser = db.collection(USERS_PATH).document(mAuth.currentUser!!.uid)

                subject.forEach {
                    batch.set(refUser.collection(SUBJECTS_PATH).document(it.id), it.toMapPost())
                }

                batch.commit()
                        .addOnSuccessListener {
                            Log.e(TAG, "todo bien")
                            callback.onSuccess(null)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "malos" + e)
                            callback.onError(e.toString())
                        }
            } else {
                Log.e(TAG, "error borrando")
            }
        }


    }

    fun getDataProfile(callback: onDomainApiActionListener) {
        db.collection(USERS_PATH).document(mAuth.currentUser!!.uid).get()
                .addOnSuccessListener({
                    // Log.e(TAG, it.toString())
                    callback.onSuccess(it)
                })
                .addOnFailureListener(OnFailureListener {
                    Log.e(TAG, it.toString())
                    callback.onError(it.toString())
                })
    }

    fun getUid(): String {
        return mAuth.currentUser!!.uid
    }

    fun getNameUser(): String {
        if (mAuth.currentUser!!.displayName != null) {
            return mAuth.currentUser!!.displayName!!
        } else {
            return "Usuario"
        }

    }


    fun getPreferencesUser(callback: onDomainApiActionListener) {
        db.collection(USERS_PATH).document(getUid()).collection(SUBJECTS_PATH).get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun getAcademic(callback: onDomainApiActionListener) {
        db.collection(USERS_PATH).document(getUid()).collection(ACADEMICS_PATH).get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    /*====================*/
    private fun deleteCollection(executor: Executor): Task<Void> {

        // Perform the delete operation on the provided Executor, which allows us to use
        // simpler synchronous logic without blocking the main thread.
        return Tasks.call(executor, object : Callable<Void> {
            @Throws(Exception::class)
            override fun call(): Void? {
                // Get the first batch of documents in the collection
                //var query = collection.orderBy()
                var query = db.collection(USERS_PATH).document(getUid()).collection(SUBJECTS_PATH).orderBy(FieldPath.documentId())

                // Get a list of deleted documents
                //var deleted = deleteQueryBatch(query)
                deleteQueryBatch(query)
                return null
            }
        })

    }

    /**
     * Delete all results from a query in a single WriteBatch. Must be run on a worker thread
     * to avoid blocking/crashing the main thread.
     */
    @WorkerThread
    @Throws(Exception::class)
    private fun deleteQueryBatch(query: Query): List<DocumentSnapshot> {
        val querySnapshot = Tasks.await(query.get())

        val batch = query.getFirestore().batch()
        for (snapshot in querySnapshot) {
            batch.delete(snapshot.getReference())
        }
        Tasks.await<Void>(batch.commit())

        return querySnapshot.getDocuments()
    }

    fun onUploadQuestionaire(questionaire: Questionaire, callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).document(questionaire.idCloud)
                .update(questionaire.toMapPost())
                .addOnSuccessListener {
                    callback.onSuccess(Any())
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }


    fun uploadImageQuestion(photo: String, idQuestionnaire: String, idQuestion: String, callback: onDomainApiActionListener) {
        Log.e(TAG, "P" + photo + "a")
        if (!photo.isNullOrBlank()) {
            val bmp = BitmapFactory.decodeFile(photo)
            val bos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, bos)
            val data = bos.toByteArray()

            storage.child(STORAGE_QUESTIONNAIRE_PHOTO).child(idQuestionnaire).child(idQuestion)
                    .putBytes(data)
                    .addOnFailureListener {
                        callback.onError(it.toString())
                    }
                    .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                            val downloadUrl = taskSnapshot.downloadUrl.toString()
                            callback.onSuccess(downloadUrl)
                        }
                    })
        } else {
            callback.onSuccess("")
        }

    }

    fun createQuestionnaire(questionaire: Questionaire, callback: OnCallbackApis<Questionaire>) {
        questionaire.idUser = getUid()

        var doc = db.collection(QUESTIONNAIRE_PATH).document()
        questionaire.idCloud = doc.id
        doc.set(questionaire.toMap())
                .addOnSuccessListener {
                    callback.onSuccess(questionaire)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun getQuestions(id: String, callback: OnCallbackApis<QuerySnapshot>) {
        db.collection(QUESTIONNAIRE_PATH).document(id).collection(QUESTIONS_PATH)
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun getMyQuestionnaries(id: String, callback: OnCallbackApis<QuerySnapshot>) {
        db.collection(QUESTIONNAIRE_PATH).whereEqualTo("idUser", id)
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }

    }

    fun updateBasicInfoQuestionnnaire(questionaire: Questionaire, callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).document(questionaire.idCloud)
                .update(questionaire.toMapInfoBasic())
                .addOnSuccessListener {
                    callback.onSuccess(Any())
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }

    }

    fun onCreateQuestion(question: Question, idQuestionnaire: String, callback: onDomainApiActionListener) {

        val questionaireRef = db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire)
        val questionRef = questionaireRef.collection(QUESTIONS_PATH).document()


        uploadImageQuestion(question.photoUrl, idQuestionnaire, questionRef.id, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                question.photoUrl = response.toString()
                /*Empieza la transaccion*/
                db.runTransaction {
                    val documentSnapshot = it.get(questionaireRef)
                    var questionaire = documentSnapshot.toObject(Questionaire::class.java)
                    questionaire!!.refQuestions.add(questionRef)

                    /*Aumentamos en 1 el numero de preguntas*/
                    it.update(questionaireRef, "numberQuest", questionaire.numberQuest + 1)
                    it.update(questionaireRef, "refQuestions", questionaire.refQuestions)


                    var anwersList = ArrayList<Map<String, Any>>()

                    question.answers.forEach { a ->
                        anwersList.add(a.toMapPost())
                    }
                    question.hashAnswers = anwersList

                    /*Insertamos la pregunta*/
                    it.set(questionRef, question.toMapPost())

                }
                        .addOnSuccessListener {
                            callback.onSuccess(Any())
                        }
                        .addOnFailureListener {
                            Log.e("error", it.toString())
                            callback.onError(it.toString())
                        }
            }

            override fun onError(error: Any?) {
                Log.e(TAG, error.toString())
            }


        })


        /*
        var questionaireRef = db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire)
        var questionRef = questionaireRef.collection(QUESTIONS_PATH).document()

        uploadImageQuestion(question.photoUrl!!, idQuestionnaire, questionRef.id, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                question.photoUrl = response.toString()
                /*Empieza la transaccion*/
                db.runTransaction {
                    var documentSnapshot = it.get(questionaireRef)
                    var questionaire = documentSnapshot.toObject(Questionaire::class.java)

                    /*Aumentamos en 1 el numero de preguntas*/
                    it.update(questionaireRef, "numberQuest", questionaire.numberQuest + 1)

                    /*Insertamos la pregunta*/
                    it.set(questionRef, question.toMapPost())
                    question.answers.forEach { a ->
                        /*Insertamos las respuestas en la pregunta*/
                        it.set(questionRef.collection(ANSWER_PATH).document(), a.toMapPost())
                    }
                }
                        .addOnSuccessListener {
                            callback.onSuccess(Any())
                        }
                        .addOnFailureListener {
                            Log.e("error", it.toString())
                            callback.onError(it.toString())
                        }
            }

            override fun onError(error: Any?) {
                Log.e(TAG, error.toString())
            }


    })
*/

    }

    fun onGetAnswers(idQuestion: Any, idQuestionnaire: Any, callback: onDomainApiActionListener) {

        db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire.toString()).collection(QUESTIONS_PATH).document(idQuestion.toString()).collection(ANSWER_PATH)
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {

                }


    }

    fun updateQuestion(question: Question, callback: OnCallbackApis<Void>) {
        var anwersList = ArrayList<Map<String, Any>>()

        question.answers.forEach { a ->
            anwersList.add(a.toMapPost())
        }
        question.hashAnswers = anwersList

        db.collection(QUESTIONNAIRE_PATH).document(question.idQuestionnnaire).collection(QUESTIONS_PATH).document(question.idCloud)
                .update(question.toMapPost())
                .addOnCompleteListener {
                    callback.onSuccess(it.result)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun onDeleteQuestion(idQuestion: String, idQuestionnaire: String, callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire).collection(QUESTIONS_PATH).document(idQuestion)
                .delete()
                .addOnSuccessListener {
                    callback.onSuccess(Any())
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun deleteQuestionnnaire(idQuestionnaire: String, callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire)
                .delete()
                .addOnSuccessListener {
                    callback.onSuccess(Any())
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }


    fun getQuestionsComplete(idQuestionnaire: String, callback: onDomainApiActionListener) {
        var questionsRef = db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire).collection(QUESTIONS_PATH)
        questionsRef
                .get()
                .addOnSuccessListener {
                    var questionsList = ArrayList<Question>()
                    it.documents.forEach {
                        var question = it.toObject(Question::class.java)
                        question!!.idCloud = it.id

                        questionsRef.document(it.id).collection(ANSWER_PATH)
                                .get()
                                .addOnSuccessListener {
                                    var answersList = ArrayList<Answer>()
                                    it.documents.forEach {
                                        var answer = it.toObject(Answer::class.java)
                                        answer!!.idCloud = it.id
                                        answersList.add(answer)
                                    }
                                    question.answers = answersList

                                }
                                .addOnFailureListener {
                                    callback.onError(it.toString())
                                }

                    }
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun getQuestionnairesRepo(callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).whereEqualTo("post", true)
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun onGetUser(idUser: Any, callback: onDomainApiActionListener) {
        db.collection(USERS_PATH).document(idUser.toString())
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun onSetRaiting(raiting: Raiting, callback: onDomainApiActionListener) {
        raiting.nameUser = getNameUser()
        var newAvgRating: Double = 0.0
        /*Referencia al cuestionrio a calificar */
        var questionaireRef = db.collection(QUESTIONNAIRE_PATH).document(raiting.idQuestionaire)

        var ratingsRef: DocumentReference

        /*Referencia al nodo de calificaciones*/
        //if (raiting.idRaiting.isNullOrBlank()) {
        ratingsRef = questionaireRef.collection(RATING_PATH).document(getUid())
        //} else {
        //ratingsRef = questionaireRef.collection(RATING_PATH).document(raiting.idRaiting)
        //}


        db.runTransaction {
            var questionnaire = it.get(questionaireRef).toObject(Questionaire::class.java)


            // Compute new number of ratings
            val newNumRatings = questionnaire!!.numAssessment + 1

            // Compute new average rating
            val oldRatingTotal = questionnaire!!.assessment * questionnaire.numAssessment
            newAvgRating = (oldRatingTotal + raiting.value) / newNumRatings

            // Set new info
            questionnaire.numAssessment = newNumRatings
            questionnaire.assessment = newAvgRating

            // actualizamos el cuestionnario
            it.update(questionaireRef, questionnaire.toMapRating())

            //creamos la calificacion
            it.set(ratingsRef, raiting.toMap())

        }
                .addOnSuccessListener {
                    Log.e("R", "todo bien")
                    callback.onSuccess(newAvgRating)
                }
                .addOnFailureListener {
                    Log.e("R", it.toString())
                    callback.onError(it.toString())
                }
    }

    fun onGetRatingsAll(idQuestionnaire: Any, callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire.toString()).collection(RATING_PATH)
                .get()
                .addOnSuccessListener {
                    Log.e("aa", "bien" + it.documents.size)
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    Log.e("aa", "mal" + it.toString())
                    callback.onError(it.toString())
                }
    }

    fun getQuestionnarie(idQuestionnaire: String, callback: OnCallbackApis<DocumentSnapshot>) {

        val questionaireRef = db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire)
        //val questionsRef = db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire!!).collection(QUESTIONS_PATH)

        questionaireRef.get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                    Log.e(TAG, it.toString())
                }

        /*
        db.runTransaction(object : Transaction.Function<Questionaire> {
            override fun apply(trans: Transaction): Questionaire? {
                var questionaire: Questionaire
                questionaire = trans.get(questionaireRef).toObject(Questionaire::class.java)!!
                questionaire.idCloud = idQuestionnaire

                questionaire.refQuestions.forEach {
                    var question = trans.get(it).toObject(Question::class.java)
                    questionaire.questions.add(question!!)
                }

                return questionaire
            }
        })
                .addOnCompleteListener {

                }

                .addOnFailureListener {

                }
                */
    }

    fun getQuestionsByPath(questionPath: String, callback: OnCallbackApis<DocumentSnapshot>) {
        db.document(questionPath)
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.toString())
                    callback.onError(it.toString())
                }

    }

}