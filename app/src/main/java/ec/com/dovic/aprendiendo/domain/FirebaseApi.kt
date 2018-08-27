package ec.com.dovic.aprendiendo.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.WorkerThread
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.*
import com.google.firebase.firestore.*
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.entities.*
import java.io.ByteArrayOutputStream
import java.util.HashMap
import java.util.concurrent.*
import kotlin.collections.ArrayList


/**
 * Created by victor on 23/1/18.
 */

class FirebaseApi(val db: FirebaseFirestore, val mAuth: FirebaseAuth, val storage: StorageReference, val functions: FirebaseFunctions) {

    private val TAG = "FirebaseApi"
    private val USERS_PATH = "users"

    private val RATING_PATH = "ratings"
    private val DOWNLOAD_PATH = "download"

    private val QUESTIONNAIRE_PATH = "questionnaires"
    private val QUESTIONS_PATH = "questions"
    private val ANSWER_PATH = "answers"
    private val STORAGE_USER_PHOTO_PATH = "user-photos"
    private val RECOMMENDATION_PATH = "recommendations"


    private val STORAGE_QUESTIONNAIRE_PHOTO = "questionnnaire-photos"

    private val SUBJECTS_PATH = "subjects"
    private val ACADEMICS_PATH = "academics"
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    val PATH_DOWNLOAD_QUESTIONNAIRE = "download_questionnaire"
    val gson = Gson()


    private val EXElllCUTOR = ThreadPoolExecutor(2, 4,
            60, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>())

