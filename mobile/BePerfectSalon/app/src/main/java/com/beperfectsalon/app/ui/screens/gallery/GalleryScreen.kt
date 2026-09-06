package com.beperfectsalon.app.ui.screens.gallery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.beperfectsalon.app.data.model.GalleryItem
import com.beperfectsalon.app.data.repository.ContentRepository
import com.beperfectsalon.app.data.repository.RepositoryProvider
import com.beperfectsalon.app.ui.components.ErrorMessage
import com.beperfectsalon.app.ui.components.FullScreenLoading
import com.beperfectsalon.app.util.Resource

@Composable
fun GalleryScreen(
    contentRepository: ContentRepository = RepositoryProvider.content,
    onBack: () -> Unit,
) {
    var state by remember { mutableStateOf<Resource<List<GalleryItem>>>(Resource.Loading) }

    LaunchedEffect(Unit) {
        val result = contentRepository.getGallery()
        state = result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(it.message ?: "Could not load the gallery.") },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gallery") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        when (val current = state) {
            is Resource.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is Resource.Error -> ErrorMessage(current.message, modifier = Modifier.padding(padding))
            is Resource.Success -> {
                if (current.data.isEmpty()) {
                    ErrorMessage("Photos coming soon!", modifier = Modifier.padding(padding))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            top = padding.calculateTopPadding() + 8.dp,
                            end = 8.dp,
                            bottom = 8.dp,
                        ),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(current.data) { _, item ->
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp)),
                            )
                        }
                    }
                }
            }
        }
    }
}
