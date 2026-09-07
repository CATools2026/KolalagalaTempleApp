package com.catools.templeapp.data

import android.content.Context
import android.net.Uri
import com.catools.templeapp.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class TempleRepository(context: Context) {
    private val app: FirebaseApp? = if (BuildConfig.FIREBASE_CONFIGURED) {
        FirebaseApp.initializeApp(context)
    } else {
        null
    }

    val firebaseReady: Boolean = app != null

    private val auth: FirebaseAuth? = if (firebaseReady) FirebaseAuth.getInstance() else null
    private val db: FirebaseFirestore? = if (firebaseReady) FirebaseFirestore.getInstance() else null
    private val storage: FirebaseStorage? = if (firebaseReady) FirebaseStorage.getInstance() else null

    fun observeEvents(onUpdate: (List<TempleEvent>) -> Unit): ListenerRegistration? {
        val firestore = db ?: return null
        return firestore.collection("events")
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    onUpdate(snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TempleEvent::class.java)?.copy(id = doc.id)
                    })
                }
            }
    }

    fun observeNotices(onUpdate: (List<TempleNotice>) -> Unit): ListenerRegistration? {
        val firestore = db ?: return null
        return firestore.collection("announcements")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    onUpdate(snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TempleNotice::class.java)?.copy(id = doc.id)
                    })
                }
            }
    }

    fun observeSettings(onUpdate: (TempleSettings) -> Unit): ListenerRegistration? {
        val firestore = db ?: return null
        return firestore.collection("settings").document("main")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && snapshot.exists()) {
                    snapshot.toObject(TempleSettings::class.java)?.let(onUpdate)
                }
            }
    }

    fun signInAdmin(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        val firebaseAuth = auth
        val firestore = db
        if (firebaseAuth == null || firestore == null) {
            onResult(Result.failure(IllegalStateException("Firebase is not configured.")))
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener {
                val uid = firebaseAuth.currentUser?.uid
                if (uid == null) {
                    onResult(Result.failure(IllegalStateException("Unable to identify the signed-in user.")))
                    return@addOnSuccessListener
                }
                firestore.collection("admins").document(uid).get()
                    .addOnSuccessListener { adminDoc ->
                        if (adminDoc.exists()) {
                            onResult(Result.success(Unit))
                        } else {
                            firebaseAuth.signOut()
                            onResult(Result.failure(SecurityException("This account is not registered as a temple admin.")))
                        }
                    }
                    .addOnFailureListener { error ->
                        firebaseAuth.signOut()
                        onResult(Result.failure(error))
                    }
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun isSignedIn(): Boolean = auth?.currentUser != null

    fun signedInEmail(): String = auth?.currentUser?.email.orEmpty()

    fun signOut() {
        auth?.signOut()
    }

    fun addNotice(notice: TempleNotice, onResult: (Result<Unit>) -> Unit) {
        val firestore = db ?: return onResult(Result.failure(IllegalStateException("Firebase is not configured.")))
        val item = notice.copy(id = "", createdAt = System.currentTimeMillis())
        firestore.collection("announcements").add(item)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun deleteNotice(id: String, onResult: (Result<Unit>) -> Unit = {}) {
        val firestore = db ?: return onResult(Result.failure(IllegalStateException("Firebase is not configured.")))
        firestore.collection("announcements").document(id).delete()
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun addEvent(event: TempleEvent, onResult: (Result<Unit>) -> Unit) {
        val firestore = db ?: return onResult(Result.failure(IllegalStateException("Firebase is not configured.")))
        val item = event.copy(id = "", createdAt = System.currentTimeMillis())
        firestore.collection("events").add(item)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun deleteEvent(id: String, onResult: (Result<Unit>) -> Unit = {}) {
        val firestore = db ?: return onResult(Result.failure(IllegalStateException("Firebase is not configured.")))
        firestore.collection("events").document(id).delete()
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun saveSettings(settings: TempleSettings, onResult: (Result<Unit>) -> Unit) {
        val firestore = db ?: return onResult(Result.failure(IllegalStateException("Firebase is not configured.")))
        firestore.collection("settings").document("main")
            .set(settings, SetOptions.merge())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun uploadSettingsImage(
        imageUri: Uri,
        filePrefix: String,
        onResult: (Result<String>) -> Unit
    ) {
        val firebaseStorage = storage
        if (firebaseStorage == null) {
            onResult(Result.failure(IllegalStateException("Firebase Storage is not configured.")))
            return
        }

        val imageRef = firebaseStorage.reference.child("settings/${filePrefix}_${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: IllegalStateException("Image upload failed.")
                }
                imageRef.downloadUrl
            }
            .addOnSuccessListener { url -> onResult(Result.success(url.toString())) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }
}
