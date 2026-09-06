package com.beperfectsalon.app.data.repository

import com.beperfectsalon.app.data.firebase.FirestorePaths
import com.beperfectsalon.app.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun signUp(name: String, email: String, password: String, phone: String): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("Sign up did not return a user id")
        val profile = UserProfile(uid = uid, name = name, email = email, phone = phone)
        firestore.collection(FirestorePaths.USERS).document(uid).set(profile).await()
        Unit
    }

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    fun logout() = auth.signOut()

    suspend fun getProfile(uid: String): Result<UserProfile?> = runCatching {
        firestore.collection(FirestorePaths.USERS).document(uid).get().await()
            .toObject(UserProfile::class.java)
    }

    suspend fun updateProfile(uid: String, name: String, phone: String): Result<Unit> = runCatching {
        firestore.collection(FirestorePaths.USERS).document(uid)
            .update(mapOf("name" to name, "phone" to phone)).await()
        Unit
    }

    /**
     * Deletes the signed-in user's own bookings and profile document, then their Firebase
     * Auth account itself. Required by Google Play's account-deletion policy for any app
     * that supports account creation. If Firebase requires a fresh login before allowing
     * account deletion (FirebaseAuthRecentLoginRequiredException, e.g. the session is old),
     * the caller should ask the user to log in again and retry.
     */
    suspend fun deleteAccount(uid: String): Result<Unit> = runCatching {
        val bookingDocs = firestore.collection(FirestorePaths.BOOKINGS)
            .whereEqualTo("userId", uid)
            .get().await()

        val batch = firestore.batch()
        bookingDocs.documents.forEach { batch.delete(it.reference) }
        batch.delete(firestore.collection(FirestorePaths.USERS).document(uid))
        batch.commit().await()

        val user = auth.currentUser ?: error("No signed-in user to delete.")
        user.delete().await()
        Unit
    }
}
