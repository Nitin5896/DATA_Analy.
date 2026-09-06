package com.beperfectsalon.app.data.repository

import com.beperfectsalon.app.data.firebase.FirestorePaths
import com.beperfectsalon.app.data.model.Service
import com.beperfectsalon.app.data.model.Stylist
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ServiceRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    suspend fun getActiveServices(): Result<List<Service>> = runCatching {
        firestore.collection(FirestorePaths.SERVICES)
            .whereEqualTo("active", true)
            .get().await()
            .toObjects(Service::class.java)
    }

    suspend fun getActiveStylists(): Result<List<Stylist>> = runCatching {
        firestore.collection(FirestorePaths.STYLISTS)
            .whereEqualTo("active", true)
            .get().await()
            .toObjects(Stylist::class.java)
    }
}
