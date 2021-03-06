package ec.com.dovic.aprendiendo.domain.listeners;

import com.google.firebase.firestore.DocumentSnapshot

interface FirebaseEventListenerCallback {

    fun onDocumentRemoved(snapshot: DocumentSnapshot);

    fun onError(error: Any);

    fun onDocumentAdded(snapshot: DocumentSnapshot);

    fun onDocumentModified(snapshot: DocumentSnapshot);
}