    fun autenticationGoogle(idToken: String, user: User, callback: OnCallbackApis<User>) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener {

                    if (it.additionalUserInfo.isNewUser) {
                        user.pk = it.user.uid

                        saveUser(user, object : OnCallbackApis<Unit> {

                            override fun onSuccess(response: Unit) {
                                callback.onSuccess(user)
                            }

                            override fun onError(error: Any?) {
                                callback.onError(error)
                            }
                        })

                    } else {
                        callback.onSuccess(user)
                    }
                }
                .addOnFailureListener {
                    callback.onError(ManageErrorFirebaseApi.getMessageErrorFirebaseAuth(it))
                }
    }

    fun autenticationFacebook(accesToken: String, user: User, callback: OnCallbackApis<User>) {
        val credential = FacebookAuthProvider.getCredential(accesToken);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener {

                    if (it.additionalUserInfo.isNewUser) {
                        user.pk = it.user.uid
                        saveUser(user, object : OnCallbackApis<Unit> {
                            override fun onSuccess(response: Unit) {
                                callback.onSuccess(user)
                            }

                            override fun onError(error: Any?) {
                                callback.onError(error)
                            }
                        })
                    } else {
                        callback.onSuccess(user)
                    }

                }.addOnFailureListener {
                    callback.onError(ManageErrorFirebaseApi.getMessageErrorFirebaseAuth(it))
                }
    }

    fun signIn(email: String, password: String, callback: OnCallbackApis<Boolean>) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    if (mAuth.currentUser!!.isEmailVerified) {
                        Log.e(TAG, "si esta verificado")
                        callback.onSuccess(true)
                    } else {
                        callback.onSuccess(false)
                    }
                }
                .addOnFailureListener { e ->
                    callback.onError(e.message)
                }
    }

    fun signUp(user: User, callback: onDomainApiActionListener) {
        mAuth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener {
                    /*
                    saveUser(user, it.user.uid, object : onDomainApiActionListener {
                        override fun onSuccess(response: Any?) {
                            updateUser(user, object : onDomainApiActionListener {
                                override fun onSuccess(response: Any?) {
                                    mAuth.currentUser!!.sendEmailVerification().addOnSuccessListener {
                                        callback.onSuccess(null)
                                    }
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
                    */

                }
                .addOnFailureListener { e ->
                    callback.onError(ManageErrorFirebaseApi.getMessageErrorFirebaseAuth(e))
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
                .addOnSuccessListener {
                    // Log.e(TAG, it.toString())
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.toString())
                    callback.onError(it.toString())
                }

    }

    fun saveUser(user: User, callback: OnCallbackApis<Unit>) {
        db.collection(USERS_PATH).document(user.pk).set(user.toMapPostSave()).addOnSuccessListener {
            callback.onSuccess(Unit)
        }.addOnFailureListener {
            callback.onError(ManageErrorFirebaseApi.getMessageErrorFirebaseFirestore(it))
        }
    }


    fun suscribeAuth(callback: onDomainApiActionListener) {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.e(TAG, "EL display" + mAuth.currentUser!!.displayName.toString())
                if (user.isEmailVerified) {
                    callback.onSuccess(null)
                } else {
                    callback.onError("Debe validar su correo")
                }
            } else {
                callback.onError("")
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
                    callback.onError(it.message)
                }
    }

    fun recoveryPasword(email: String, callback: onDomainApiActionListener) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    callback.onSuccess("El enlace de recuperación se envio a su cuenta de correo")
                }
                .addOnFailureListener {
                    callback.onError(it.message)
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
                    callback.onError(it.message)
                }

    }

    fun updatePhoto(photo: String, callback: onDomainApiActionListener) {

        val bmp = BitmapFactory.decodeFile(photo)

        val bos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, bos)

        val data = bos.toByteArray()


        storage.child(STORAGE_USER_PHOTO_PATH).child(mAuth.currentUser!!.uid).putBytes(data)
                .addOnFailureListener {
                    callback.onError(it.message)
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
                                    callback.onSuccess(downloadUrl)
                                }
                                .addOnFailureListener {
                                    Log.e(TAG, it.toString())
                                    callback.onError(it.message)
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
                    callback.onError(it.message)
                }
    }

    fun setSubjects(subject: ArrayList<Subject>, callback: onDomainApiActionListener) {
        /*
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
                            callback.onError(e.message)
                        }
            } else {
                Log.e(TAG, "error borrando")
            }
        }
*/

    }

    fun getDataProfile(callback: onDomainApiActionListener) {
        db.collection(USERS_PATH).document(mAuth.currentUser!!.uid).get()
                .addOnSuccessListener({
                    // Log.e(TAG, it.toString())
                    callback.onSuccess(it)
                })
                .addOnFailureListener(OnFailureListener {
                    Log.e(TAG, it.toString())
                    callback.onError(it.message)
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
        doc.set(questionaire.toMapAux())
                .addOnSuccessListener {
                    callback.onSuccess(questionaire)
                }
                .addOnFailureListener {
                    callback.onError(it.message)
                }
    }

    fun getQuestions(id: String, callback: OnCallbackApis<QuerySnapshot>) {
        db.collection(QUESTIONNAIRE_PATH).document(id).collection(QUESTIONS_PATH)
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.message)
                }
    }

    fun getMyQuestionnaries(id: String, callback: OnCallbackApis<QuerySnapshot>) {
        db.collection(QUESTIONNAIRE_PATH).whereEqualTo("idUser", id).orderBy("title")
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    Log.e(TAG, "error ${it.toString()}")
                    callback.onError(it.message)
                }

    }

    fun updateBasicInfoQuestionnnaire(questionaire: Questionaire, callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).document(questionaire.idCloud)
                .update(questionaire.toMapInfoBasic())
                .addOnSuccessListener {
                    callback.onSuccess(Any())
                }
                .addOnFailureListener {
                    callback.onError(it.message)
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
                            callback.onError(it.message)
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

    fun updateQuestion(question: Question, callback: OnCallbackApis<Unit>) {
        var anwersList = ArrayList<Map<String, Any>>()

        question.answers.forEach { a ->
            anwersList.add(a.toMapPost())
        }
        question.hashAnswers = anwersList

        db.collection(QUESTIONNAIRE_PATH).document(question.idQuestionnnaire).collection(QUESTIONS_PATH).document(question.idCloud)
                .update(question.toMapPost())
                .addOnCompleteListener {
                    callback.onSuccess(Unit)
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
                    callback.onError(it.message)
                }
    }

    fun deleteQuestionnnaire(idQuestionnaire: String, callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire)
                .delete()
                .addOnSuccessListener {
                    callback.onSuccess(Any())
                }
                .addOnFailureListener {
                    callback.onError(it.message)
                }
    }


    fun getQuestionsComplete(idQuestionnaire: String, callback: OnCallbackApis<DocumentSnapshot>) {
        db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire).get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }
    }

    fun getQuestionnairesRepo(callback: onDomainApiActionListener) {
        db.collection(QUESTIONNAIRE_PATH).whereEqualTo("post", true).orderBy("title")
                .get()
                .addOnSuccessListener {
                    Log.e(TAG, "succes" + it.toString())
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    Log.e(TAG, "error $it" )
                    callback.onError(it.message)
                }
    }

    fun onGetUser(idUser: Any, callback: onDomainApiActionListener) {
        db.collection(USERS_PATH).document(idUser.toString())
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.message)
                }
    }

    fun onSetRaiting(raiting: Raiting, update: Boolean, oldRaiting: Double, callback: onDomainApiActionListener) {

        raiting.nameUser = getNameUser()
        raiting.me = true

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
            val newNumRatings = if (update) questionnaire!!.numAssessment else questionnaire!!.numAssessment + 1

            var aux = questionnaire!!.assessment
            if (update) {
                aux = questionnaire!!.assessment - oldRaiting
            }

            // Compute new average rating
            val oldRatingTotal = aux * questionnaire.numAssessment
            newAvgRating = (oldRatingTotal + raiting.value) / newNumRatings

            // Set new info
            questionnaire.numAssessment = newNumRatings
            questionnaire.assessment = newAvgRating

            raiting.idRaiting = questionaireRef.id

            // actualizamos el cuestionnario
            it.update(questionaireRef, questionnaire.toMapRating())

            //creamos la calificacion
            it.set(ratingsRef, raiting.toMap())

        }
                .addOnSuccessListener {
                    Log.e("R", "todo bien" + newAvgRating)
                    callback.onSuccess(raiting)
                }
                .addOnFailureListener {
                    Log.e("R", it.toString())
                    callback.onError(it.message)
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
                    callback.onError(it.message)
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
                    callback.onError(it.message)
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
                    callback.onError(it.message)
                }

    }

    fun getQuestionnaireComplete(idQuestionnaire: String, update: Boolean, callback: OnCallbackApis<QuestionnaireBd>) {
        val parametros = HashMap<String, Any>()
        parametros.put("id", idQuestionnaire)
        parametros.put("update", update)
        parametros.put("id_user", getUid())
        functions.getHttpsCallable(PATH_DOWNLOAD_QUESTIONNAIRE).call(parametros)
                .addOnSuccessListener {
                    Log.e("bien", it.data.toString())
                    val questionnaireBd = gson.fromJson(gson.toJson(it.data), QuestionnaireBd::class.java)
                    callback.onSuccess(questionnaireBd)
                }

                .addOnFailureListener {
                    Log.e("error", it.toString())

                }
    }

    fun sendEmailVerify() {
        mAuth.currentUser?.sendEmailVerification()
    }

    fun isDownLoadedQuestionnaire(idQuestionnaire: String, callbackApis: OnCallbackApis<Boolean>) {
        db.collection(USERS_PATH).document(getUid()).collection(DOWNLOAD_PATH).document(idQuestionnaire).get()
                .addOnSuccessListener {
                    callbackApis.onSuccess(it.exists())
                }
                .addOnFailureListener {

                }
    }


    /*******************LISTA DE CUESTIONARIOS*******************/


    /*DESARROLLO MOVIL 10*/

    fun cuestionario_dm1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Conociendo Android", "Aprendiendo sobre la plataforma android", "DESARROLLO MOVIL", 5, "android, plataforma, básico")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Google", true))
        answers1.add(crearRespuesta("Microsoft", false))
        answers1.add(crearRespuesta("Apple", false))


        questionList.add(crearPregunta("", answers1, "¿Android es una plataforma propiedad de?"))
        //val question1 = crearPregunta("", answers1, "¿Android es una plataforma propiedad de?")

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "¿Android es una plataforma open source?"))
        //val question2 = crearPregunta("", answers2, "¿Android es una plataforma open source?")

        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Kotlin", true))
        answers3.add(crearRespuesta("Java", true))
        answers3.add(crearRespuesta("Go", false))
        answers3.add(crearRespuesta("Dart", false))
        answers3.add(crearRespuesta("Python", false))


        questionList.add(crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android."))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("2003", false))
        answers4.add(crearRespuesta("2010", false))
        answers4.add(crearRespuesta("2008", true))


        questionList.add(crearPregunta("", answers4, "En qué año fue lanzado oficialmente Android por Google."))
        //val question4 = crearPregunta("", answers4, "En qué año fue lanzado oficialmente Android por Google.")

        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Postres o dulces.", false))
        answers5.add(crearRespuesta("Vegetales", false))
        answers5.add(crearRespuesta("Frutas", true))


        questionList.add(crearPregunta("", answers5, "Las diferentes versiones reciben nombres referentes a"))
        //val question5 = crearPregunta("", answers5, "Las diferentes versiones reciben nombres referentes a")

        questionnaire.questions = questionList
        return questionnaire

        /*
        val batch = db.batch()
        val CuesRef = db.collection(QUESTIONNAIRE_PATH).document()
        val PreRef1 = db.collection(QUESTIONNAIRE_PATH).document(CuesRef.id).collection(QUESTIONS_PATH).document()

        batch.set(CuesRef, questionnaire.toMap())
        batch.set(PreRef1, question1.toMapPost())


        batch.commit().addOnSuccessListener {

        }.addOnFailureListener {

        }
        */

    }

    fun cuestionario_dm2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Android Básico.", "Conceptos básicos sobre la plataforma Android.", "DESARROLLO MOVIL", 5, "básico,android,conceptos")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Android Studio.", true))
        answers1.add(crearRespuesta("Eclipse", false))
        answers1.add(crearRespuesta("Visual Studio.", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android."))

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("AndroidManifest.xml", true))
        answers2.add(crearRespuesta("Manifest.xml", false))
        answers2.add(crearRespuesta("AndroidManifest.json", false))

        questionList.add(crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Kotlin", true))
        answers3.add(crearRespuesta("Java", true))
        answers3.add(crearRespuesta("Go", false))
        answers3.add(crearRespuesta("Dart", false))
        answers3.add(crearRespuesta("Python", false))


        questionList.add(crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android."))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Maven", false))
        answers4.add(crearRespuesta("Yarn", false))
        answers4.add(crearRespuesta("Gradle", true))


        questionList.add(crearPregunta("", answers4, "Seleccione el gestor de librerías que utiliza android."))
        //val question4 = crearPregunta("", answers4, "Seleccione el gestor de librerías que utiliza android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("drawable.", true))
        answers5.add(crearRespuesta("icon.", false))
        answers5.add(crearRespuesta("mipmap.", true))
        answers5.add(crearRespuesta("layout.", true))
        answers5.add(crearRespuesta("service.", false))

        questionList.add(crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android."))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionario_dm3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Android Studio", "Conceptos básicos sobre el IDE Android Studio.", "DESARROLLO MOVIL", 3, "android studio, conceptos")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Google.", false))
        answers1.add(crearRespuesta("JetBrains.", true))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Android Studio es propiedad de"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Multiplataforma.", true))
        answers2.add(crearRespuesta("Nativas.", false))

        questionList.add(crearPregunta("", answers2, "Con Android Studio se puede desarrollador aplicaciones"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Depuración de aplicaciones.", true))
        answers3.add(crearRespuesta("Subir aplicaciones a  Play Store", false))
        answers3.add(crearRespuesta("Firmar aplicaciones para producción.", true))


        questionList.add(crearPregunta("", answers3, "Seleccione las funciones que brinda Android Studio"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionario_dm4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Recursos Android.", "Identificación de los recursos utilizados por Android.", "DESARROLLO MOVIL", 4, "recursos, android")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Imágenes", true))
        answers1.add(crearRespuesta("Iconos", true))
        answers1.add(crearRespuesta("Logo de la aplicación.", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "El directorio drawable, sirve para almacenar."))

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Actividades.", false))
        answers2.add(crearRespuesta("Layouts o vistas.", true))
        answers2.add(crearRespuesta("Imágenes", false))

        questionList.add(crearPregunta("", answers2, "El directorios layout, sirve para almacenar."))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Logo de la aplicación.", true))
        answers3.add(crearRespuesta("HashMap", false))
        answers3.add(crearRespuesta("Imágenes", false))


        questionList.add(crearPregunta("", answers3, "El directorio mipmap, sirve para almacenar."))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Estilos de la aplicación.", true))
        answers4.add(crearRespuesta("Colores", true))
        answers4.add(crearRespuesta("Strings", true))
        answers4.add(crearRespuesta("Iconos", false))


        questionList.add(crearPregunta("", answers4, "El directorio values sirve para almacenar."))
        //val question4 = crearPregunta("", answers4, "Seleccione el gestor de librerías que utiliza android.")


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionario_dm5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Aprendiendo  Kotlin.", "Introducción al desarrollo con Kotlin.", "DESARROLLO MOVIL", 4, "kotlin,conceptos")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Web", true))
        answers1.add(crearRespuesta("Móvil", true))
        answers1.add(crearRespuesta("Ninguna.", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Kotlin es un lenguaje oficial para desarrollar en plataformas"))

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Orientado a Objetos.", false))
        answers2.add(crearRespuesta("Funcional.", false))
        answers2.add(crearRespuesta("Multiparadigma.", true))

        questionList.add(crearPregunta("", answers2, "Seleccione los paradigmas que soporta Kotlin"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Google", false))
        answers3.add(crearRespuesta("JetBrains", true))
        answers3.add(crearRespuesta("Microsoft", false))


        questionList.add(crearPregunta("", answers3, "Kotlin en propiedad de la empresa"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Inferencia de Tipos", true))
        answers4.add(crearRespuesta("Interoperabilidad con Java.", true))
        answers4.add(crearRespuesta("Distinción entre tipos nullables y no-nullables ", true))
        answers4.add(crearRespuesta("Paradigma estrictamente funcional", false))


        questionList.add(crearPregunta("", answers4, "Seleccionar las principales características que brinda kotlin"))
        //val question4 = crearPregunta("", answers4, "Seleccione el gestor de librerías que utiliza android.")


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionario_dm6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sdk Android ", "Introdución al sdk de Android", "DESARROLLO MOVIL", 2, "sdk, android")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Si", true))
        answers1.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers1, "Las versiones android estan identificadas por un sdk"))

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("compileSdkVersion.", true))
        answers2.add(crearRespuesta("targetSdkVersion", true))
        answers2.add(crearRespuesta("minSdkVersion", true))
        answers2.add(crearRespuesta("mediumSdkVersion.", false))

        questionList.add(crearPregunta("", answers2, "En el desarrollo de android usamos la version de sdk para configurar atributos como"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionario_dm7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Despliegue de una aplicación Android", "Nociones básicas para el despliegue de una aplicacion en la Play Store", "DESARROLLO MOVIL", 2, "despliegue, aplicaciones, android")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Debug", false))
        answers1.add(crearRespuesta("Release", true))

        questionList.add(crearPregunta("", answers1, "Para poder desplegar una apliacion necesitamos una compilacion de tipo"))

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "El apk que sera desplegado en la Play store debe estar firmado por un desarrollador"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionario_dm8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Plataformas Móvil", "Plataformas utilizadas para desarrollar aplicaciones moviles", "DESARROLLO MOVIL", 2, "plataforma, aplicacione, moviles")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Android", false))
        answers1.add(crearRespuesta("Ios", false))
        answers1.add(crearRespuesta("React Native", true))
        answers1.add(crearRespuesta("Ionic", true))
        answers1.add(crearRespuesta("Flutter", true))


        questionList.add(crearPregunta("", answers1, "Seleccion las plataformas utilizadas para el desarrollo multiplaforma"))

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "Google desarrollo flutter para el desarrollo multiplaforma"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionario_dm9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Firebase", "Conociendo firebase para desarrollo móvil", "DESARROLLO MOVIL", 2, "firebase, movil, desarrollo")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Herramienta para desarrollo multiplaforma", false))
        answers1.add(crearRespuesta("Servidor como servicio", true))
        answers1.add(crearRespuesta("Sistema operativo", true))


        questionList.add(crearPregunta("", answers1, "Firebase es"))

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "Firebase es un producto de google"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionario_dm10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Cloud Firestore", "Introducción a la base de datos firestore para desarrollo móvil", "DESARROLLO MOVIL", 2, "firestore, base de datos, movil")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Si", true))
        answers1.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers1, "Firestore es una base de datos de tipo NoSql"))

        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "Firestore es una base de datos a tiempo a real"))


        questionnaire.questions = questionList
        return questionnaire
    }


    /*DESARROLLO WEB FRONTEND 20*/

    fun cuestionariodw1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Html", "Etiquetas Html de forma general", "DESARROLLO WEB FRONTEND", 3, "etiquetas, html")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("<script>, <link> ,<title> ", true))
        answers1.add(crearRespuesta("<base>,<meta>", true))
        answers1.add(crearRespuesta("<input><script><label> ", false))
        answers1.add(crearRespuesta(" <head>,<script>, <link> ", false))


        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Que etiquetas pueden ir dentro de <head>"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Para presentar texto en la página web", false))
        answers2.add(crearRespuesta("Muestra un título en la barra del navegador ", true))
        answers2.add(crearRespuesta("Presenta título general en la página web", false))

        questionList.add(crearPregunta("", answers2, "Para qué sirve la etiqueta <title>"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("<hr>,<p>,<label>,<textarea>", false))
        answers3.add(crearRespuesta("<input>, <br>,<hr>  ", true))


        questionList.add(crearPregunta("", answers3, "Que etiquetas no necesitan ser cerradas"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Html", "Etiqueta html input", "DESARROLLO WEB FRONTEND", 4, "etiqueta, html, input")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("placeholder, name, id", false))
        answers1.add(crearRespuesta("url, email, text, tel, number ", true))


        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "El atributo “type” que tipo de restricciones permite la etiqueta <input>"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("button", false))
        answers2.add(crearRespuesta("submit ", true))
        answers2.add(crearRespuesta("input", false))

        questionList.add(crearPregunta("", answers2, "Cual es el atributo que permite que la etiqueta <input> se convierta en un botón que envía el formulario"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("text ", true))
        answers3.add(crearRespuesta("number ", false))
        answers3.add(crearRespuesta("placeholder ", false))


        questionList.add(crearPregunta("", answers3, "Cual es el valor predeterminado que obtienen el atributo “type” en la etiqueta <input>"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("file", true))
        answers4.add(crearRespuesta("images", false))
        answers4.add(crearRespuesta("source", false))


        questionList.add(crearPregunta("", answers4, "Que atributo permite que la etiqueta <input> solo ingresar archivos "))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Html", "Restricciones Html en los atributos ", "DESARROLLO WEB FRONTEND", 2, "atributos, html, restricciones")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("maxlength, placeholder, name, id", false))
        answers1.add(crearRespuesta("type, pattern, min, max, required, maxlength ", true))


        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Que atributos permiten validar o restringir el ingreso de cualquier tipo, tamaño o cadena de caracteres en las etiquetas"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("<label>,<b>,<p>,<body> ", true))
        answers2.add(crearRespuesta("<input>,<select>,<label>", false))

        questionList.add(crearPregunta("", answers2, "Que etiquetas no necesitan restricciones para el ingreso de datos "))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Angular CLI", "Introducción a Angular CLI", "DESARROLLO WEB FRONTEND", 3, "angular,introducción")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Es un lenguaje de programación.", false))
        answers1.add(crearRespuesta("Es un gestor de paquetes", false))
        answers1.add(crearRespuesta("Es una herramienta para gestionar proyectos de angular ", true))


        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Que es angular cli"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("ng new nombre proyecto", true))
        answers2.add(crearRespuesta("npm new nombre proyecto ", false))
        answers2.add(crearRespuesta("npm new nombre proyecto", false))


        questionList.add(crearPregunta("", answers2, "Cual es el comando que permite crear un proyecto angular"))


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("ng serve ", true))
        answers3.add(crearRespuesta("ng server", false))
        answers3.add(crearRespuesta("npm start", false))


        questionList.add(crearPregunta("", answers3, "Como se puede ejecutar un proyecto angular con angular cli"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Angular ", "Conceptos generales Angular", "DESARROLLO WEB FRONTEND", 2, "conceptos, angular")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Es un lenguaje de programación", false))
        answers1.add(crearRespuesta("Es gestor de paquetes", false))
        answers1.add(crearRespuesta("Es un framework de desarrollo  para javascript  ", true))


        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Que es angular"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Verdadero ", true))
        answers2.add(crearRespuesta("Falso", false))


        questionList.add(crearPregunta("", answers2, "Un componente va a controlar un trozo de pantalla o de vista"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("CSS Básico", "Conceptos básicos sobre css desarrollo web", "DESARROLLO WEB FRONTEND", 2, "csss, básico")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("text-type", false))
        answers1.add(crearRespuesta("font-type", false))
        answers1.add(crearRespuesta("text-family", false))
        answers1.add(crearRespuesta("font-family", true))


        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "¿Qué propiedad de CSS se emplea para cambiar el tipo de letra de un elemento?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("a {underline:no-underline} ", false))
        answers2.add(crearRespuesta("a {underline:none}", false))
        answers2.add(crearRespuesta("a {text-decoration:no-underline} ", false))
        answers2.add(crearRespuesta("a {text-decoration:none} ", true))


        questionList.add(crearPregunta("", answers2, "¿Cómo se hace en CSS para que un enlace se muestre sin el subrayado?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Javascript Básico", "Nociones basicas de javascript", "DESARROLLO WEB FRONTEND", 2, "javascript, básico")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("<script>", true))
        answers1.add(crearRespuesta("<javascript>", false))
        answers1.add(crearRespuesta("<scripting>", false))
        answers1.add(crearRespuesta("<js>", false))


        questionList.add(crearPregunta("", answers1, "¿Qué etiqueta de HTML se emplea para escribir código JavaScript?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Compilado", false))
        answers2.add(crearRespuesta("Interpretado ", true))
        answers2.add(crearRespuesta("No estructurado ", false))
        answers2.add(crearRespuesta("a {text-decoration:none} ", false))


        questionList.add(crearPregunta("", answers2, "JavaScript es un lenguaje de programación"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Javascript Básico", "Funciones básicas utilizadas", "DESARROLLO WEB FRONTEND", 2, "funciones, javascript, básico")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("alert(\"Hola mundo!\"); ", true))
        answers1.add(crearRespuesta("msgBox(\"Hola mundo!); ", false))

        questionList.add(crearPregunta("", answers1, "¿cómo se muestra una ventana con el mensaje:  Hola mundo ?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("function miFuncion() ", correct = true))
        answers2.add(crearRespuesta("function:miFuncion() ", correct = false))
        answers2.add(crearRespuesta("function->miFuncion() ", correct = false))


        questionList.add(crearPregunta("", answers2, "En JavaScript, ¿cómo se define una función llamada \"miFuncion\"?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Javascript Variables", "Declaracion y tipos de variables", "DESARROLLO WEB FRONTEND", 2, "variables, javascript")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Siempre hay que declarar el tipo de dato", false))
        answers1.add(crearRespuesta("No se declara el tipo de dato", true))

        questionList.add(crearPregunta("", answers1, "Respecto a la declaración de variables"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("No", correct = true))
        answers2.add(crearRespuesta("Si", correct = false))


        questionList.add(crearPregunta("", answers2, "Javascrip es fuertemente tipado"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariodw10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Comentarios en javascript", "Utilizacion de comentarios sola linea y multilinea", "DESARROLLO WEB FRONTEND", 2, "comentarios, javascript")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("//", true))
        answers1.add(crearRespuesta("/", false))
        answers1.add(crearRespuesta("/*", false))

        questionList.add(crearPregunta("", answers1, "Comentario de una sola linea se define con"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("/*  ---  */", correct = true))
        answers2.add(crearRespuesta("/ ------ /", correct = false))


        questionList.add(crearPregunta("", answers2, "Comentario multinea se define con"))

        questionnaire.questions = questionList
        return questionnaire
    }


    /*TELEMÁTICA 30*/

    fun cuestionariot1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Redes y Telecomunicaciones.", "Conceptos sobre redes y telecomunicaciones.", "TELEMÁTICA", 5, "conceptos, redes, telecomunicaciones")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Modem ", false))
        answers1.add(crearRespuesta("Repetidor", true))
        answers1.add(crearRespuesta("Router.", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Es el dispositivo que Amplifica la señal para llegar a su destino."))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Router", false))
        answers2.add(crearRespuesta("Switche", true))
        answers2.add(crearRespuesta("Gateway", false))

        questionList.add(crearPregunta("", answers2, "Es un dispositivo de red conocido como conmutador de paquetes y funciona en la capa de enlace del modelo osi."))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Man", false))
        answers3.add(crearRespuesta("Lan", false))
        answers3.add(crearRespuesta("Wan", true))


        questionList.add(crearPregunta("", answers3, "Según la clasificación de las redes cuál de ellas utiliza la banda ancha"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Netbios", false))
        answers4.add(crearRespuesta("Tcp/ip", false))
        answers4.add(crearRespuesta("Ip", true))


        questionList.add(crearPregunta("", answers4, "Es el protocolo que funciona en la capa de red del modelo OSI y es el protocolo de internet"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Comando", false))
        answers5.add(crearRespuesta("Conjunto de dispositivos", false))
        answers5.add(crearRespuesta("Conjunto de normas y reglas.", true))

        questionList.add(crearPregunta("", answers5, "Un protocolo de Comunicación se define como"))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Tipos de Redes", "Redes tipo wan, pan, lan, can", "TELEMÁTICA", 5, "tipos, redes")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Casa ", true))
        answers1.add(crearRespuesta("Conteniente ", false))
        answers1.add(crearRespuesta("País..", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Según la extensión física en cual de estos componentes podríamos instalar una red lan"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Compartir Información entre usuarios", false))
        answers2.add(crearRespuesta("Compartir energía entre usuarios.", false))
        answers2.add(crearRespuesta("Compartir recursos, información entre grupos de computadores y usuarios múltiples de red", true))

        questionList.add(crearPregunta("", answers2, "Las redes de comunicación permiten"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("100 Mbps", true))
        answers3.add(crearRespuesta("50 Gbps ", false))
        answers3.add(crearRespuesta("500 Gbps ", false))


        questionList.add(crearPregunta("", answers3, "¿Qué velocidad tienen las redes LAN?"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("5 Km ", false))
        answers4.add(crearRespuesta("6 Km", false))
        answers4.add(crearRespuesta("4 Km", true))


        questionList.add(crearPregunta("", answers4, " ¿Qué distancia de cobertura tienen la red MAN?"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Wan", true))
        answers5.add(crearRespuesta("Pan", true))
        answers5.add(crearRespuesta("Men", false))
        answers5.add(crearRespuesta("Can", true))
        answers5.add(crearRespuesta("Lan", true))

        questionList.add(crearPregunta("", answers5, "Qué tipos de redes existen?"))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Topologia de Redes.", "Introducción a las topologías en redes y telecomunicaciones.", "TELEMÁTICA", 5, "topologia, redes, telecomunicaciones")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Estrella , Anillo y Red  ", false))
        answers1.add(crearRespuesta("Estrella, Bus y Anillo ", true))
        answers1.add(crearRespuesta("Red Estrella , Red Bus y Red Session", false))
        answers1.add(crearRespuesta("Bus, Sol y Sección", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "¿Cuáles son las tres principales topologías?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Los mensajes que van de un equipo a otro deben pasar por un nodo central ", false))
        answers2.add(crearRespuesta("Los mensajes se envían, a través del canal, a todas las estaciones, y rebotan de un extremo a otro del canal para, posteriormente, ser admitidos por el equipo al que van dirigidos ", false))
        answers2.add(crearRespuesta("El equipo siempre recibe mensajes del anterior, y cuando no van dirigidos a el los transmite al equipo siguiente", true))

        questionList.add(crearPregunta("", answers2, "En la topología en anillo"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Bus ", false))
        answers3.add(crearRespuesta("Anillo  ", false))
        answers3.add(crearRespuesta("Estrella ", true))


        questionList.add(crearPregunta("", answers3, "Topología donde el mensaje que envía una computadora a otra debe pasar por el concentrador"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Hay que cambiar todo", false))
        answers4.add(crearRespuesta("Sigue trabajando ", false))
        answers4.add(crearRespuesta("Falla", true))


        questionList.add(crearPregunta("", answers4, " En la topología en anillo si una computadora falla la red."))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Una ", false))
        answers5.add(crearRespuesta("Diez ", false))
        answers5.add(crearRespuesta("Cuatro", true))
        answers5.add(crearRespuesta("Tres", false))

        questionList.add(crearPregunta("", answers5, " ¿Cuántas topologías de red existen?"))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Tipos de cables Redes.", "Introducción a los tipos de cables utilizados en redes.", "TELEMÁTICA", 5, "cables, redes")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Verdadero", false))
        answers1.add(crearRespuesta("Falso  ", true))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "El cable de fibra óptica está formado por varios hilos trenzados y se utiliza en sistemas telefónicos."))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Verdadero", false))
        answers2.add(crearRespuesta("Falso ", true))

        questionList.add(crearPregunta("", answers2, "El cable coaxial transmite información digital a gran velocidad utilizando luz."))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Cable Coaxial ", true))
        answers3.add(crearRespuesta("Cable par trenzado   ", false))
        answers3.add(crearRespuesta("Cable Fibra Optica  ", false))


        questionList.add(crearPregunta("", answers3, "Es el medio guiado más barato y más usado. Consiste en un par de cables, embutidos para su aislamiento, para cada enlace de comunicación."))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Fibra Óptica ", false))
        answers4.add(crearRespuesta("Par trenzado ", false))
        answers4.add(crearRespuesta("Coaxial ", false))
        answers4.add(crearRespuesta("Ninguna ", true))


        questionList.add(crearPregunta("", answers4, "Cable formado por 7 hilos generalmente usado en todas las redes de computadoras en un edificio"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Conexión a tv", true))
        answers5.add(crearRespuesta("Conexión a computadores ", false))
        answers5.add(crearRespuesta("Todas las anteriores ", false))

        questionList.add(crearPregunta("", answers5, "¿El cable coaxial sirve para ?"))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Modelo Osi", "Introducción a las capas del modelo OSI.", "TELEMÁTICA", 5, "modelo osi, capas")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Open systems intercommunication ", false))
        answers1.add(crearRespuesta("Open systems interconnection  ", true))
        answers1.add(crearRespuesta("Open status interconnection", false))
        answers1.add(crearRespuesta("Open systems intermediation ", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "Defina el significado de OSI "))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("11 ", false))
        answers2.add(crearRespuesta("6  ", false))
        answers2.add(crearRespuesta("7 ", true))
        answers2.add(crearRespuesta("11 ", false))

        questionList.add(crearPregunta("", answers2, "¿Cuántas capas tiene el modelo OSI?"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Red", false))
        answers3.add(crearRespuesta("Física  ", false))
        answers3.add(crearRespuesta("Aplicación", false))
        answers3.add(crearRespuesta("Transporte", true))


        questionList.add(crearPregunta("", answers3, "¿Cuál es la capa del modelo OSI que proporciona una entrega de mensajes confiable de extremo a extremo con confirmaciones?"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Establece una sintaxis y semántica de la información transmitida. ", false))
        answers4.add(crearRespuesta("Específica cables, conectores y componentes de interfaz con el medio de transmisión.", true))
        answers4.add(crearRespuesta("Permite a usuarios en diferentes máquinas establecer una sesión ", false))
        answers4.add(crearRespuesta("Ninguna de las anteriores  ", false))


        questionList.add(crearPregunta("", answers4, "Mencione una función de la capa física "))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Agrega una secuencia especial de bits al principio y al final del flujo inicial de bits", true))
        answers5.add(crearRespuesta("Gestionar recursos del sistema digital ", false))
        answers5.add(crearRespuesta("Compresión de datos", false))

        questionList.add(crearPregunta("", answers5, "Mencione una función de la Capa Enlace de Datos."))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Direccion IP", "Conceptos básicos sobre direcciones ip", "TELEMÁTICA", 2, "direccion ip, conceptos")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta(" Direccionamiento y control de paquetes.", false))
        answers1.add(crearRespuesta("Direccionamiento y fragmentación. ", true))
        answers1.add(crearRespuesta(" Direccionamiento, fragmentación y control de paquetes.", false))

        questionList.add(crearPregunta("", answers1, "¿Qué funciones básicas proporciona el protocolo IP?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("No tener un tamaño fijo. ", false))
        answers2.add(crearRespuesta("No está garantizada la entrega en el destino.  ", true))
        answers2.add(crearRespuesta(" No tener un tamaño fijo. ", false))

        questionList.add(crearPregunta("", answers2, "El protocolo IP no se considera fiable por"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Protocolo TCP IP", "Conceptos básicos sobre tcp ip", "TELEMÁTICA", 2, "protocolo, tcp ip")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Lenguaje WI-FI de comunicación.", false))
        answers1.add(crearRespuesta("Un protocolo de transmisión de datos. ", true))
        answers1.add(crearRespuesta("Un tipo de red.", false))

        questionList.add(crearPregunta("", answers1, "Las siglas TCP/IP se corresponden a"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta(" Aplicación, Físico, Red.", false))
        answers2.add(crearRespuesta("Presentación, Sesión, Transporte.", true))
        answers2.add(crearRespuesta(" Red, Enlace a Datos, Aplicación.", false))

        questionList.add(crearPregunta("", answers2, "A que 3 capas del modelo OSI equivale la capa de transporte del modelo TCP/IP."))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Topogologias de Red", "Caracterisitcas de las topologias de red", "TELEMÁTICA", 2, "topologias, red, caracteristicas")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Solo podrían comunicarse 3 de los ordenadores", false))
        answers1.add(crearRespuesta("La red dejaría de funcionar", true))
        answers1.add(crearRespuesta("La red funcionará con normalidad", false))

        questionList.add(crearPregunta("", answers1, "¿si tenemos una red en topología de anillo, y se rompe el cable entre el primer y el segundo ordenador que pasaría?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta(" Alto nivel de colisiones", false))
        answers2.add(crearRespuesta("Fallo en un nodo afecta a la red completa.", true))
        answers2.add(crearRespuesta(" Para redes pequeñas con poco tráfico", false))

        questionList.add(crearPregunta("", answers2, "Indique la respuesta incorrecta sobre las redes con topología de Bus"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Arquitectura de una red", "Caracterisitcas de la arquitectura de red", "TELEMÁTICA", 2, "caractersticas, arquitectura, red")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta(" Transporte, aplicación y topología.", false))
        answers1.add(crearRespuesta("Topologia, metodo de acceso y Protocolos de comunicación.", true))
        answers1.add(crearRespuesta("Red, topología y datos.", false))

        questionList.add(crearPregunta("", answers1, "Cuáles son las tres características de la arquitectura de una red:"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta(" Método de acceso a la red, protocolo de comunicación y servidor.", false))
        answers2.add(crearRespuesta("Topología de red, método de acceso a la red y protocolo de comunicación.", true))
        answers2.add(crearRespuesta("  Servidor, hardware de red y protocolo de comunicación.", false))

        questionList.add(crearPregunta("", answers2, "Las tres características que definen la arquitectura de una red son:"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariot10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Dirección IP", "Caracterisitcas de las direcciones ip", "TELEMÁTICA", 2, "caracteristicas, direcciones ip")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("8 bytes.8 bytes.8 bytes.8 bytes", false))
        answers1.add(crearRespuesta("8 bits.8 bits.8 bits.8 bits", true))
        answers1.add(crearRespuesta("24 cifras en binario", false))

        questionList.add(crearPregunta("", answers1, "¿Qué formato sigue una dirección IP de IPv4?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta(" 16 bits", false))
        answers2.add(crearRespuesta("32 bits", true))
        answers2.add(crearRespuesta("64 bits", false))

        questionList.add(crearPregunta("", answers2, "¿Cuantos bits componen una IP?"))

        questionnaire.questions = questionList
        return questionnaire
    }


    /*BASE DE DATOS 40*/

    fun cuestionariobd1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sentencias Mysql", "Sentencias basicas para base de datos mysql", "BASE DE DATOS", 5, "sentencias, mysql")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Delete database", false))
        answers1.add(crearRespuesta("Drop Database", true))
        answers1.add(crearRespuesta("Erase Database ", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, " Sentencia utilizada para eliminar una base de datos"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Verdadero ", true))
        answers2.add(crearRespuesta("Falso ", false))

        questionList.add(crearPregunta("", answers2, " La sentencia DESCRIBE proporciona información acerca de columnas en una tabla."))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Falso", false))
        answers3.add(crearRespuesta("Verdadero ", true))


        questionList.add(crearPregunta("", answers3, " La sentencia CHANGE TABLE le permite cambiar la estructura de una tabla existente."))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Falso", false))
        answers4.add(crearRespuesta("Verdadero", true))


        questionList.add(crearPregunta("", answers4, "El comando USE bd_ejemplo le dice a mySQL que use la base de datos bd_ejemplo como la base de datos por defecto para los comandos siguientes."))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Analize", false))
        answers5.add(crearRespuesta("Describe", false))
        answers5.add(crearRespuesta("Explain", true))

        questionList.add(crearPregunta("", answers5, "El comando usado para obtener información acerca de cómo MySQL podría ejecutar una consulta."))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Introducción MongoDB", "Conceptos básicos sobre la base de datos mongo db.", "BASE DE DATOS", 5, "mongodb db, conceptos")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("XML ", false))
        answers1.add(crearRespuesta("JSON", true))
        answers1.add(crearRespuesta("TXT ", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "MongoDB es un almacén de datos no relacional para documentos"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Verdadero ", true))
        answers2.add(crearRespuesta("Falso ", false))

        questionList.add(crearPregunta("", answers2, " Mongo es schemaless (sin esquema)."))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Falso", true))
        answers3.add(crearRespuesta("Verdadero ", false))


        questionList.add(crearPregunta("", answers3, "MongoDB soporta JOINS"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Falso", true))
        answers4.add(crearRespuesta("Verdadero", false))


        questionList.add(crearPregunta("", answers4, "MongoDB soporta Transactions (Transacciones)"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Verdadero ", true))
        answers5.add(crearRespuesta("Falso", false))

        questionList.add(crearPregunta("", answers5, "MongoDB es más escalable que un sistema RDBMS (Sistema Gestor de Bases de Datos Relacionales)."))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Introducción Base de Datos", "Fundamentos básicos sobre base de datos", "BASE DE DATOS", 5, "fundamentos, básicos")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Sólo agregar, eliminar y actualizar datos en archivos existentes de la BD  ", false))
        answers1.add(crearRespuesta("Agregar archivos nuevos en la BD, insertar, eliminar, actualizar y obtener datos de archivos existentes de la BD", true))
        answers1.add(crearRespuesta("Actualizar sólo datos de archivos existentes  ", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "En una BD al usuario del sistema se le brindarán recursos para realizar diversas operaciones sobre estos archivos, tales como:"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Proporcionar Una forma de almacenar y recuperar información de una base de Datos de manera que sea práctica como eficiente ", true))
        answers2.add(crearRespuesta("Diseñar base de datos, y utilizar sus lenguajes  ", false))
        answers2.add(crearRespuesta("Contener información de los usuarios, manipularla, diseñar base de datos y utilizar sus lenguajes.  ", false))
        answers2.add(crearRespuesta("Compartir datos a los usuarios  ", false))

        questionList.add(crearPregunta("", answers2, "Cual es Su principal objetivo del SGBD?"))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Es un conjunto de Algoritmos que permite la Gestión y Optimización de Base de datos.", true))
        answers3.add(crearRespuesta("Es un nivel de mediación entre los niveles internos y externos.  ", false))
        answers3.add(crearRespuesta("Es un conjunto de archivos que permite la Gestión y Optimización de Base de datos. ", false))
        answers3.add(crearRespuesta("Es un conjunto de niveles que permiten la Gestión de Base de Datos.", false))


        questionList.add(crearPregunta("", answers3, "Qué es un Motor de Bases de Datos?"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        /*Pregunta 4*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Falso", true))
        answers4.add(crearRespuesta("Verdadero", false))


        questionList.add(crearPregunta("", answers4, "MongoDB soporta Transactions (Transacciones)"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")


        /*PREGUNTA 5*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Verdadero ", true))
        answers5.add(crearRespuesta("Falso", false))

        questionList.add(crearPregunta("", answers5, "MongoDB es más escalable que un sistema RDBMS (Sistema Gestor de Bases de Datos Relacionales)."))
        //val question5 = crearPregunta("", answers5, "Selecciones los directorios de recursos que usa Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Base de Datos Relacional", "Fundamentos básicos sobre base de datos relacionales", "BASE DE DATOS", 5, "relacional, base de datos")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Registro", false))
        answers1.add(crearRespuesta("Tabla", true))
        answers1.add(crearRespuesta("Archivos", false))


        questionList.add(crearPregunta("", answers1, "Conjunto de campos realacionados"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Codigo del empleado", true))
        answers2.add(crearRespuesta("Nombre del Empleado", false))
        answers2.add(crearRespuesta("Dirección", false))

        questionList.add(crearPregunta("", answers2, "Ejemplo de una llave primaria"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Base de Datos Relacional", "Caracteristicas de  base de datos relacionales", "BASE DE DATOS", 5, "caracteristicas, relacional")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Representar la informaciòn por medio de nodos", false))
        answers1.add(crearRespuesta("Realizar conexiones para relacionar los datos", true))
        answers1.add(crearRespuesta("Utilizar arboles para representar los datos", false))


        questionList.add(crearPregunta("", answers1, "Las base de datos relacionales se caracteriza por"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "¿Una tabla es un lugar donde se almacenan datos sobre un determinado tema, como por ejemplo clientes?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Modelo de Base de datos", " Modelos de base de datos existentes", "BASE DE DATOS", 5, "modelos")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Modelo jerárquico", false))
        answers1.add(crearRespuesta("Modelo en red", true))
        answers1.add(crearRespuesta("Modelo relacional", false))


        questionList.add(crearPregunta("", answers1, "¿Cual no es un modelo de base de datos?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("MongoDb", true))
        answers2.add(crearRespuesta("Mysql", true))
        answers2.add(crearRespuesta("TensorFlow", false))

        questionList.add(crearPregunta("", answers2, "Seleccion cuales son plataformas o gestores de base de datos"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Base de datos no relacionales", " Introducción base de datos no relacionales.", "BASE DE DATOS", 5, "no relacionales, introducción")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("No", false))
        answers1.add(crearRespuesta("Si", true))


        questionList.add(crearPregunta("", answers1, "¿En base de datos no relaciones existe duplicidad de datos?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("MongoDb", true))
        answers2.add(crearRespuesta("Mysql", false))
        answers2.add(crearRespuesta("Firestore", true))

        questionList.add(crearPregunta("", answers2, "Seleccion cuales son plataformas o gestores de base de datos no relacionales"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Base de datos no relacionales", " Caracteristicas base de datos no relacionales.", "BASE DE DATOS", 5, "no relacionales, características")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("No", false))
        answers1.add(crearRespuesta("Si", true))


        questionList.add(crearPregunta("", answers1, "¿En base de datos no relaciones existe duplicidad de datos?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("No", true))
        answers2.add(crearRespuesta("Si", false))

        questionList.add(crearPregunta("", answers2, "Las consultas se ejecutan mediante lenguaje Sql"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Base de datos relacionales", " Características base de datos relacionales.", "BASE DE DATOS", 5, "relacional, características")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("No", true))
        answers1.add(crearRespuesta("Si", false))


        questionList.add(crearPregunta("", answers1, "¿En base de datos relaciones existe duplicidad de datos?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "Las consultas se ejecutan mediante lenguaje Sql"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariobd10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Clave primaria", " Caracteristicas de una clave primaria", "BASE DE DATOS", 5, "clave primaria, características")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Si", true))
        answers1.add(crearRespuesta("No", false))


        questionList.add(crearPregunta("", answers1, "¿La clave primera debe ser unica?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "La clave primera puede estar compuesto de mas de un campo"))

        questionnaire.questions = questionList
        return questionnaire
    }


    /*PROGRAMACION BÁSICA 50*/

    fun cuestionariopb1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("PROGRAMACION BÁSICA.", "Conceptos sobre PROGRAMACION BÁSICA", "PROGRAMACION BÁSICA", 3, "programación, básica")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Sentencias de control.", true))
        answers1.add(crearRespuesta("Tipos de datos.", false))
        answers1.add(crearRespuesta("Ninguno.", false))

        //val question1 = crearPregunta("", answers1, "Seleccione el IDE oficial para el desarrollo de aplicaciones nativas Android.")
        questionList.add(crearPregunta("", answers1, "If, else son"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Ciclos repetitivos.", false))
        answers2.add(crearRespuesta("Tipos de datos.", false))
        answers2.add(crearRespuesta("Ninguno.", true))

        questionList.add(crearPregunta("", answers2, "While, for, foreach "))

        //val question2 = crearPregunta("", answers2, "Seleccione el archivo de configuración de una aplicación en android")


        /*Tercera Pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Tipos de datos.", true))
        answers3.add(crearRespuesta("Sentencias de control.", false))
        answers3.add(crearRespuesta("Ciclos repetitivos.", false))


        questionList.add(crearPregunta("", answers3, "int, char, float, string y boolean son"))
        //val question3 = crearPregunta("", answers3, "Seleccione los lenguajes oficiales para desarrollo nativo en Android.")

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Lenguajes de Programacion.", "Definición de un lenguaje de programación", "PROGRAMACION BÁSICA", 1, "definición, lenguaje, programación")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Es un lenguaje o Software diseñado para describir un conjunto de acciones consecutivas que un equipo debe ejecutar.", true))
        answers1.add(crearRespuesta("Es un Programa que define un medio de comunicación compartido por un grupo de personas y la PC", false))

        questionList.add(crearPregunta("", answers1, "¿Que es un lenguaje de programación?"))


        /*Segunda Pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Si.", true))
        answers2.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers2, "Un lenguaje de programación utiliza palabras reservadas que son propias de cada lenguaje"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Tipos de lenguaje de Programacion.", "Clasificación de lenguajes como alto nivel, bajo nivel.", "PROGRAMACION BÁSICA", 1, "lenguaje,alto nivel, bajo nivel")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("El lenguaje máquina, Lenguajes ensambladores y de lenguajes de alto nivel.", true))
        answers1.add(crearRespuesta("Únicamente de bajo nivel y lenguajes de alto nivel.", false))

        questionList.add(crearPregunta("", answers1, "¿Cuáles son los tipos de lenguaje de programación?"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Lenguaje Emsamblador", "Definición de lenguaje emsamblador", "PROGRAMACION BÁSICA", 1, "lenguaje, emsamblador")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Es un lenguaje de programación de bajo nivel para los computadores, microprocesadores, microcontroladores y otros circuitos integrados programables.", true))
        answers1.add(crearRespuesta("Sistema de códigos directamente interpretable (0 y 1) por un circuito microprogramable, como el microprocesador de una computadora.", false))

        questionList.add(crearPregunta("", answers1, "¿Que es un Lenguaje Máquina?"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Lenguaje Máquina", "Definición de lenguaje máquina", "PROGRAMACION BÁSICA", 1, "lenguaje, máquina")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Sistema de códigos directamente interpretable por un circuito microprogramable, como el microprocesador de una computadora o el microcontrolador de un autómata", true))
        answers1.add(crearRespuesta("Sistema de códigos para programar juegos de computadora de una manera mas entendible para el ser humano", false))

        questionList.add(crearPregunta("", answers1, "¿Que es un Lenguaje Máquina?"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Lenguaje Alto Nivel", "Definición de lenguaje de alto nivel", "PROGRAMACION BÁSICA", 1, "lenguaje, alto nivel")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Se caracteriza por expresar los algoritmos de una manera adecuada a la capacidad cognitiva humana, en lugar de la capacidad ejecutora de las máquinas", true))
        answers1.add(crearRespuesta("Es un lenguaje de programación de bajo nivel para los computadores, microprocesadores, microcontroladores y otros circuitos integrados programables.", false))

        questionList.add(crearPregunta("", answers1, "¿Que es un Lenguaje de Alto nivel?"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Programacion Orientada a objetos", "Lenguajes de programación con paradigma orientado a objetos", "PROGRAMACION BÁSICA", 1, "paradigma, orientado, objetos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Java, Kotlin, C#,", true))
        answers1.add(crearRespuesta("HTML, XML, VML, Java, PHP, C++, Fortran, Cobol, Lisp, entre otros", false))

        questionList.add(crearPregunta("", answers1, "Son lenguajes de programación orientado a objetos"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Tipos de Datos", "Principales tipos de datos usados en programación", "PROGRAMACION BÁSICA", 1, "tipos, datos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Cadena, Boleano, Carácter, Numeros, Entero.", true))
        answers1.add(crearRespuesta("Simbólicos, de estructura, de cadena, de complemento, generales, particulares, entre otros.", false))

        questionList.add(crearPregunta("", answers1, "Son  tipos de datos que se utlizan en Programación."))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Clases", "Definción de una clase en programación", "PROGRAMACION BÁSICA", 1, "clase, programacion")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Un modelo donde se definen caracteristicas, metodos", true))
        answers1.add(crearRespuesta("Una caracterisitca que pueder compartir varios objetos.", false))

        questionList.add(crearPregunta("", answers1, "Una clases es"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionariopb10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Atributos", "Definción de una atributo de una clase en programación", "PROGRAMACION BÁSICA", 1, "atributos, clase")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Un modelo donde se definen caracteristicas, metodos", false))
        answers1.add(crearRespuesta("Una caracteristica", true))

        questionList.add(crearPregunta("", answers1, "Un Atributo es"))


        questionnaire.questions = questionList
        return questionnaire
    }


    /*ARQUITECTURA DE COMPUTADORES 60 */

    fun cuestionarioac1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Perificos de entrada", "Perificos de entrada de un computador", "ARQUITECTURA DE COMPUTADORES", 1, "perifericos, entrada")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("wepcam, microfono, teclado, mouse, escaner", true))
        answers1.add(crearRespuesta("microfono, impresora, bocinas", false))
        answers1.add(crearRespuesta("bocinas, impresora, monitor", false))

        questionList.add(crearPregunta("", answers1, "Son perifericos de Entrada de una Computadora"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Perificos de salida", "Perificos de salida de un computador", "ARQUITECTURA DE COMPUTADORES", 1, "perifericos, salida")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("bosinas, impresora, monitor, audifonos", true))
        answers1.add(crearRespuesta("microfono, impresora, bocinas", false))
        answers1.add(crearRespuesta("bocinas, escaner, monitor", false))

        questionList.add(crearPregunta("", answers1, "¿Cuales de los siguientes son Perifericos de Salida?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("CPU", "Deficion de cpu", "ARQUITECTURA DE COMPUTADORES", 1, "cpu, unidad central procesamiento")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Unidad Central de Procesos", true))
        answers1.add(crearRespuesta("Control de Procesamiento Unidireccional", false))
        answers1.add(crearRespuesta("Unidad de Control de Procesos", false))

        questionList.add(crearPregunta("", answers1, "¿Cual es el significado de C.P.U?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("CPU", "Composicion del cpu", "ARQUITECTURA DE COMPUTADORES", 1, "composicion, cpu, unidad central procesamiento")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("ALU, registros, sección de control y bus lógico", true))
        answers1.add(crearRespuesta("Disco duro, ALU, perifericos", false))
        answers1.add(crearRespuesta("El mouse y teclado, bus lógico", false))

        questionList.add(crearPregunta("", answers1, "¿De qué está compuesto el CPU?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Composcion de una computadora", "Componentes principales de una computadora", "ARQUITECTURA DE COMPUTADORES", 1, "componentes, principales")


        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Tarjeta madre, disco duro, memoria ram y procesador", true))
        answers1.add(crearRespuesta("Mouse, teclado, monitor", false))
        answers1.add(crearRespuesta("Mouse y teclado, bus lógico", false))

        questionList.add(crearPregunta("", answers1, "¿Cuales son los principales componentes internos de una computadora?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Unidades de almacenamiento", "Tipo de unidades de almacenamiento", "ARQUITECTURA DE COMPUTADORES", 1, "unidades, almacenamiento")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("petabyte, kilobyte, bits, megabyte, gigabyte, terabyte", true))
        answers1.add(crearRespuesta("kilobyte, megabyte, yardabyte, terabyte", false))

        questionList.add(crearPregunta("", answers1, "Son las unidades de medida de almacenamiento o capacidad"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Disco Duro", "Velocidad de un disco duro", "ARQUITECTURA DE COMPUTADORES", 1, "disco duro, velocidad")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("RPM.", true))
        answers1.add(crearRespuesta("MHz.", false))

        questionList.add(crearPregunta("", answers1, "La velocidad de un disco duro se mide en"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Disco Duro", "Capacidad de un disco duro", "ARQUITECTURA DE COMPUTADORES", 1, "disco duro, capacidad")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("GB", true))
        answers1.add(crearRespuesta("RPM", false))

        questionList.add(crearPregunta("", answers1, "La capacidad de almacenamiento de un disco duro se mide en"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Bios", "Funciones principales de la bios", "ARQUITECTURA DE COMPUTADORES", 1, "bios, funciones")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Realizar una verificación de periféricos en el arranque del computador", true))
        answers1.add(crearRespuesta("Almacena la información del computador", false))

        questionList.add(crearPregunta("", answers1, " La BIOS es la encargada de"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioac10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Procesador", "Funcion principal de la CPU", "ARQUITECTURA DE COMPUTADORES", 1, "cpu, funciones")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Si", true))
        answers1.add(crearRespuesta("No", false))

        questionList.add(crearPregunta("", answers1, " El procesador es el cerebro del computador"))

        questionnaire.questions = questionList
        return questionnaire
    }


    /*SISTEMAS OPERATIVOS 70*/

    fun cuestionarioaSO1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Procesos", "Definición de los procesos", "SISTEMAS OPERATIVOS", 1, "procesos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Un programa en ejecución que necesita recursos para realizar su tarea", true))
        answers1.add(crearRespuesta("Son colecciones de información relacionada, definidas por sus creadores.", false))
        answers1.add(crearRespuesta("Es una gran tabla de palabras o bytes que se referencia cada una mediante una dirección única", false))

        questionList.add(crearPregunta("", answers1, "¿Qué es un proceso?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sistema Operativo", "Definición de un sistema operativo", "SISTEMAS OPERATIVOS", 1, "sistemas, operativos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Sistema Operativo", true))
        answers1.add(crearRespuesta("Sistema Operacional", false))
        answers1.add(crearRespuesta("Sistema de Gestión de Programas", false))

        questionList.add(crearPregunta("", answers1, "Es un programa o conjunto de programas de un sistema informático que gestiona los recursos de hardware y provee servicios a los programas de aplicación de software"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sistema Operativo", "Objetivos de un sistema operativo", "SISTEMAS OPERATIVOS", 1, "objetivos, sistemas, operativos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Lograr que el sistema de computación se use de manera cómoda", true))
        answers1.add(crearRespuesta("Hacer que todos los drivers se instalen de la manera mas facil", false))
        answers1.add(crearRespuesta("Gestión de tareas", false))

        questionList.add(crearPregunta("", answers1, "El objetivo principal de un sistema operativo es:"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sistema Operativo Multitarea", "Definición de un sistema operativo multitarea", "SISTEMAS OPERATIVOS", 1, "sistema, multitarea")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Permite ejecutar diversos programas al mismo tiempo.", true))
        answers1.add(crearRespuesta("Que un mismo ordenador pueda tener varios microprocesadores que deben utilizarse simultáneamente.", false))
        answers1.add(crearRespuesta("permite que varios usuarios puedan ejecutar programas a la vez.", false))

        questionList.add(crearPregunta("", answers1, "¿Qué es un sistema multitarea?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sistema Multiusuario", "Definición de un sistema multiusuario", "SISTEMAS OPERATIVOS", 1, "sistema, multiusuario")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Permite que varios usuarios puedan ejecutar programas a la vez.", true))
        answers1.add(crearRespuesta("Que un mismo ordenador pueda tener varios microprocesadores que deben utilizarse simultáneamente.", false))
        answers1.add(crearRespuesta("Permite ejecutar diversos programas al mismo tiempo ", false))

        questionList.add(crearPregunta("", answers1, "¿Qué es un sistema multiusuario?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sistema Multiprocesador", "Definición de un sistema multiprocesor", "SISTEMAS OPERATIVOS", 1, "sistema,mutiprocesador")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Que un mismo ordenador pueda tener varios microprocesadores que deben utilizarse simultáneamente. ", true))
        answers1.add(crearRespuesta("Permite que varios usuarios puedan ejecutar programas a la vez.", false))
        answers1.add(crearRespuesta("Permite ejecutar diversos programas al mismo tiempo ", false))

        questionList.add(crearPregunta("", answers1, "¿Qué es un sistema multiprocesador?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Interrupciones", "Interrupciones en un sistema operativo", "SISTEMAS OPERATIVOS", 1, "Interrupciones")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("El sistema operativo guarda el estado del proceso interrumpido. En muchos sistemas esta información se guarda en el bloque de control de proceso interno ", true))
        answers1.add(crearRespuesta("Se reinicia el equipo perdiendo la información de los programas que se están ejecutando", false))
        answers1.add(crearRespuesta("Controlar el uso de los dispositivos físicos de ordenador y detectar los posibles errores que se produzcan en su funcionamiento", false))

        questionList.add(crearPregunta("", answers1, " ¿Qué ocurre con el S.O ante una interrupción?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Memoria cache", "Funcione de la memoria cache", "SISTEMAS OPERATIVOS", 1, "memoria,cache,funciones")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Mantener la informacion en algún sistema de almacenamiento y en la medida que se usa es copiada en una memoria más rápida temporalmente. ", true))
        answers1.add(crearRespuesta("Arrancar el sistema operativo a traves de ms dos\n", false))
        answers1.add(crearRespuesta("Controlar el uso de los dispositivos físicos de ordenador y detectar los posibles errores que se produzcan en su funcionamiento", false))

        questionList.add(crearPregunta("", answers1, " ¿Cual es la función de la memoria cache?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Procesos", "Estados de un proceso ", "SISTEMAS OPERATIVOS", 1, "estados, procesos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Nuevo,Listo,Ejecucion,Bloqueado,Zombie,Terminado ", true))
        answers1.add(crearRespuesta("Nuevo,Listo,Ejecucion,Terminado ", false))
        answers1.add(crearRespuesta("Nuevo,Listo,Ejecucion,Bloqueado,Terminado ", false))

        questionList.add(crearPregunta("", answers1, " Un proceso, a lo largo de su vida, alterna entre diferentes estados de ejecución que son."))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaSO10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Información Procesos", "Informacion asociada a un proceso. ", "SISTEMAS OPERATIVOS", 1, "informacion, procesos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("La siguiente instrucción a ser ejecutada por el proceso. ", true))
        answers1.add(crearRespuesta("La ultima instrucción ejecutada por el proceso. ", false))
        answers1.add(crearRespuesta("La primera instrucción en ser ejecutada por el proceso. ", false))

        questionList.add(crearPregunta("", answers1, " Contador de programa es"))

        questionnaire.questions = questionList
        return questionnaire
    }


    /*INTELIGENCIA ARTIFICAL 80*/

    fun cuestionarioaIA1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Introducción a IA", "Introducción a Inteligencia artificial", "INTELIGENCIA ARTIFICAL", 1, "introducción")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Rama de la cienca informatica dedicada al desarrollo de agentes racionales no vivos", true))
        answers1.add(crearRespuesta("Rama de la cienca informatica dedicada al desarrollo de agentes racionales vivos", false))
        answers1.add(crearRespuesta("Cienca informatica que evita  el desarrollo de agentes racionales no vivos", false))

        questionList.add(crearPregunta("", answers1, "¿Qué es la inteligencia artifical?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Division IA", "División de la inteligencia artificial", "INTELIGENCIA ARTIFICAL", 1, "división")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Inteligencia artifical convencial", true))
        answers1.add(crearRespuesta("Inteligencia computacional", true))
        answers1.add(crearRespuesta("Intelengia empirica", false))

        questionList.add(crearPregunta("", answers1, "¿La inteligencia artificial se divide en?"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("", " Aplicaciones y Técnicas", "INTELIGENCIA ARTIFICAL", 1, "aplicaciones, tecnicas")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Programacion extrema", false))
        answers1.add(crearRespuesta("Representacion del conocimiento", true))
        answers1.add(crearRespuesta("Redes neuronales", true))

        questionList.add(crearPregunta("", answers1, "Seleccione las tecnicas usadas en inteligencia artifical"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Fundamentos IA", "Fundamentos básicos", "INTELIGENCIA ARTIFICAL", 1, "fundamentos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Química", false))
        answers1.add(crearRespuesta("Filosfia", true))
        answers1.add(crearRespuesta("Matemáticas", true))

        questionList.add(crearPregunta("", answers1, "La inteligencia artifical se fundamente en "))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Aplicación IA", "Campos de Aplicación", "INTELIGENCIA ARTIFICAL", 1, "campos, aplicación")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Ninguna", false))
        answers1.add(crearRespuesta("Procesamiento de Lenguaje natural", true))
        answers1.add(crearRespuesta("Robotica", true))

        questionList.add(crearPregunta("", answers1, "La inteligencia artifical puede ser usada en"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Personajes IA", "Personajes influyentes", "INTELIGENCIA ARTIFICAL", 1, "personajes")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Tomas Edison", false))
        answers1.add(crearRespuesta("Alan Turing", true))
        answers1.add(crearRespuesta("Nicolas Tesla", false))

        questionList.add(crearPregunta("", answers1, "El padre de la inteligencia artificial es"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Objetivos IA", "Objetivos principales", "INTELIGENCIA ARTIFICAL", 1, "objetivos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Comprender y construir entidades no racionales", false))
        answers1.add(crearRespuesta("Comprender y construir entidades inteligentes. ", true))
        answers1.add(crearRespuesta("Hacer que las computadoras sean capaces de mostrar un comportamiento que sea considerado como inteligente por parte de un observador humano ", true))

        questionList.add(crearPregunta("", answers1, "Seleccione objetivos que tiene la inteligencia artifical"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Metodos utilizados en IA", "Métodos utilizados para el desarrollo y aprendizaje", "INTELIGENCIA ARTIFICAL", 1, "metodos,aprendizaje")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Distancia euclidiana", false))
        answers1.add(crearRespuesta("Maquina de vectores de soporte ", true))
        answers1.add(crearRespuesta("Redes neuronales", true))

        questionList.add(crearPregunta("", answers1, "Seleccione los métodos utlizados en inteligencia artifical "))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sistemas IA", "Categoria de sistemas", "INTELIGENCIA ARTIFICAL", 1, "categorias, sistemas")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Sistemas que actuan irracionalmente", false))
        answers1.add(crearRespuesta("Sistemas que actuan racionalmente", true))
        answers1.add(crearRespuesta("Sistemas que piensan racionalmente", true))

        questionList.add(crearPregunta("", answers1, "Seleccione las categorias de la inteligencia artifical "))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaIA10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Agentes IA", "Agentes inteligentes", "INTELIGENCIA ARTIFICAL", 1, "agentes, inteligentes")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Verdadero", true))
        answers1.add(crearRespuesta("Falso", false))

        questionList.add(crearPregunta("", answers1, "Un agente inteligente, es una entidad capaz de percibir su entorno, procesar tales percepciones y responder o actuar en su entorno de manera racional, es decir, de manera correcta y tendiendo a maximizar un resultado esperado. "))

        questionnaire.questions = questionList
        return questionnaire
    }

    /*COMPILADORES 90*/

    fun cuestionarioaC1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Conceptos", "Conceptos generales de compiladores", "COMPILADORES", 1, "Programa fuente, proceso de traducción.")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Reportar cualquier error en el programa fuente que detecte en el proceso de traducción.", true))
        answers1.add(crearRespuesta("Detectar la inconsistencia en el programa destino y reportarlo.", false))
        answers1.add(crearRespuesta("Trasformar los lexemas del programa destino en tokens.", false))

        questionList.add(crearPregunta("", answers1, "Cual es una función importante del compilador"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Análisis léxico", "Análisis léxico conceptos generales", "COMPILADORES", 1, "Tokens, autómata, lexema, analizador")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("El proceso de un analizador léxico en leer los caracteres de la entrada del programa fuente, agruparlos en lexemas y produce como salida una secuencia de tokens para cada lexema.", true))
        answers1.add(crearRespuesta("El proceso del analizador léxico en leer los tokens del programa, agruparlos en lexemas, separa los caracteres y produce una salida de tokens y lexemas.", false))

        questionList.add(crearPregunta("", answers1, "Qué proceso realiza un analizar léxico"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Análisis léxico", "Información de un lexema", "COMPILADORES", 1, "Tokens, tabla de símbolos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Tabla de símbolos", true))
        answers1.add(crearRespuesta("Tabla de caracteres", false))
        answers1.add(crearRespuesta("Tabla de tokens", false))

        questionList.add(crearPregunta("", answers1, "Para leer la información de un lexema el analizador léxico que interactúa."))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Análisis sintáctico", "Estructura jerarquica de instrucciones", "COMPILADORES", 1, "Gramática, árbol sintáctico.")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Gramática", true))
        answers1.add(crearRespuesta("Árbol sintáctico", false))
        answers1.add(crearRespuesta("Generador de código intermedio.", false))

        questionList.add(crearPregunta("", answers1, "Quien describe la estructura jerárquica de las instrucciones de un lenguaje de programación, en forma natural."))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Análisis semántico", "Utilizacion del arbol sintáctico y tabla de símbolos", "COMPILADORES", 1, "semántica, programa fuente, lenguaje.")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Comprobar la consistencia semántica del programa fuente con la definición del lenguaje.", true))
        answers1.add(crearRespuesta("Realizar comparaciones entre ellos y verificar la semántica del programa.", false))
        answers1.add(crearRespuesta("Comparar los tokens y en la tabla de símbolos.", false))

        questionList.add(crearPregunta("", answers1, "El analizador semántico utiliza el árbol sintáctico y la información en la tabla de símbolos para:"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("LLI", "Gramática libre de contexto (LLI)", "COMPILADORES", 1, "Ambigüedad, recursividad, árboles sintácticos.")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Ambigüedad", true))
        answers1.add(crearRespuesta("Recursividad", false))
        answers1.add(crearRespuesta("Gramática libre de contexto.", false))

        questionList.add(crearPregunta("", answers1, "Cuál es el nombre que se da, cuando a una cadena se puede asociar dos árboles sintácticos diferentes."))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Analizador sintáctico", "Predictivo dirigido por tabla", "COMPILADORES", 1, "Buffer de entrada, pila de análisis sintáctico, tabla de análisis sintáctico.")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Buffer de entrada, pila de análisis sintáctico, tabla de análisis sintáctico, salida", true))
        answers1.add(crearRespuesta("Tokens, gramática libre de contexto, tabla de análisis sintáctico.", false))
        answers1.add(crearRespuesta("Tokens, árbol sintáctico y tabla semántica.", false))

        questionList.add(crearPregunta("", answers1, "Cuál es el esquema de un analizador sintáctico descendente dirigido por tabla"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Generación de código intermedio", "Estructura del código", "COMPILADORES", 1, "Código intermedio, código fuente, código máquina.")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Gramática", false))
        answers1.add(crearRespuesta("Código intermedio", true))
        answers1.add(crearRespuesta("Traductor", false))

        questionList.add(crearPregunta("", answers1, "Estructura de código que posee una complejidad comprendida entre el código fuente y el código máquina."))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("", "Introducción a compiladores", "COMPILADORES", 1, "interprete, código fuente, instrucción, compilador")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("El intérprete traduce y ejecuta instrucción a instrucción el código fuente", true))
        answers1.add(crearRespuesta("El intérprete traduce de golpe el programa fuente y crea el fichero ejecutable", false))
        answers1.add(crearRespuesta("El compilador no genera el código ejecutable", false))

        questionList.add(crearPregunta("", answers1, "Señale la opción correcta con respecto a los intérpretes y compiladores"))

        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaC10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Estructura", "Estructura de un compilador", "COMPILADORES", 1, "léxico, sintáctico, semántico, generador de código intermedio.")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Analizador léxico, analizador sintáctico, analizador semántico, Generador de código intermedio, optimizador de código independiente de la máquina, Generador de código, Optimización de código independiente de la máquina.", true))
        answers1.add(crearRespuesta("Analizador léxico, tabla de símbolos, analizador sintáctico, árbol sintáctico, analizador semántico, generador de código intermedio.", false))
        answers1.add(crearRespuesta("Analizador léxico, analizador sintáctico, analizador semántico.", false))

        questionList.add(crearPregunta("", answers1, "Cuáles son las fases que comprenden un compilador"))

        questionnaire.questions = questionList
        return questionnaire
    }


    /*CONTROL AUTOMATIZADO 100*/


    fun cuestionarioaCA1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Representación de Funciones", "Diagramas de bloques", "CONTROL AUTOMATIZADO", 5, "diagrama, bloques, funciones")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Sirven para representar gráficamente las relaciones entre las variables de un sistema.", true))
        answers1.add(crearRespuesta("Eliminan los errores en el desarrollo de sistemas.", false))
        answers1.add(crearRespuesta("Nos permiten programar su lógica en otro entorno.", false))

        questionList.add(crearPregunta("", answers1, "¿Para qué sirven los diagramas de bloques?"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Bloques, flechas, bifurcaciones y sumadores.", true))
        answers2.add(crearRespuesta("Bloques, flechas, lanzas, bifurcaciones y sumadores.", false))
        answers2.add(crearRespuesta("Bloques, lanzas, bifurcaciones, sumadores, dirección y sentido.", false))

        questionList.add(crearPregunta("", answers2, "¿Qué elementos contiene un diagrama de bloques?"))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Verdadero", false))
        answers3.add(crearRespuesta("Falso", true))

        questionList.add(crearPregunta("", answers3, "La bifurcación solo puede ir a un solo bloque o sumador"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Verdadero", false))
        answers4.add(crearRespuesta("Falso", true))

        questionList.add(crearPregunta("", answers4, "No se puede simplificar funciones en un diagrama de bloques"))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Verdadero", false))
        answers5.add(crearRespuesta("Falso", true))

        questionList.add(crearPregunta("", answers5, "Los sumadores pueden realizar todo tipo de operaciones aritméticas"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Funciones de transferencia", "Funciones de transferencia en sistemas en tiempo discreto", "CONTROL AUTOMATIZADO", 5, "tiempo, discreto, funciones, transferencia")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Trenes de Pulso.", true))
        answers1.add(crearRespuesta("Retroalimentación", false))
        answers1.add(crearRespuesta("Ondas.", false))

        questionList.add(crearPregunta("", answers1, "¿Los sistema de control en tiempo discreto tienen características únicas en las que las señales son de forma de?"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Entrada A/D y Salida D/A", true))
        answers2.add(crearRespuesta("Entradas D/A y Salidas A/D", false))
        answers2.add(crearRespuesta("Entrada A y Salida D", false))

        questionList.add(crearPregunta("", answers2, "¿La interfaz del mundo analógico se hace a través de conversores de?"))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Entrada constante de muestreo.", false))
        answers3.add(crearRespuesta("Salida constante entre instante de muestreo.", true))
        answers3.add(crearRespuesta("Función de pulsos", true))

        questionList.add(crearPregunta("", answers3, "¿Qué mantiene la función de transferencia del Reten de Orden Cero?"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Nodo padre", false))
        answers4.add(crearRespuesta("Nodo intermedio", false))
        answers4.add(crearRespuesta("Nodo sumidero.", true))
        answers4.add(crearRespuesta("Nodos mixto", true))

        questionList.add(crearPregunta("", answers4, "Dentro de un diagrama de flujos de señal podemos encontrar:"))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Transformada Z bilateral.", false))
        answers5.add(crearRespuesta("Transformada Z unilateral.", true))

        questionList.add(crearPregunta("", answers5, "Para funciones que arrancan en un determinado tiempo se utiliza la :"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Controladores PID", "Reglas de sintonía PID", "CONTROL AUTOMATIZADO", 5, "controlador pid")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Ganancia proporcional, tiempo integral y tiempo de derivación", true))
        answers1.add(crearRespuesta("Ganancia proporcional y tiempo integral", false))
        answers1.add(crearRespuesta("Tiempo integral y tiempo de derivación.", false))

        questionList.add(crearPregunta("", answers1, "¿Señales, los parámetros de las reglas de sintonía Ziegler-Nichols?"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Falso", true))
        answers2.add(crearRespuesta("Verdadero", false))

        questionList.add(crearPregunta("", answers2, "El método de oscilación o método de respuesta en frecuencia es válido solo para plantas estables de lazo cerrado."))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Falso", false))
        answers3.add(crearRespuesta("Verdadero", true))

        questionList.add(crearPregunta("", answers3, "Ziegler y Nichols propusieron una serie de reglas para afinar controladores PID con base a una respuesta experimental."))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Error calculado entre la entrada del controlador menos la ganancia obtenida", false))
        answers4.add(crearRespuesta("Error calculado entre el tiempo integral menos la salida obtenida", false))
        answers4.add(crearRespuesta("Error calculado entre la salida deseada menos la salida obtenida", true))

        questionList.add(crearPregunta("", answers4, "¿Qué parámetro ingresa a un sistema PID?"))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Cuando una función continua pasa de una ganancia mínima a la máxima", false))
        answers5.add(crearRespuesta("Cuando una función continua pasa de un tipo de concavidad a otra", true))

        questionList.add(crearPregunta("", answers5, "¿Qué es un punto de inflexión en la curva?"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Funciones de transferencia", "Filtros en funciones de transferencia", "CONTROL AUTOMATIZADO", 5, "funciones, transferencia, filtros")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Un sistema que permite el paso de señales eléctricas a un rango de frecuencias determinadas e impide el paso del resto", true))
        answers1.add(crearRespuesta("Un sistema que impide el paso de señales eléctricas a un rango de frecuencias determinadas y permite el paso del resto.", false))

        questionList.add(crearPregunta("", answers1, "Un filtro es"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Rechaza señales de frecuencias superiores a una dada, denominada frecuencia de corte", true))
        answers2.add(crearRespuesta("Permite señales de frecuencias superiores a una dada, denominada frecuencia de corte", false))

        questionList.add(crearPregunta("", answers2, "Un filtro paso bajo"))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Señales de frecuencias superiores a la frecuencia de corte.", false))
        answers3.add(crearRespuesta("Señales de frecuencias inferiores a la frecuencia de corte.", true))

        questionList.add(crearPregunta("", answers3, "Un filtro paso alto rechaza:"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Paso Medios", false))
        answers4.add(crearRespuesta("Paso alto.", true))
        answers4.add(crearRespuesta("Paso bajo.", true))
        answers4.add(crearRespuesta("Paso banda..", true))

        questionList.add(crearPregunta("", answers4, "Los filtros en función de transferencia se clasifican en"))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Dos filtros paso bajo.", false))
        answers5.add(crearRespuesta("Un filtro paso bajo y un filtro paso alto.", true))

        questionList.add(crearPregunta("", answers5, "Un filtro paso banda puede ser creado colocando:"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Controladores PID", "Método de optimización computacional controladores PID", "CONTROL AUTOMATIZADO", 5, "métodos, computacional, controlar pid")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Obtener un conjunto óptimo de valores de los controladores PID", true))
        answers1.add(crearRespuesta("Eliminar un conjunto óptimo de valores de los controladores PID.", false))
        answers1.add(crearRespuesta("Optimizar el conjunto de valores de los controladores PID.", false))

        questionList.add(crearPregunta("", answers1, "El método de optimización computacional permite:"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Rechaza señales de frecuencias superiores a una dada, denominada frecuencia de corte", true))
        answers2.add(crearRespuesta("Permite señales de frecuencias superiores a una dada, denominada frecuencia de corte", false))

        questionList.add(crearPregunta("", answers2, "Un filtro paso bajo"))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Encontrar una combinación de K y a para que la sobreelongación sea menor de 10%", true))
        answers3.add(crearRespuesta("Encontrar una combinación de K y a para que la sobreelongación sea menor de 15%", false))

        questionList.add(crearPregunta("", answers3, "El conjunto óptimo de los controladores PID permite:"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Es cuando al variar unas de las características o condiciones del sistemas, hace que varíe considerablemente sus condiciones finales con respecto al tiempo.", true))
        answers4.add(crearRespuesta("Es una magnitud que expresa la relación entre una señal de salida, con respecto a la señal de entrada.", false))

        questionList.add(crearPregunta("", answers4, "Inestabilidad es:"))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Verdadero", false))
        answers5.add(crearRespuesta("Falso", true))

        questionList.add(crearPregunta("", answers5, "Los resultados obtenidos en el método de optimización computacional de controladores PID son iguales a los resultados que se obtiene al ser implementados "))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA6(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Controladores PID", "Modificaciones a los esquemas de control PID", "CONTROL AUTOMATIZADO", 5, "esquemas, controlador pid")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("La salida del sistema no se ve afectada por errores en medición", true))
        answers1.add(crearRespuesta("El sistema regula el valor de medición para que no afecta a la señal de salida", false))
        answers1.add(crearRespuesta("Los errores afectan a la salida del sistema", false))

        questionList.add(crearPregunta("", answers1, "¿En la medición qué sucede cuando el sistema no está retroalimentado?"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Moviendo los controles proporcional y derivativo (PD) al camino de realimentación.", true))
        answers2.add(crearRespuesta("Moviendo los controles derivativo e integral (DI) al camino de realimentación.", false))

        questionList.add(crearPregunta("", answers2, "El control I – PD se obtiene:"))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Moviendo el control derivativo (D) al camino de realimentación.", true))
        answers3.add(crearRespuesta("Moviendo el integrador (I) al camino de realimentación.", false))

        questionList.add(crearPregunta("", answers3, "El control PI – D se obtiene:"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Falso", true))
        answers4.add(crearRespuesta("Verdadero", false))

        questionList.add(crearPregunta("", answers4, "Una perturbación interna constituye una entrada para el sistema."))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Verdadero", false))
        answers5.add(crearRespuesta("Falso", true))

        questionList.add(crearPregunta("", answers5, "Una perturbación es una señal que tiende a afectar de forma positiva el valor de la salida de un sistema."))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("", "Control con 2 grados de libertad", "CONTROL AUTOMATIZADO", 5, "2 grados, libertad")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Al menos 2 de las funciones de transferencia tienen que ser independientes", true))
        answers1.add(crearRespuesta("Al menos 3 de las funciones de transferencia tienen que ser independientes", false))
        answers1.add(crearRespuesta("Todas las funciones de transferencia tienen que ser independientes", false))

        questionList.add(crearPregunta("", answers1, "En un controlador con dos grados de libertad"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Señal de entrada Ruido.", true))
        answers2.add(crearRespuesta("Señal de entrada distorsionada", false))

        questionList.add(crearPregunta("", answers2, "En un control con 2 grados de libertad se inyecta :"))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Realimentación", true))
        answers3.add(crearRespuesta("Reutilización", false))

        questionList.add(crearPregunta("", answers3, "Control con 2 grados de libertad utiliza:"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("La planta es la acción que realiza", true))
        answers4.add(crearRespuesta("La planta es la función que realiza", false))

        questionList.add(crearPregunta("", answers4, "En un sistema de control con 2 grados de libertad"))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Pasado(P), Presente(I), Futuro(D)", false))
        answers5.add(crearRespuesta("Presente(P), Pasado(I), Futuro(D)", true))

        questionList.add(crearPregunta("", answers5, "En un controlador PID cada letra representa un error en:"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Funciones de transferencia", "Respuesta al impulso y función de transferencia de sistemas lineales.", "CONTROL AUTOMATIZADO", 5, "impulso, funcion, transferencia, sistemas lineales")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Relaciones Entrada Salida", true))
        answers1.add(crearRespuesta("Relaciones entre funciones", false))
        answers1.add(crearRespuesta("Relaciones entre bloques", false))

        questionList.add(crearPregunta("", answers1, "¿Qué representa la Función de Transferencia?"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Dos Transformadas de Laplace.", true))
        answers2.add(crearRespuesta("Una Transformada de la Laplace y una Ecuación Diferencial.", false))

        questionList.add(crearPregunta("", answers2, "La función de transferencia de la Respuesta de Impulso es producto de:"))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Falso", true))
        answers3.add(crearRespuesta("Verdadero", false))

        questionList.add(crearPregunta("", answers3, "La Respuesta al Impulso está definida para sistemas no lineales"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Mediante Funciones de Transferencia.", true))
        answers4.add(crearRespuesta("Mediante Diagramas de Bloques.", false))

        questionList.add(crearPregunta("", answers4, "¿Cual es la forma clásica de modelar sistemas lineales?"))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Falso", false))
        answers5.add(crearRespuesta("Verdadero", true))

        questionList.add(crearPregunta("", answers5, "La Función de transferencia se define como el cociente entre la transformada de laplace de la señal de salida Y(s) y la transformada de laplace de la señal de entrada U(s)"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Polos y Ceros", "Asignacion de ceros en Polos y Ceros", "CONTROL AUTOMATIZADO", 5, "ceros, polos")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Estable", true))
        answers1.add(crearRespuesta("Positivo", false))
        answers1.add(crearRespuesta("Rápido", true))

        questionList.add(crearPregunta("", answers1, "Los ceros en la cadena abierta hacen que el sistema se vuelva más:"))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("El intervalo de tiempo en darse la máxima amplitud de salida.", true))
        answers2.add(crearRespuesta("alor de pico máximo de la salida ponderado con el valor final", false))

        questionList.add(crearPregunta("", answers2, "El tiempo de pico se define como:"))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Falso", true))
        answers3.add(crearRespuesta("Verdadero", false))

        questionList.add(crearPregunta("", answers3, "¿El tiempo de pico se referiere al tiempo que transcure el sistema hasta que logra estabilizarse?"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Verdadero", true))
        answers4.add(crearRespuesta("Falso", false))

        questionList.add(crearPregunta("", answers4, "Los ceros se los calcula del numerador y los polos de denominador."))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Falso", true))
        answers5.add(crearRespuesta("Verdadero", false))

        questionList.add(crearPregunta("", answers5, "¿La respuesta de la adición de un cero en serie, será con una mayor sobreoscilación y con un aumento en el tiempo de pico.?"))


        questionnaire.questions = questionList
        return questionnaire
    }

    fun cuestionarioaCA10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Representación de Funciones", "Graficas de Flujo de Señales", "CONTROL AUTOMATIZADO", 5, "gráficas, flujo, señales")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Un diagrama de flujo de señal es larepresentación de un conjunto de ecuaciones algebraicas lineales simultáneas, que consiste en una red donde los nodos están conectados por ramas con dirección y sentido.", true))
        answers1.add(crearRespuesta("Un diagrama de flujo de señal es la representación de un conjunto de ecuaciones algebraicas no lineales, que consiste en una red donde los nodos están conectados por ramas con dirección y sentido.", false))

        questionList.add(crearPregunta("", answers1, "Seleccione la definición correcta de diagrama de señales de flujo."))

        /* 2da pregunta*/
        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("En sistemas complejos.", true))
        answers2.add(crearRespuesta("En sistemas sencillos.", false))
        answers2.add(crearRespuesta("En cualquier sistema", false))

        questionList.add(crearPregunta("", answers2, "Cuando es recomendable el uso de diagramas de flujo de señal."))


        /* 3da pregunta*/
        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Rectángulos", false))
        answers3.add(crearRespuesta("Ramas", true))
        answers3.add(crearRespuesta("Nodos", true))

        questionList.add(crearPregunta("", answers3, "Para realizar un diagrama de señales de flujo se utiliza:"))


        /* 4da pregunta*/
        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Nodos mixto", true))
        answers4.add(crearRespuesta("Nodo sumidero.", true))
        answers4.add(crearRespuesta("Nodo padre", false))
        answers4.add(crearRespuesta("Nodos intermedio", false))

        questionList.add(crearPregunta("", answers4, "Dentro de un diagrama de flujos de señal podemos encontrar:"))


        /* 5da pregunta*/
        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Las propiedades del álgebra de bloques, no pueden ser aplicadas en los diagramas de señal de flujo.", false))
        answers5.add(crearRespuesta("Las propiedades del álgebra de bloques, también pueden ser aplicadas en los diagramas de señal de flujo.", true))

        questionList.add(crearPregunta("", answers5, "Seleccione la afirmación correcta."))


        questionnaire.questions = questionList
        return questionnaire
    }


    /*
    val answers = ArrayList<Answer>()
    answers.add(crearRespuesta("", false))
    answers.add(crearRespuesta("", false))
    answers.add(crearRespuesta("", false))
    answers.add(crearRespuesta("", false))

    questionList.add(crearPregunta("", answers, ""))
    */

    fun crearCuesitonarioExpresionOral() {
        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Plan de contigencia integral", "Examen de plan de contigencia para aprobación de expresión oral y escrita UNL", "EXPRESION ORAL Y ESCRITA", 50, "contingencia, examen, expresion oral y escrita")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Doble", true))
        answers1.add(crearRespuesta("Dos", false))
        answers1.add(crearRespuesta("Mitad", false))
        answers1.add(crearRespuesta("Cuarto", false))

        questionList.add(crearPregunta("", answers1, "El numero multiplicativo es"))


        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Prohibir", false))
        answers2.add(crearRespuesta("Musicomanía ", true))
        answers2.add(crearRespuesta("Cariamanga", false))
        answers2.add(crearRespuesta("Manutención", false))

        questionList.add(crearPregunta("", answers2, "La palabra que contiene hiato es"))

        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Yo tampoco lo sé, ¿y tu?", false))
        answers3.add(crearRespuesta("¿Quién es el que hace bulla?", true))
        answers3.add(crearRespuesta("Es tarde para empezar desde cero", false))
        answers3.add(crearRespuesta("No se que es la tilde enfática", false))

        questionList.add(crearPregunta("", answers3, "La oración donde se ha empleado la tilde enfática es"))


        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Lloviendo", false))
        answers4.add(crearRespuesta("Chiminea", true))
        answers4.add(crearRespuesta("Escarbar", false))
        answers4.add(crearRespuesta("Pifian", false))

        questionList.add(crearPregunta("", answers4, "La palabra que esta mal escrita es"))


        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("XVIII", false))
        answers5.add(crearRespuesta("MLCI", false))
        answers5.add(crearRespuesta("MCLVIII", true))
        answers5.add(crearRespuesta("MCIVIII", false))

        questionList.add(crearPregunta("", answers5, "El número romano que representa 1158 es"))

        val answers6 = ArrayList<Answer>()
        answers6.add(crearRespuesta("Fonema", false))
        answers6.add(crearRespuesta("Consonante", false))
        answers6.add(crearRespuesta("Sílaba", true))
        answers6.add(crearRespuesta("Vocal", false))

        questionList.add(crearPregunta("", answers6, "La frase: \"Es el sonido o grupo de sonidos que se pronuncian con una sola emisión de voz en la cadena hablada\" de Guerro(2011) hace referencia al concepto de"))


        val answers7 = ArrayList<Answer>()
        answers7.add(crearRespuesta("Cuestionable", false))
        answers7.add(crearRespuesta("Rehusar", false))
        answers7.add(crearRespuesta("Cuautemoc", true))
        answers7.add(crearRespuesta("Mantequilla", false))

        questionList.add(crearPregunta("", answers7, "La palabra que contiene triptongo es"))


        val answers8 = ArrayList<Answer>()
        answers8.add(crearRespuesta("Científica", false))
        answers8.add(crearRespuesta("Literaria", false))
        answers8.add(crearRespuesta("Topográfica ", true))
        answers8.add(crearRespuesta("Cinematográfica ", false))

        questionList.add(crearPregunta("", answers8, "Cuando se hace la descripción de una región, un paisaje o un lugar determinado se llama"))


        val answers9 = ArrayList<Answer>()
        answers9.add(crearRespuesta("Quiero que el escritorio; este aquí mañana mismo.", false))
        answers9.add(crearRespuesta("La fiesta sera de todos; los dias de la semana", false))
        answers9.add(crearRespuesta("Apresúrate; que llegamos tarde ", false))
        answers9.add(crearRespuesta("Atiéndeme, muchacha; los beneficios seran multiples ", true))

        questionList.add(crearPregunta("", answers9, "La oracion donde se ha empleado bien el punto y coma es "))


        val answers10 = ArrayList<Answer>()
        answers10.add(crearRespuesta("Juan obtuve 19,50 puntos", false))
        answers10.add(crearRespuesta("Aunque cansado, llegare en primer lugar", false))
        answers10.add(crearRespuesta("Hasta cuatro pollos me comería del hambre que tengo ", true))
        answers10.add(crearRespuesta("Vêndame un cuarto de pollo ", false))

        questionList.add(crearPregunta("", answers10, "La oración en la que se ha empleado números cardinales es"))


        val answers11 = ArrayList<Answer>()
        answers11.add(crearRespuesta("Dialecto", false))
        answers11.add(crearRespuesta("Símbolo", true))
        answers11.add(crearRespuesta("Ideas", false))
        answers11.add(crearRespuesta("Significante", false))

        questionList.add(crearPregunta("", answers11, " Cuando dos o más personas han logrado comunicarse mediante un signo determinado, ese signo o señal que se ha utilizado para comunicarse adquiere la categoria de"))

        val answers12 = ArrayList<Answer>()
        answers12.add(crearRespuesta("Seremos felices, ya lo verás", false))
        answers12.add(crearRespuesta("En Guayaquil, que es bastante caluroso, se llevará acabo el festival", true))
        answers12.add(crearRespuesta("Apresúrate, que llegamos tarde", false))
        answers12.add(crearRespuesta("Hubo varios bocados, canciones, folclor y vino", false))

        questionList.add(crearPregunta("", answers12, "La oración donde se ha empleado la coma para señalar el adjetivo explicativo es"))


        val answers13 = ArrayList<Answer>()
        answers13.add(crearRespuesta("Separar fechas cuando se indica un periodo determinado", false))
        answers13.add(crearRespuesta("Señalar dialogos", true))
        answers13.add(crearRespuesta("Señalar nombres", false))
        answers13.add(crearRespuesta("Separar las silabas de una palabra cuando no entra al final del reglon", false))

        questionList.add(crearPregunta("", answers13, "Utilizamos la raya para "))


        val answers14 = ArrayList<Answer>()
        answers14.add(crearRespuesta("El informe", false))
        answers14.add(crearRespuesta("La esquela", true))
        answers14.add(crearRespuesta("El acta", false))
        answers14.add(crearRespuesta("El memorando", false))

        questionList.add(crearPregunta("", answers14, "El escrito breve sirve para invitar al destinatario a actos organizados por una institución o por personas particulares es"))

        val answers15 = ArrayList<Answer>()
        answers15.add(crearRespuesta("Volverá enojado, pero no importa", false))
        answers15.add(crearRespuesta("A mí me dijieron que el examen no era de esa manera", true))
        answers15.add(crearRespuesta("Los años pasaran muy lento", false))
        answers15.add(crearRespuesta("El examen de Empresión Oral y Escrita sera de carácter objetivo ", false))

        questionList.add(crearPregunta("", answers15, "La oracion en la que se ha utilizado la tilde diátrica es"))


        val answers16 = ArrayList<Answer>()
        answers16.add(crearRespuesta("Tilde diacrítica ", false))
        answers16.add(crearRespuesta("Acento prósodico", true))
        answers16.add(crearRespuesta("Acento ortográfico", false))
        answers16.add(crearRespuesta("Tilde enfática", false))

        questionList.add(crearPregunta("", answers16, "El concepto \"no se da en la grafía  sino solo en la pronunciación, es decir a nivel fonético, en cuanto pronunciamos con la mayor intensidad una sílaba de una palabra determinada\" corresponde a  "))


        val answers17 = ArrayList<Answer>()
        answers17.add(crearRespuesta("Comenta el contenido de un escrito", false))
        answers17.add(crearRespuesta("Extrae las ideas principales de un texto", true))
        answers17.add(crearRespuesta("Va acompañado con una introducción en la que se hace referencia sobre el asunto del informe", false))
        answers17.add(crearRespuesta("Es idéntico a la reseña", false))

        questionList.add(crearPregunta("", answers17, "El resumen "))

        val answers18 = ArrayList<Answer>()
        answers18.add(crearRespuesta("Libro, biblioteca", false))
        answers18.add(crearRespuesta("Marrano, chancho", false))
        answers18.add(crearRespuesta("Banco, banco", false))
        answers18.add(crearRespuesta("Novel, nobel", true))

        questionList.add(crearPregunta("", answers18, "El literal donde se encuentra palabras homófonas es "))


        val answers19 = ArrayList<Answer>()
        answers19.add(crearRespuesta("Metalingüística.", false))
        answers19.add(crearRespuesta("Estética o poética.", false))
        answers19.add(crearRespuesta("Conativa o apelativa.", false))
        answers19.add(crearRespuesta("Fática o de contacto.", true))

        questionList.add(crearPregunta("", answers19, "¿Me escuchas? ¿Estás seguro? Habla más fuerte. ¿Qué dijiste? Son frases de las que se ocupa la función"))


        val answers20 = ArrayList<Answer>()
        answers20.add(crearRespuesta("Cántaro, cantaro", false))
        answers20.add(crearRespuesta("Manzanilla, mesanine", false))
        answers20.add(crearRespuesta("Biósfera, bioesfera.", false))
        answers20.add(crearRespuesta("Rácimo, racimo.", true))

        questionList.add(crearPregunta("", answers20, "El literal donde se encuentran palabras de doble acentuación es"))

        val answers21 = ArrayList<Answer>()
        answers21.add(crearRespuesta("Miserable", false))
        answers21.add(crearRespuesta("Payaso", false))
        answers21.add(crearRespuesta("Risa", false))
        answers21.add(crearRespuesta("Llanto", true))

        questionList.add(crearPregunta("", answers21, "Feliz : sonrisa :: triste: ?"))


        val answers22 = ArrayList<Answer>()
        answers22.add(crearRespuesta("Cocer, coser.", false))
        answers22.add(crearRespuesta("Sabia, savia.", false))
        answers22.add(crearRespuesta("Blandura, afabilidad.", true))
        answers22.add(crearRespuesta("Absorber, absolver.", false))

        questionList.add(crearPregunta("", answers22, "El literal donde se encuentran palabras sinónimas es:"))


        val answers23 = ArrayList<Answer>()
        answers23.add(crearRespuesta("Me concedieron el tercer lugar: no importa.", true))
        answers23.add(crearRespuesta("Dos de los 40 estudiantes aprobaron el ciclo.", false))
        answers23.add(crearRespuesta("En este mes se me cuadriplicaron las ganancias.", false))
        answers23.add(crearRespuesta("No sé si tengo cuatro o cinco dólares en mi bolsillo.", false))

        questionList.add(crearPregunta("", answers23, "La oración en la que se ha empleado números ordinales es"))

        val answers24 = ArrayList<Answer>()
        answers24.add(crearRespuesta("Panadería.", false))
        answers24.add(crearRespuesta("Maestro.", false))
        answers24.add(crearRespuesta("Caotizar.", false))
        answers24.add(crearRespuesta("Opcional.", true))

        questionList.add(crearPregunta("", answers24, "La palabra que contiene diptongo es"))

        val answers25 = ArrayList<Answer>()
        answers25.add(crearRespuesta("La cláusula compuesta.", false))
        answers25.add(crearRespuesta("El mundo.", true))
        answers25.add(crearRespuesta("Solamente las palabras.", false))
        answers25.add(crearRespuesta("Solo para memorizar.", false))

        questionList.add(crearPregunta("", answers25, "Cuando se lee un texto, se está leyendo"))


        val answers26 = ArrayList<Answer>()
        answers26.add(crearRespuesta("Miguel de Cervantes, el Manco de Lepanto, fue escritor y soldado.", true))
        answers26.add(crearRespuesta("Así será, ya lo verás.", false))
        answers26.add(crearRespuesta("Él se va de paseo; ella, de vacaciones.", false))
        answers26.add(crearRespuesta("Tráiganme las flores, por favor.", false))

        questionList.add(crearPregunta("", answers26, "La oración en la que hay la aposición, gracias al uso de la coma, es:"))

        val answers27 = ArrayList<Answer>()
        answers27.add(crearRespuesta("Canal", true))
        answers27.add(crearRespuesta("Mensaje.", false))
        answers27.add(crearRespuesta("Receptor.", false))
        answers27.add(crearRespuesta("Emisor.", false))

        questionList.add(crearPregunta("", answers27, "El vehículo o medio a través del cual se trasmite el mensaje o la comunicación se denomina"))

        val answers28 = ArrayList<Answer>()
        answers28.add(crearRespuesta("¿Será posible que se haya portado así!", false))
        answers28.add(crearRespuesta("?Qué quieres que haga si ella es así?", false))
        answers28.add(crearRespuesta("¿Cómo se llama el último libro que publicaste?", true))
        answers28.add(crearRespuesta("Cada vez es más difícil comprender?", false))

        questionList.add(crearPregunta("", answers28, "La oración en la que se ha empleado bien los signos de interrogación es"))

        val answers29 = ArrayList<Answer>()
        answers29.add(crearRespuesta("dichoso", false))
        answers29.add(crearRespuesta("enfadado", false))
        answers29.add(crearRespuesta("desconsolado", false))
        answers29.add(crearRespuesta("desdicha", true))

        questionList.add(crearPregunta("", answers29, "Felicidad : dicha :: tristeza: ?"))

        val answers30 = ArrayList<Answer>()
        answers30.add(crearRespuesta("El momento en que se presenta el desarrollo del máximo potencial cognitivo.", false))
        answers30.add(crearRespuesta("Las circunstancias y los factores que rodean un hecho.", true))
        answers30.add(crearRespuesta("Los hechos vinculados a la acción del sujeto de la oración.", false))
        answers30.add(crearRespuesta("Los verbos que componen la unidad mínima lingüística.", false))

        questionList.add(crearPregunta("", answers30, "Entendemos por contexto"))


        val answers31 = ArrayList<Answer>()
        answers31.add(crearRespuesta("Alacrán, santidad, maremoto camino, regreso, caso, Quito, Macará, Santillana.", false))
        answers31.add(crearRespuesta("Hermandad, psicólogo, tono, pintó, carácter, muchedumbre.", true))
        answers31.add(crearRespuesta("Piano, cuarto, siembra, pueblo, cuota, andamio.", false))
        answers31.add(crearRespuesta("Alejandro, cuencano, guayaquileño, cusqueño, Paraná, universo, maní, Paraguay.", false))

        questionList.add(crearPregunta("", answers31, "El grupo de palabras donde hay dos agudas, tres graves y una esdrújula es"))


        val answers32 = ArrayList<Answer>()
        answers32.add(crearRespuesta("Tilde enfática", false))
        answers32.add(crearRespuesta("Tilde diacrítica", true))
        answers32.add(crearRespuesta("Acento prosódico", false))
        answers32.add(crearRespuesta("Acento ortográfico", false))

        questionList.add(crearPregunta("", answers32, "El concepto “se usa en el caso de los monosílabos que tienen igual forma, pero funciones gramaticales distintas”, corresponde a:"))


        val answers33 = ArrayList<Answer>()
        answers33.add(crearRespuesta("Las aes.", true))
        answers33.add(crearRespuesta("Las as.", false))
        answers33.add(crearRespuesta("Las ases.", false))
        answers33.add(crearRespuesta("De las tres formas.", false))

        questionList.add(crearPregunta("", answers33, "Para la “a”, el plural correcto es:"))


        val answers34 = ArrayList<Answer>()
        answers34.add(crearRespuesta("Del gruñido a la palabra.", true))
        answers34.add(crearRespuesta("De su especie a la raza.", false))
        answers34.add(crearRespuesta("Del verbo a la prosa.", false))
        answers34.add(crearRespuesta("De Mono a persona.", false))

        questionList.add(crearPregunta("", answers34, "Gracias a su cerebro y a su aparato fonador, el hombre es el único ser exclusivo que, en la larga cadena de la historia humana, ha logrado pasar:"))


        val answers35 = ArrayList<Answer>()
        answers35.add(crearRespuesta("Se escribe desde una actitud experiencial.", true))
        answers35.add(crearRespuesta("Es una obra de consulta donde encontramos datos puntuales.", false))
        answers35.add(crearRespuesta("Es propio de la ponderación y la exaltación en el buen sentido de la palabra.", false))
        answers35.add(crearRespuesta("Se basa exclusivamente en las ciencias experimentales.", false))

        questionList.add(crearPregunta("", answers35, "El ensayo:"))


        val answers36 = ArrayList<Answer>()
        answers36.add(crearRespuesta("Habla, idiolecto, uso", false))
        answers36.add(crearRespuesta("Forma, habla, idiolecto", false))
        answers36.add(crearRespuesta("Lengua, habla, idiolecto", false))
        answers36.add(crearRespuesta("Forma, contenido y uso.", true))

        questionList.add(crearPregunta("", answers36, "Según Horcas (2009), el lenguaje, como sistema simbólico implica tres dimensiones:"))


        val answers37 = ArrayList<Answer>()
        answers37.add(crearRespuesta("Significa que está muy motivado para seguir leyendo.", false))
        answers37.add(crearRespuesta("Cultiva la humildad y la realidad propia.", false))
        answers37.add(crearRespuesta("Está evidenciando una actitud muy empobrecedora.", true))
        answers37.add(crearRespuesta("Lo está enriqueciendo.", false))

        questionList.add(crearPregunta("", answers37, "Cuando el lector clausura el sentido del texto:"))


        val answers38 = ArrayList<Answer>()
        answers38.add(crearRespuesta("Azul, abril, asistir, Samuel, pared, compré, manantial.", true))
        answers38.add(crearRespuesta("Fotógrafo, análisis, cámara, cántaro, plátano, radiólogo.", false))
        answers38.add(crearRespuesta("Grato, gema, claro, absorto, lento, flauta", false))
        answers38.add(crearRespuesta("Grupo, calabaza, manaza, cárcel, carácter, campo, césped, mayo.", false))

        questionList.add(crearPregunta("", answers38, "El literal donde se encuentran las palabras agudas es:"))

        val answers39 = ArrayList<Answer>()
        answers39.add(crearRespuesta("Tubería.", true))
        answers39.add(crearRespuesta("Coloriar.", false))
        answers39.add(crearRespuesta("Adolorido.", false))
        answers39.add(crearRespuesta("Afusilar.", false))

        questionList.add(crearPregunta("", answers39, "La palabra que está escrita correctamente es:"))

        val answers40 = ArrayList<Answer>()
        answers40.add(crearRespuesta("Universidad Técnica Particular de Loja, Banco Central del Ecuador, Tame.", false))
        answers40.add(crearRespuesta("Álvaro, Ángel, Édgar, Wálter, Perú, Argentina.", false))
        answers40.add(crearRespuesta("Centinela del Sur, Luz de América, Atenas del Ecuador", false))
        answers40.add(crearRespuesta("País, república, patria, cantón, provincia, océano, parque, libertad.", true))

        questionList.add(crearPregunta("", answers40, "Las palabras que deben escribirse siempre con minúscula inicial, siempre que no sea inicial de frase u oración, son"))

        val answers41 = ArrayList<Answer>()
        answers41.add(crearRespuesta("Árbol sin ramas ni flores.", false))
        answers41.add(crearRespuesta("Cada rama en cada árbol.", false))
        answers41.add(crearRespuesta("Ese árbol tiene ramas y flores.", true))
        answers41.add(crearRespuesta("Ramas y flores del árbol.", false))

        questionList.add(crearPregunta("", answers41, "La frase de construcción lógica es:"))

        val answers42 = ArrayList<Answer>()
        answers42.add(crearRespuesta("Edad", false))
        answers42.add(crearRespuesta("Salario", false))
        answers42.add(crearRespuesta("Siglo", true))
        answers42.add(crearRespuesta("Día", false))

        questionList.add(crearPregunta("", answers42, "Centavo : dólar :: año: ?"))

        val answers43 = ArrayList<Answer>()
        answers43.add(crearRespuesta("Las esquelas.", false))
        answers43.add(crearRespuesta("Las cartas invitación.", false))
        answers43.add(crearRespuesta("Los informes.", true))
        answers43.add(crearRespuesta("Las solicitudes.", false))

        questionList.add(crearPregunta("", answers43, "Los escritos que se redactan de la manera más concreta posible y con la mayor veracidad y claridad son:"))

        val answers44 = ArrayList<Answer>()
        answers44.add(crearRespuesta("La descripción.", false))
        answers44.add(crearRespuesta("La narración.", true))
        answers44.add(crearRespuesta("El ambiente.", false))
        answers44.add(crearRespuesta("El ensayo.", false))

        questionList.add(crearPregunta("", answers44, "La acción y los caracteres son elementos que pertenecen a"))


        val answers45 = ArrayList<Answer>()
        answers45.add(crearRespuesta("Alcalde", false))
        answers45.add(crearRespuesta("Estado", true))
        answers45.add(crearRespuesta("Ciudad", false))
        answers45.add(crearRespuesta("Gente", false))

        questionList.add(crearPregunta("", answers45, "Presidente : nación :: gobernador: ?"))


        val answers46 = ArrayList<Answer>()
        answers46.add(crearRespuesta("Tilde enfática", true))
        answers46.add(crearRespuesta("Tilde diacrítica", false))
        answers46.add(crearRespuesta("Acento prosódico", false))
        answers46.add(crearRespuesta("Acento ortográfico", false))

        questionList.add(crearPregunta("", answers46, "El concepto “para resaltar la interrogación o admiración con que son expresadas, de conformidad con el ánimo y la actitud de quien las pronuncia”, corresponde a:"))


        val answers47 = ArrayList<Answer>()
        answers47.add(crearRespuesta("Monosílaba", false))
        answers47.add(crearRespuesta("Bisílaba", false))
        answers47.add(crearRespuesta("Trisílaba", false))
        answers47.add(crearRespuesta("Tetrasílaba", true))

        questionList.add(crearPregunta("", answers47, "Atlántico se clasifica como una palabra:"))


        val answers48 = ArrayList<Answer>()
        answers48.add(crearRespuesta("Dijo que lo haría así tú no quieras. ¿Verdad?.", false))
        answers48.add(crearRespuesta("La misa será a las cuatro de la tarde en la iglesia Matriz.", true))
        answers48.add(crearRespuesta("Lo haré así tú no lo quieras. Pero ven.", false))
        answers48.add(crearRespuesta("Espérame a las tres de la tarde.", false))

        questionList.add(crearPregunta("", answers48, "El ejemplo de estilo informativo es"))


        val answers49 = ArrayList<Answer>()
        answers49.add(crearRespuesta("Le dieron una placa de reconocimiento.", false))
        answers49.add(crearRespuesta("Prácticamente hemos terminado.", true))
        answers49.add(crearRespuesta("Se irá a Ambato.", false))
        answers49.add(crearRespuesta("Espérame a las tres de la tarde.", false))

        questionList.add(crearPregunta("", answers49, "La expresión donde hay cacofonía es"))


        val answers50 = ArrayList<Answer>()
        answers50.add(crearRespuesta("Ruido.", true))
        answers50.add(crearRespuesta("Receptor.", false))
        answers50.add(crearRespuesta("Código.", false))
        answers50.add(crearRespuesta("Canal.", false))

        questionList.add(crearPregunta("", answers50, "El elemento que está dado por las interferencias ajenas al mensaje y que estropea el proceso de la comunicación se denomina:"))

        val batch = db.batch()

        val cuestionarioRef = db.collection(QUESTIONNAIRE_PATH).document()
        batch.set(cuestionarioRef, questionnaire.toMapAux())

        questionList.forEach {

            val anwersList = ArrayList<Map<String, Any>>()

            it.answers.forEach { a ->
                anwersList.add(a.toMapPost())
            }
            it.hashAnswers = anwersList

            batch.set(db.collection(QUESTIONNAIRE_PATH).document(cuestionarioRef.id).collection(QUESTIONS_PATH).document(), it.toMapPost())
        }

        batch.commit().addOnSuccessListener {
            Log.e(TAG, "Cuestionario subido expresion subido correctamente")
        }.addOnFailureListener {
            Log.e(TAG, it.toString())
        }
    }

    fun crearCuestionarioMetodologiaInvestigación(){
        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Prueba 3 ciclo sistemas", "", "METODOLOGÍA DE LA INVESTIGACIÓN", 29, "metodología, investigación")

        /* Primera pregunta*/
        val answers1 = ArrayList<Answer>()
        answers1.add(crearRespuesta("Materiales escritos", true))
        answers1.add(crearRespuesta("Materiales audiovisuales", false))
        answers1.add(crearRespuesta("Programas de radio o televisión", false))
        answers1.add(crearRespuesta("Información disponible en internet", false))
        answers1.add(crearRespuesta("Todas las anteriores", true))
        answers1.add(crearRespuesta("Ninguna de las anteriores", false))

        questionList.add(crearPregunta("", answers1, "Cuáles son las fuentes de ideas para la investigación"))

        val answers2 = ArrayList<Answer>()
        answers2.add(crearRespuesta("Es necesario no adentrarse en algún tema que ya ha sido estudiado muy afondo.", true))
        answers2.add(crearRespuesta("El tema que se va a abordar debe ser confuso y no estar estructurado para eso es necesario consultar varias fuentes bibliográficas.", false))
        answers2.add(crearRespuesta("Estructurar la idea de investigación de manera formal", true))
        answers2.add(crearRespuesta("Hay que adentrarse en algún tema que ya ha sido estudiado muy afondo.", false))
        answers2.add(crearRespuesta("Elegir la perspectiva principal en la cual se abordará la idea de investigación.", true))

        questionList.add(crearPregunta("", answers2, "Cuáles son los antecedentes que se deben tomar en cuenta antes de realizar una investigación."))

        val answers3 = ArrayList<Answer>()
        answers3.add(crearRespuesta("Objetivos de la investigación.", false))
        answers3.add(crearRespuesta("Pregunta de investigación", false))
        answers3.add(crearRespuesta("Justificación de la investigación", false))
        answers3.add(crearRespuesta("Evaluación de las deficiencias en el conocimiento del problema", false))
        answers3.add(crearRespuesta("Todas las anteriores", true))

        questionList.add(crearPregunta("", answers3, "Los elementos del planteamiento cuantitativo del problema:"))

        val answers4 = ArrayList<Answer>()
        answers4.add(crearRespuesta("Delimitar el problema.", false))
        answers4.add(crearRespuesta("Viabilidad de la investigación", false))
        answers4.add(crearRespuesta("Relación entre variables.", true))
        answers4.add(crearRespuesta("Formular como pregunta", true))
        answers4.add(crearRespuesta("Tratar un problema medible u observable.", true))

        questionList.add(crearPregunta("", answers4, "Criterios del planteamiento del problema cuantitativo:"))


        val answers5 = ArrayList<Answer>()
        answers5.add(crearRespuesta("Implicaciones Práctica", false))
        answers5.add(crearRespuesta("Delimitar el problema .", false))
        answers5.add(crearRespuesta("Factibilidad de la investigación", false))
        answers5.add(crearRespuesta("Viabilidad del estudio que implica.", true))
        answers5.add(crearRespuesta("Relación entre variables", false))

        questionList.add(crearPregunta("", answers5, "¿Cuál de las siguientes opciones es un elemento del problema cuantitativo?"))

        val answers6 = ArrayList<Answer>()
        answers6.add(crearRespuesta("Que no se conozcan las respuestas", true))
        answers6.add(crearRespuesta("Que implique usar cualquier medio", false))
        answers6.add(crearRespuesta("Que impliquen usar medios éticos.", true))
        answers6.add(crearRespuesta("Que sean claras.", true))
        answers6.add(crearRespuesta("Que el conocimiento obtenido sea común.", false))

        questionList.add(crearPregunta("", answers6, "¿Cuáles son los requisitos para las preguntas de investigación?"))

        val answers7 = ArrayList<Answer>()
        answers7.add(crearRespuesta("Es el capítulo del trabajo en el cual se encuentran los antecedentes y las bases teóricas o la fundamentación teórica.", false))
        answers7.add(crearRespuesta("Es exponer todas las razones, las cuales nos parezcan de importancia y nos motiven a realizar una Investigación", true))
        answers7.add(crearRespuesta("Representan las acciones concretas que el investigador llevará a cabo para intentar responder a las preguntas de investigación y así resolver el problema de investigación", false))
        answers7.add(crearRespuesta("Es la aspiración, el propósito, que presupone el objeto transformado, la situación propia del problema superado.", false))

        questionList.add(crearPregunta("", answers7, "Definición de Justificación."))


        val answers8 = ArrayList<Answer>()
        answers8.add(crearRespuesta("Conveniencia", false))
        answers8.add(crearRespuesta("Intereses", true))
        answers8.add(crearRespuesta("Valor Teórico", false))
        answers8.add(crearRespuesta("Relevancia Social", false))

        questionList.add(crearPregunta("", answers8, "Razones o motivos por las cuales se procedió a la investigación:"))


        val answers9 = ArrayList<Answer>()
        answers9.add(crearRespuesta("Conveniencia", false))
        answers9.add(crearRespuesta("Relevancia Social", false))
        answers9.add(crearRespuesta("Implicaciones Prácticas", false))
        answers9.add(crearRespuesta("Valor Teórico", false))
        answers9.add(crearRespuesta("Utilidad Metodológica", true))


        questionList.add(crearPregunta("", answers9, "Que contribución o que aportación tendría nuestra investigación hacia otras áreas del conocimiento, tendría alguna importancia trascendental, los resultados podrán ser aplicables a otros fenómenos o ayudaría a explicar o entenderlos."))

        val answers10 = ArrayList<Answer>()
        answers10.add(crearRespuesta("El objetivo es la aspiración, el propósito, que presupone el objeto transformado, la situación propia del problema superado.", false))
        answers10.add(crearRespuesta("El objetivo es la aspiración, el propósito, el resultado a alcanzar, el para qué se desarrolla la investigación, que presupone el objeto transformado, la situación propia del problema superado, como resultado del conocimiento del objeto de estudio que se investiga en el Proceso", false))
        answers10.add(crearRespuesta("Los objetivos son las guías de estudio que durante todo el desarrollo del mismo deben tenerse presente", true))
        answers10.add(crearRespuesta("Objetivo es la categoría que refleja el propósito o intencionalidad de la investigación (el para que), lo que debe lograrse, de modo que se transforme el objeto y se solucione el problema", false))
        answers10.add(crearRespuesta("Ninguna de las anteriores", false))

        questionList.add(crearPregunta("", answers10, "Definición de Objetivos según Sampieri"))

        val answers11 = ArrayList<Answer>()
        answers11.add(crearRespuesta("Terminal", true))
        answers11.add(crearRespuesta("Cualitativo", true))
        answers11.add(crearRespuesta("Específico", false))
        answers11.add(crearRespuesta("Cuantitativo", false))
        answers11.add(crearRespuesta("Integral", true))
        answers11.add(crearRespuesta("Ninguna de las anteriores", false))

        questionList.add(crearPregunta("", answers11, "Cuáles son los atributos del objetivo general?"))



        val answers12 = ArrayList<Answer>()
        answers12.add(crearRespuesta("Integral", false))
        answers12.add(crearRespuesta("Conductuales", true))
        answers12.add(crearRespuesta("Terminal", false))
        answers12.add(crearRespuesta("Cualitativos", true))
        answers12.add(crearRespuesta("Específicos", true))
        answers12.add(crearRespuesta("Ninguna de las anteriores", false))

        questionList.add(crearPregunta("", answers12, "Cuáles son los atributos del objetivo específico?"))



        val answers13 = ArrayList<Answer>()
        answers13.add(crearRespuesta("Es aquel que contiene pocas páginas.", false))
        answers13.add(crearRespuesta("Trata con profundidad únicamente los aspectos relacionados con el problema", false))
        answers13.add(crearRespuesta("Vincula de manera lógica los conceptos", false))
        answers13.add(crearRespuesta("Todas las anteriores", true))

        questionList.add(crearPregunta("", answers13, "¿Que comprende el marco teórico?"))



        val answers14 = ArrayList<Answer>()
        answers14.add(crearRespuesta("Su claridad y estructura dependen de que seleccionemos los términos adecuados, lo que a su vez se relaciona con un planteamiento enfocado.", false))
        answers14.add(crearRespuesta("En el problema de investigación que nos ocupa sin divagar en otros temas ajenos al estudio.", true))
        answers14.add(crearRespuesta("Debemos recurrir al mapa conceptual centrándonos en el problema de estudio.", false))
        answers14.add(crearRespuesta("Su estructura no dependen de que seleccionemos los términos adecuados, lo que a su vez se relaciona con un análisis propio.", false))

        questionList.add(crearPregunta("", answers14, "¿En que nos centramos al construir el marco teórico?"))


        val answers15 = ArrayList<Answer>()
        answers15.add(crearRespuesta("Que exista una teoría desarrollada que no se aplique a nuestro tema de investigación.", false))
        answers15.add(crearRespuesta("Que haya una sola teoría que se aplique al problema de investigación.", false))
        answers15.add(crearRespuesta("Que haya generalizaciones empíricas que se adapten a dicho problema.", true))
        answers15.add(crearRespuesta("Ninguna de las anteriores.", false))

        questionList.add(crearPregunta("", answers15, "La construcción de marco teórico depende de:"))

        val answers16 = ArrayList<Answer>()
        answers16.add(crearRespuesta("Problema, tema general, subtema, referencia.", false))
        answers16.add(crearRespuesta("Tema, subtema, bibliografía, hipótesis.", false))
        answers16.add(crearRespuesta("Tema general, título, subtema, problema.", false))
        answers16.add(crearRespuesta("Tema general, tema, subtema, referencia.", true))

        questionList.add(crearPregunta("", answers16, "Identifique el orden correcto del esquema vertebrado del marco teórico a partir de un índice."))

        val answers17 = ArrayList<Answer>()
        answers17.add(crearRespuesta("Problemática", false))
        answers17.add(crearRespuesta("Hipótesis", false))
        answers17.add(crearRespuesta("Resumen", false))
        answers17.add(crearRespuesta("Marco teórico", true))

        questionList.add(crearPregunta("", answers17, "A veces conocido como el capítulo II de una tesis, es el pilar fundamental de cualquier investigación. La teoría constituye la base donde se sustentará cualquier análisis, experimento o propuesta de desarrollo de un trabajo de grado."))


        val answers18 = ArrayList<Answer>()
        answers18.add(crearRespuesta("Operacional, Gráfica y Virtual.", false))
        answers18.add(crearRespuesta("Conceptual y Geográfica.", false))
        answers18.add(crearRespuesta("Conceptual y operacional.", true))
        answers18.add(crearRespuesta("Todas las anteriores", false))

        questionList.add(crearPregunta("", answers18, "En las hipótesis las variables deben ser definidas de forma:"))



        val answers19 = ArrayList<Answer>()
        answers19.add(crearRespuesta("Universo", true))
        answers19.add(crearRespuesta("Población", false))
        answers19.add(crearRespuesta("Muestra", false))
        answers19.add(crearRespuesta("Todas las anteriores", false))

        questionList.add(crearPregunta("", answers19, "Totalidad de individuos o elementos en los cuales puede presentarse determinada característica susceptible a ser estudiada."))


        val answers20 = ArrayList<Answer>()
        answers20.add(crearRespuesta("Contenido", false))
        answers20.add(crearRespuesta("Lugar", false))
        answers20.add(crearRespuesta("Tiempo", false))
        answers20.add(crearRespuesta("Todas las anteriores", true))

        questionList.add(crearPregunta("", answers20, "Las poblaciones deben situarse claramente en torno a sus características de:"))



        val answers21 = ArrayList<Answer>()
        answers21.add(crearRespuesta("Estableciendo claramente las características de la población. Con esto delimitamos cuáles serán nuestros parámetros muestrales", false))
        answers21.add(crearRespuesta("Se busca que la muestra sea un reflejo fiel del conjunto de la población (deben ser representativas).", false))
        answers21.add(crearRespuesta("Un estudio no es mejor al tener una población más grande, sino al haber delimitado claramente su población en base a los objetivos del estudio", false))
        answers21.add(crearRespuesta("A y B son correctas", true))

        questionList.add(crearPregunta("", answers21, "¿Cómo seleccionamos la muestra? "))



        val answers22 = ArrayList<Answer>()
        answers22.add(crearRespuesta("Es la rama de la filosofía que estudia la investigación científica y su producto, el conocimiento científico.", true))
        answers22.add(crearRespuesta("Es la rama de la filosofía que estudia la investigación explicativa y su producto, el conocimiento inductivo.", false))
        answers22.add(crearRespuesta("Es la rama de la filosofía que estudia la investigación explicativa y su producto, el conocimiento científico.", false))
        answers22.add(crearRespuesta("Es la rama de la filosofía que estudia la investigación cuantitativa y su producto, el conocimiento empírico.", false))

        questionList.add(crearPregunta("", answers22, "Definición del epistemología"))

        val answers23 = ArrayList<Answer>()
        answers23.add(crearRespuesta("Debe tener criterios sobre la estética de la ciencia o estudio de los valores estéticos de la investigación científica.", false))
        answers23.add(crearRespuesta("Debe reflexionar acerca de los intereses que mueven la ciencia.", false))
        answers23.add(crearRespuesta("Debe reflexionar sobre la axiología de la ciencia o estudio del sistema de valores de la comunidad científica.", false))
        answers23.add(crearRespuesta("Debe obviar el  reflexionar sobre los intereses que mueven la ciencia.", true))

        questionList.add(crearPregunta("", answers23, "Cuál de los siguientes aspecto no pertenecen a   los problemas que le competen a la epistemología"))


        val answers24 = ArrayList<Answer>()
        answers24.add(crearRespuesta("Concepto universal del procedimiento que se realiza para ejecutar una determinada tarea.", false))
        answers24.add(crearRespuesta("Conjunto de estrategias y herramientas que se utilizan para llegar a un objetivo preciso.", false))
        answers24.add(crearRespuesta("La actividad que desarrolla el hombre para alcanzar una serie de verdades sobre la realidad que la circunda.", true))
        answers24.add(crearRespuesta("Está determinada por la averiguación de datos o la búsqueda de soluciones para ciertos inconvenientes", false))

        questionList.add(crearPregunta("", answers24, "Qué es la ciencia"))


        val answers25 = ArrayList<Answer>()
        answers25.add(crearRespuesta("Es un único proceso sistemático, crítico y empírico que se aplican al estudio de un fenómeno.", false))
        answers25.add(crearRespuesta("Es una Petición de información, opinión o consejo sobre una materia determinada.", false))
        answers25.add(crearRespuesta("Es una  búsqueda de información en una fuente de documentación para aprender una cosa o para aclarar una duda.", false))
        answers25.add(crearRespuesta("Es un conjunto de procesos sistemáticos, críticos y empíricos que se aplican al estudio de un fenómeno.", true))

        questionList.add(crearPregunta("", answers25, "¿Cómo se define la investigación?"))


        val answers26 = ArrayList<Answer>()
        answers26.add(crearRespuesta("Es aquella que recoge  información basada en la observación de comportamientos naturales, discursos, respuestas abiertas para la posterior interpretación de significados.", false))
        answers26.add(crearRespuesta("Es aquel proceso que pretende encontrar respuestas a problemas trascendentales mediante la construcción teórica del objeto de investigación, introducción  innovación, etc. ", true))
        answers26.add(crearRespuesta("Utiliza experimentos y los principios encontrados en el método científico", false))
        answers26.add(crearRespuesta("Es aquella que tiene relación causal; no sólo persigue describir o acercarse a un problema, sino. que intenta encontrar las causas del mismo.", false))

        questionList.add(crearPregunta("", answers26, "¿Cómo se define la investigación científica?"))

        val answers27 = ArrayList<Answer>()
        answers27.add(crearRespuesta("4, 1, 2, 3, 5", false))
        answers27.add(crearRespuesta("2, 5, 3, 1, 4", true))
        answers27.add(crearRespuesta("1, 2, 3, 4, 5", false))
        answers27.add(crearRespuesta("3, 2, 1, 4, 5", false))

        questionList.add(crearPregunta("", answers27, "Elige la secuencia en la que ocurre las fases de la investigación científica. 1. Emisión de conclusiones 2. Observación. 3. Experimentación 4. Publicación y comparación 5. Formulación de hipótesis.\n"))


        val answers28 = ArrayList<Answer>()
        answers28.add(crearRespuesta("Niveles de la Investigación.", false))
        answers28.add(crearRespuesta("Tipos de la Investigación.", true))
        answers28.add(crearRespuesta("Fases de la Investigación.", false))

        questionList.add(crearPregunta("", answers28, "La investigación Cuantitativa, Cualitativa, Histórica, Descriptiva, Experimental, Básica, Aplicada, Documental de campo o mixta son:"))


        val answers29 = ArrayList<Answer>()
        answers29.add(crearRespuesta("La investigación básica o pura tiene como finalidad la obtención y recopilación de información para ir construyendo una base de conocimiento que se va agregando a la información previa existente. La investigación aplicada, por su parte, tienen como objetivo resolver un determinado problema o planteamiento específico, y usa el conocimiento generado por la investigación Básica para ello.", true))
        answers29.add(crearRespuesta("La investigación básica tiene como objetivo el desarrollo de las capacidades reflexivas y críticas a través del análisis, interpretación y confrontación de la información recogida. En cambio la Investigación Aplicada Comprende el desarrollo de prototipos y la construcción y operación de Plantas Piloto.", false))
        answers29.add(crearRespuesta("La investigación básica o pura tiene como finalidad la obtención y recopilación de información para ir construyendo una base de conocimiento que se va agregando a la información previa existente. La investigación aplicada, por otro lado se encarga del desarrollo de prototipos.", false))
        answers29.add(crearRespuesta("La investigación básica  se encarga de recolectar  información y describir el fenómeno, en cambio la Investigación Aplicada trata de resolver un problema específico y se fundamenta de los resultados de la Investigación Básica.", false))

        questionList.add(crearPregunta("", answers29, "¿Cuál es la diferencia entre Investigación Básica e Investigación Aplicada?"))


        val batch = db.batch()

        val cuestionarioRef = db.collection(QUESTIONNAIRE_PATH).document()

        batch.set(cuestionarioRef, questionnaire.toMapAux())

        questionList.forEach {

            val anwersList = ArrayList<Map<String, Any>>()

            it.answers.forEach { a ->
                anwersList.add(a.toMapPost())
            }
            it.hashAnswers = anwersList

            batch.set(db.collection(QUESTIONNAIRE_PATH).document(cuestionarioRef.id).collection(QUESTIONS_PATH).document(), it.toMapPost())
        }

        batch.commit().addOnSuccessListener {
            Log.e(TAG, "Cuestionario subido correctamente")
        }.addOnFailureListener {
            Log.e(TAG, it.toString())
        }

    }

    fun crearCuestionarios() {
        Log.e(TAG, "VOY A guardar mis cuesitonarios")

        val batch = db.batch()

        val listaCuestionarios = ArrayList<Questionaire>()
        listaCuestionarios.add(cuestionario_dm1())
        listaCuestionarios.add(cuestionario_dm2())
        listaCuestionarios.add(cuestionario_dm3())
        listaCuestionarios.add(cuestionario_dm4())
        listaCuestionarios.add(cuestionario_dm5())
        listaCuestionarios.add(cuestionario_dm6())
        listaCuestionarios.add(cuestionario_dm7())
        listaCuestionarios.add(cuestionario_dm8())
        listaCuestionarios.add(cuestionario_dm9())
        listaCuestionarios.add(cuestionario_dm10())

        listaCuestionarios.add(cuestionariodw1())
        listaCuestionarios.add(cuestionariodw2())
        listaCuestionarios.add(cuestionariodw3())
        listaCuestionarios.add(cuestionariodw4())
        listaCuestionarios.add(cuestionariodw5())
        listaCuestionarios.add(cuestionariodw6())
        listaCuestionarios.add(cuestionariodw7())
        listaCuestionarios.add(cuestionariodw8())
        listaCuestionarios.add(cuestionariodw9())
        listaCuestionarios.add(cuestionariodw10())

        listaCuestionarios.add(cuestionariot1())
        listaCuestionarios.add(cuestionariot2())
        listaCuestionarios.add(cuestionariot3())
        listaCuestionarios.add(cuestionariot4())
        listaCuestionarios.add(cuestionariot5())
        listaCuestionarios.add(cuestionariot6())
        listaCuestionarios.add(cuestionariot7())
        listaCuestionarios.add(cuestionariot8())
        listaCuestionarios.add(cuestionariot9())
        listaCuestionarios.add(cuestionariot10())

        listaCuestionarios.add(cuestionariobd1())
        listaCuestionarios.add(cuestionariobd2())
        listaCuestionarios.add(cuestionariobd3())
        listaCuestionarios.add(cuestionariobd4())
        listaCuestionarios.add(cuestionariobd5())
        listaCuestionarios.add(cuestionariobd6())
        listaCuestionarios.add(cuestionariobd7())
        listaCuestionarios.add(cuestionariobd8())
        listaCuestionarios.add(cuestionariobd9())
        listaCuestionarios.add(cuestionariobd10())

        listaCuestionarios.add(cuestionariopb1())
        listaCuestionarios.add(cuestionariopb2())
        listaCuestionarios.add(cuestionariopb3())
        listaCuestionarios.add(cuestionariopb4())
        listaCuestionarios.add(cuestionariopb5())
        listaCuestionarios.add(cuestionariopb6())
        listaCuestionarios.add(cuestionariopb7())
        listaCuestionarios.add(cuestionariopb8())
        listaCuestionarios.add(cuestionariopb9())
        listaCuestionarios.add(cuestionariopb10())

        listaCuestionarios.add(cuestionarioac1())
        listaCuestionarios.add(cuestionarioac2())
        listaCuestionarios.add(cuestionarioac3())
        listaCuestionarios.add(cuestionarioac4())
        listaCuestionarios.add(cuestionarioac5())
        listaCuestionarios.add(cuestionarioac6())
        listaCuestionarios.add(cuestionarioac7())
        listaCuestionarios.add(cuestionarioac8())
        listaCuestionarios.add(cuestionarioac9())
        listaCuestionarios.add(cuestionarioac10())

        listaCuestionarios.add(cuestionarioaSO1())
        listaCuestionarios.add(cuestionarioaSO2())
        listaCuestionarios.add(cuestionarioaSO3())
        listaCuestionarios.add(cuestionarioaSO4())
        listaCuestionarios.add(cuestionarioaSO5())
        listaCuestionarios.add(cuestionarioaSO6())
        listaCuestionarios.add(cuestionarioaSO7())
        listaCuestionarios.add(cuestionarioaSO8())
        listaCuestionarios.add(cuestionarioaSO9())
        listaCuestionarios.add(cuestionarioaSO10())

        listaCuestionarios.add(cuestionarioaIA1())
        listaCuestionarios.add(cuestionarioaIA2())
        listaCuestionarios.add(cuestionarioaIA3())
        listaCuestionarios.add(cuestionarioaIA4())
        listaCuestionarios.add(cuestionarioaIA5())
        listaCuestionarios.add(cuestionarioaIA6())
        listaCuestionarios.add(cuestionarioaIA7())
        listaCuestionarios.add(cuestionarioaIA8())
        listaCuestionarios.add(cuestionarioaIA9())
        listaCuestionarios.add(cuestionarioaIA10())

        listaCuestionarios.add(cuestionarioaC1())
        listaCuestionarios.add(cuestionarioaC2())
        listaCuestionarios.add(cuestionarioaC3())
        listaCuestionarios.add(cuestionarioaC4())
        listaCuestionarios.add(cuestionarioaC5())
        listaCuestionarios.add(cuestionarioaC6())
        listaCuestionarios.add(cuestionarioaC7())
        listaCuestionarios.add(cuestionarioaC8())
        listaCuestionarios.add(cuestionarioaC9())
        listaCuestionarios.add(cuestionarioaC10())

        listaCuestionarios.add(cuestionarioaCA1())
        listaCuestionarios.add(cuestionarioaCA2())
        listaCuestionarios.add(cuestionarioaCA3())
        listaCuestionarios.add(cuestionarioaCA4())
        listaCuestionarios.add(cuestionarioaCA5())
        listaCuestionarios.add(cuestionarioaCA6())
        listaCuestionarios.add(cuestionarioaCA7())
        listaCuestionarios.add(cuestionarioaCA8())
        listaCuestionarios.add(cuestionarioaCA9())
        listaCuestionarios.add(cuestionarioaCA10())


        listaCuestionarios.forEach {
            val cuestionarioRef = db.collection(QUESTIONNAIRE_PATH).document()
            batch.set(cuestionarioRef, it.toMapAux())

            it.questions.forEach {

                val anwersList = ArrayList<Map<String, Any>>()

                it.answers.forEach { a ->
                    anwersList.add(a.toMapPost())
                }
                it.hashAnswers = anwersList


                batch.set(db.collection(QUESTIONNAIRE_PATH).document(cuestionarioRef.id).collection(QUESTIONS_PATH).document(), it.toMapPost())
            }
        }

        batch.commit().addOnSuccessListener {
            Log.e(TAG, "Cuestionario subidos correctamete")
        }.addOnFailureListener {
            Log.e(TAG, it.toString())
        }
    }

    fun crearCuestionario(title: String, descripcion: String, category: String, num_preg: Int, palabrasClaves: String): Questionaire {
        val q = Questionaire()
        q.title = title
        q.description = descripcion
        q.subject = category
        q.idUser = getUid()
        q.post = true
        q.numberQuest = num_preg
        q.keywords = palabrasClaves
        return q
    }

    fun crearRespuesta(statement: String, correct: Boolean): Answer {
        val answer = Answer()
        answer.statement = statement
        answer.correct = correct
        return answer
    }

    fun crearPregunta(url: String, anserws: ArrayList<Answer>, statement: String): Question {
        val question = Question()
        question.statement = statement
        question.answers = anserws
        question.photoUrl = url
        return question
        //repository.onCreateQuestion(question, idQuestionnaire)
    }

    fun getRecommendations(callback: OnCallbackApis<QuerySnapshot>) {
        db.collection(USERS_PATH).document(getUid()).collection(RECOMMENDATION_PATH).get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
                    callback.onError(it.toString())
                }

    }


}