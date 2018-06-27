package ec.com.dovic.aprendiendo.domain

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
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.entities.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.*


/**
 * Created by victor on 23/1/18.
 */

class FirebaseApi(val db: FirebaseFirestore, var mAuth: FirebaseAuth, var storage: StorageReference) {

    private val TAG = "FirebaseApi"
    private val USERS_PATH = "users"

    private val RATING_PATH = "ratings"
    private val DOWNLOAD_PATH = "download"

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
                }
                .addOnFailureListener { e ->
                    callback.onError(e.message)
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
        doc.set(questionaire.toMap())
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
        db.collection(QUESTIONNAIRE_PATH).whereEqualTo("idUser", id)
                .get()
                .addOnSuccessListener {
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
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
                    Log.e(TAG, "succes" + it.toString())
                    callback.onSuccess(it)
                }
                .addOnFailureListener {
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

    fun getQuestionnaireComplete(idQuestionnaire: String, update: Boolean, callback: OnCallbackApis<Questionaire>) {
        val qRef = db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire)

        val dqRef = db.collection(QUESTIONNAIRE_PATH).document(idQuestionnaire).collection(DOWNLOAD_PATH).document(getUid())

        val dquRef = db.collection(USERS_PATH).document(getUid()).collection(DOWNLOAD_PATH).document(idQuestionnaire)

        var downQuestionnnaireMap = HashMap<String, Any>()
        downQuestionnnaireMap.put("date", FieldValue.serverTimestamp())
        downQuestionnnaireMap.put("idUser", getUid())


        var downUserMap = HashMap<String, Any>()
        downUserMap.put("date", FieldValue.serverTimestamp())
        downUserMap.put("idQuestionnaire", idQuestionnaire)


        db.runTransaction(object : Transaction.Function<Questionaire> {
            override fun apply(t: Transaction): Questionaire? {
                var doc = t.get(qRef)
                var questionnaire = doc.toObject(Questionaire::class.java)
                questionnaire!!.idCloud = doc.id

                // Aumentamos en 1 el numero de descargas
                questionnaire!!.numberDonwloads = if (update) questionnaire!!.numberDonwloads else questionnaire!!.numberDonwloads + 1

                t.update(qRef, questionnaire.toMapDownload())

                t.set(dqRef, downQuestionnnaireMap)

                t.set(dquRef, downUserMap)

                return questionnaire
            }
        }).addOnSuccessListener {
            Log.e(TAG, "todo bien ")
            callback.onSuccess(it)

        }.addOnFailureListener {
            Log.e(TAG, it.cause.toString())
            callback.onError(it.message)
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

        val questionnaire = crearCuestionario("Conociendo Android", "Aprendiendo sobre la plataforma android", "DESARROLLO MOVIL", 5)

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

        val questionnaire = crearCuestionario("Android Básico.", "Conceptos básicos sobre la plataforma Android.", "DESARROLLO MOVIL", 5)


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

        val questionnaire = crearCuestionario("Android Studio", "Conceptos básicos sobre el IDE Android Studio.", "DESARROLLO MOVIL", 3)


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

        val questionnaire = crearCuestionario("Recursos Android.", "Identificación de los recursos utilizados por Android.", "PDESARROLLO MOVIL", 4)


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

        val questionnaire = crearCuestionario("Aprendiendo  Kotlin.", "Introducción al desarrollo con Kotlin.", "DESARROLLO MOVIL", 4)


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

        val questionnaire = crearCuestionario("Sdk Android ", "Introdución al sdk de Android", "DESARROLLO MOVIL", 2)

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

        val questionnaire = crearCuestionario("Despliegue de una aplicación Android", "Nociones básicas para el despliegue de una aplicacion en la Play Store", "DESARROLLO MOVIL", 2)

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

        val questionnaire = crearCuestionario("Plataformas Móvil", "Plataformas utilizadas para desarrollar aplicaciones moviles", "DESARROLLO MOVIL", 2)

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

        val questionnaire = crearCuestionario("Firebase", "Conociendo firebase para desarrollo móvil", "DESARROLLO MOVIL", 2)

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

        val questionnaire = crearCuestionario("Cloud Firestore", "Introducción a la base de datos firestore para desarrollo móvil", "DESARROLLO MOVIL", 2)

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

        val questionnaire = crearCuestionario("Html", "Etiquetas de forma general", "DESARROLLO WEB FRONTEND", 3)


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

        val questionnaire = crearCuestionario("Html", "Etiqueta input", "DESARROLLO WEB FRONTEND", 4)


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

        val questionnaire = crearCuestionario("Html", "Restricciones en los atributos ", "DESARROLLO WEB FRONTEND", 2)


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

        val questionnaire = crearCuestionario("Angular CLI", "Introducción a Angular CLI", "DESARROLLO WEB FRONTEND", 3)


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

        val questionnaire = crearCuestionario("Angular ", "Conceptos generales", "DESARROLLO WEB FRONTEND", 2)


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

        val questionnaire = crearCuestionario("CSS Básico", "Conceptos básicos sobre css desarrollo web", "DESARROLLO WEB FRONTEND", 2)


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

        val questionnaire = crearCuestionario("Javascript Básico", "Nociones basicas de javascript", "DESARROLLO WEB FRONTEND", 2)


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

        val questionnaire = crearCuestionario("Javascript Básico", "Funciones básicas utilizadas", "DESARROLLO WEB FRONTEND", 2)


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

        val questionnaire = crearCuestionario("Javascript Variables", "Declaracion y tipos de variables", "DESARROLLO WEB FRONTEND", 2)


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

        val questionnaire = crearCuestionario("Comentarios en javascript", "Utilizacion de comentarios sola linea y multilinea", "DESARROLLO WEB FRONTEND", 2)


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

        val questionnaire = crearCuestionario("Redes y Telecomunicaciones.", "Conceptos sobre redes y telecomunicaciones.", "TELEMÁTICA", 5)


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

        val questionnaire = crearCuestionario("Tipos de Redes", "Redes tipo wan, pan, lan, can", "TELEMÁTICA", 5)


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

        val questionnaire = crearCuestionario("Topologia de Redes.", "Introducción a las topologías en redes y telecomunicaciones.", "TELEMÁTICA", 5)


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

        val questionnaire = crearCuestionario("Tipos de cables Redes.", "Introducción a los tipos de cables utilizados en redes.", "TELEMÁTICA", 5)


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

        val questionnaire = crearCuestionario("Modelo Osi", "Introducción a las capas del modelo OSI.", "TELEMÁTICA", 5)


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

        val questionnaire = crearCuestionario("Direccion IP", "Conceptos básicos sobre direcciones ip", "TELEMÁTICA", 2)


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

        val questionnaire = crearCuestionario("Protocolo TCP/IP", "Conceptos básicos sobre tcp/ip", "TELEMÁTICA", 2)


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

        val questionnaire = crearCuestionario("Topogolias de Red", "Caracterisitcas de las topologias de red", "TELEMÁTICA", 2)


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

        val questionnaire = crearCuestionario("Arquitectura de una red", "Caracterisitcas de la arquitectura de red", "TELEMÁTICA", 2)


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

        val questionnaire = crearCuestionario("Dirección IP", "Caracterisitcas de las direcciones ip", "TELEMÁTICA", 2)


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

        val questionnaire = crearCuestionario("Sentencias Mysql", "Sentencias basicas para base de datos mysql", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Introducción MongoDB", "Conceptos básicos sobre la base de datos mongo db.", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Introducción Base de Datos", "Fundamentos básicos sobre base de datos", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Base de Datos Relacional", "Fundamentos básicos sobre base de datos relacionales", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Base de Datos Relacional", "Caracteristicas de  base de datos relacionales", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Modelo de Base de datos", " Modelos de base de datos existentes", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Base de datos no relacionales", " Introducción base de datos no relacionales.", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Base de datos no relacionales", " Caracteristicas base de datos no relacionales.", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Base de datos relacionales", " Caracteristicas base de datos relacionales.", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Clave primaria", " Caracteristica de una clave primaria", "BASE DE DATOS", 5)


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

        val questionnaire = crearCuestionario("Programación Básica.", "Conceptos sobre programación básica", "Programación Básica", 3)


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

        val questionnaire = crearCuestionario("Lenguajes de Programacion.", "Definición de un lenguaje de programación", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Tipos de lenguaje de Programacion.", "Clasificación de lenguajes como alto nivel, bajo nivel.", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Lenguaje Emsamblador", "Definición de lenguaje emsamblador", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Lenguaje Máquina", "Definición de lenguaje máquina", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Lenguaje Alto Nivel", "Definición de lenguaje de alto nivel", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Programacion Orientada a objetos", "Lenguajes de programación con paradigma orientado a objetos", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Tipos de Datos", "Principales tipos de datos usados en programación", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Clases", "Definción de una clase en programación", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Atributos", "Definción de una atributo de una clase en programación", "Programación Básica", 1)

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

        val questionnaire = crearCuestionario("Perificos de entrada", "Perificos de entrada de un computador", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("Perificos de salida", "Perificos de salida de un computador", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("CPU", "Deficion de cpu", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("CPU", "Composicion del cpu", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("Composcion de una computadora", "Componentes principales de una computadora", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("Unidades de almacenamiento", "Tipo de unidades de almacenamiento", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("Disco Duro", "Velocidad de un disco duro", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("Disco Duro", "Capacidad de un disco duro", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("Bios", "Funciones principales de la bios", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("Procesador", "Funcion principal de la CPU", "ARQUITECTURA DE COMPUTADORES", 3)

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

        val questionnaire = crearCuestionario("Procesos", "Definición de los procesos", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Sistema Operativo", "Definición de un sistema operativo", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Sistema Operativo", "Objetivos de un sistema operativo", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Sistema Operativo Multitarea", "Definición de un sistema operativo multitarea", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Sistema Multiusuario", "Definición de un sistema multiusuario", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Sistema Multiprocesador", "Definición de un sistema multiprocesor", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Interrumpciones", "Interrupciones en un sistema operativo", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Memoria cache", "Funcione de la memoria cache", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Procesos", "Estados de un proceso ", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("Información Procesos", "Informacion asociada a un proceso. ", "SISTEMAS OPERATIVOS", 1)

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

        val questionnaire = crearCuestionario("", " Introducción a la Inteligencia artificial", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "División de la inteligencia artificial", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", " Aplicaciones y Técnicas", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "Fundamentos básicos", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "Campos de Aplicación", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "Personajes influyentes", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "Objetivos principales", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "Métodos utilizados para el desarrollo y aprendizaje", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "Categoria de sistemas", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "Agentes inteligentes", "INTELIGENCIA ARTIFICAL", 1)

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

        val questionnaire = crearCuestionario("", "Conceptos generales de compiladores", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Análisis léxico conceptos generales", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Análisis léxico", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Análisis sintáctico", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Análisis semántico", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Gramática libre de contexto (LLI)", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Analizador sintáctico predictivo dirigido por tabla", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Generación de código intermedio", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Introducción a compiladores", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Estructura de un compilador", "COMPILADORES", 1)

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

        val questionnaire = crearCuestionario("", "Diagramas de bloques", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Funciones de transferencia en sistemas en tiempo discreto", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Reglas de sintonía PID", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Filtros en funciones de transferencia", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Método de optimización computacional controladores PID", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Modificaciones a los esquemas de control PID", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Control con 2 grados de libertad", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Respuesta al impulso y función de transferencia de sistemas lineales.", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Asignacion de ceros en Polos y Ceros", "CONTROL AUTOMATIZADO", 5)

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

        val questionnaire = crearCuestionario("", "Graficas de Flujo de Señales", "CONTROL AUTOMATIZADO", 5)

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
    fun crearCuestionarios() {
        Log.e(TAG, "VOY A guardar mis cuesitonarios")

        val batch = db.batch()

        val listaCuestionarios = ArrayList<Questionaire>()
        listaCuestionarios.add(cuestionario1())
        listaCuestionarios.add(cuestionario2())
        listaCuestionarios.add(cuestionario3())
        listaCuestionarios.add(cuestionario4())
        listaCuestionarios.add(cuestionario5())
        listaCuestionarios.add(cuestionario6())
        listaCuestionarios.add(cuestionario7())
        listaCuestionarios.add(cuestionario8())
        listaCuestionarios.add(cuestionario9())
        listaCuestionarios.add(cuestionario10())
        listaCuestionarios.add(cuestionario11())
        listaCuestionarios.add(cuestionario12())
        listaCuestionarios.add(cuestionario13())
        listaCuestionarios.add(cuestionario14())
        listaCuestionarios.add(cuestionario15())
        listaCuestionarios.add(cuestionario16())
        listaCuestionarios.add(cuestionario17())
        listaCuestionarios.add(cuestionario18())
        listaCuestionarios.add(cuestionario19())

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

    */

    fun crearCuestionario(title: String, descripcion: String, category: String, num_preg: Int): Questionaire {
        val q = Questionaire()
        q.title = title
        q.description = descripcion
        q.category = category
        q.idUser = getUid()
        q.post = true
        q.numberQuest = num_preg
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


}