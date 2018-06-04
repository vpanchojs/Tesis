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


    fun cuestionario1(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Conociendo Android", "Aprendiendo sobre la plataforma android", "Programación Móvil", 5)

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

    fun cuestionario2(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Android Básico.", "Conceptos básicos sobre la plataforma Android.", "Programación Móvil", 5)


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

    fun cuestionario3(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Android Studio", "Conceptos básicos sobre el IDE Android Studio.", "Programación Móvil", 3)


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

    fun cuestionario4(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Recursos Android.", "Identificación de los recursos utilizados por Android.", "Programación Móvil", 4)


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

    fun cuestionario5(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Aprendiendo  Kotlin.", "Introducción al desarrollo con Kotlin.", "Programación Móvil", 4)


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

    fun cuestionario6(): Questionaire {

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

    fun cuestionario7(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Redes y Telecomunicaciones.", "Conceptos sobre redes y telecomunicaciones.", "Telemática", 5)


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

    fun cuestionario8(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Tipos de Redes", "Redes tipo wan, pan, lan, can", "Telemática", 5)


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

    fun cuestionario9(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Topologia de Redes.", "Introducción a las topologías en redes y telecomunicaciones.", "Telemática", 5)


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

    fun cuestionario10(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Tipos de cables Redes.", "Introducción a los tipos de cables utilizados en redes.", "Telemática", 5)


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

    fun cuestionario11(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Modelo Osi", "Introducción a las capas del modelo OSI.", "Telemática", 5)


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

    fun cuestionario12(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Sentencias Mysql", "Sentencias basicas para base de datos mysql", "Base de Datos", 5)


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

    fun cuestionario13(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Introducción MongoDB", "Conceptos básicos sobre la base de datos mongo db.", "Base de Datos", 5)


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

    fun cuestionario14(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Introducción Base de Datos", "Fundamentos básicos sobre base de datos", "Base de Datos", 5)


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

    fun cuestionario15(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Html", "Etiquetas de forma general", "Programación Web", 3)


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

    fun cuestionario16(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Html", "Etiqueta input", "Programación Web", 4)


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

    fun cuestionario17(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Html", "Restricciones en los atributos ", "Programación Web", 2)


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

    fun cuestionario18(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Angular CLI", "Introduccion a Angular CLI", "Programación Web", 3)


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

    fun cuestionario19(): Questionaire {

        val questionList = ArrayList<Question>()

        val questionnaire = crearCuestionario("Angular ", "Conceptos generales", "Programación Web", 2)


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