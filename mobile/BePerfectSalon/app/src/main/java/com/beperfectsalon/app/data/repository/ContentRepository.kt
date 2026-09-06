package com.beperfectsalon.app.data.repository

import com.beperfectsalon.app.data.firebase.FirestorePaths
import com.beperfectsalon.app.data.model.GalleryItem
import com.beperfectsalon.app.data.model.Offer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** Read-only content that the salon owner manages from the Firebase console: gallery + offers. */
class ContentRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    suspend fun getGallery(): Result<List<GalleryItem>> = runCatching {
        firestore.collection(FirestorePaths.GALLERY).get().await()
            .toObjects(GalleryItem::class.java)
    }

    suspend fun getActiveOffers(): Result<List<Offer>> = runCatching {
        firestore.collection(FirestorePaths.OFFERS)
            .whereEqualTo("active", true)
            .get().await()
            .toObjects(Offer::class.java)
    }
}
