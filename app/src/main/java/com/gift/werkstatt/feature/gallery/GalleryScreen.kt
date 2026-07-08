package com.gift.werkstatt.feature.gallery

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.R
import com.gift.werkstatt.core.design.LocalWerkstattColors
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.model.SortMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun GalleryScreen(
    state: GalleryUiState,
    onCreateClick: () -> Unit,
    onCycleSort: () -> Unit,
    onOpenCanvas: (String) -> Unit,
    onRequestRename: (CanvasEntry) -> Unit,
    onRequestDelete: (CanvasEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWerkstattColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GalleryHeader(
                count = state.entries.size,
                sortMode = state.sortMode,
                onCreateClick = onCreateClick,
                onCycleSort = onCycleSort
            )

            if (state.entries.isEmpty()) {
                EmptyGallery(onCreateClick = onCreateClick, modifier = Modifier.fillMaxSize())
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.sortedEntries, key = { it.id }) { entry ->
                        CanvasCard(
                            entry = entry,
                            onOpen = { onOpenCanvas(entry.id) },
                            onRename = { onRequestRename(entry) },
                            onDelete = { onRequestDelete(entry) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryHeader(
    count: Int,
    sortMode: SortMode,
    onCreateClick: () -> Unit,
    onCycleSort: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    val sortLabel = stringResource(sortMode.labelRes())
    val createDescription = stringResource(R.string.content_create_canvas)
    val sortDescription = stringResource(R.string.content_sort_canvas, sortLabel)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(8.dp),
        color = colors.surface,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.gallery_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.text
                    )
                    Text(
                        text = if (count == 1) {
                            stringResource(R.string.gallery_canvas_count_one)
                        } else {
                            stringResource(R.string.gallery_canvas_count_many, count)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textMuted
                    )
                }

                FilledTonalButton(
                    onClick = onCreateClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colors.accent,
                        contentColor = colors.onAccent
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = createDescription
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                    Text(stringResource(R.string.gallery_create))
                }
            }

            if (count > 0) {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onCycleSort,
                    modifier = Modifier.semantics {
                        contentDescription = sortDescription
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(stringResource(R.string.gallery_sort_button, sortLabel))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CanvasCard(
    entry: CanvasEntry,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    var showMenu by remember { mutableStateOf(false) }
    val formattedDate = remember(entry.updatedAt) { formatDate(entry.updatedAt) }
    val cardDescription = stringResource(R.string.content_canvas_card, entry.title, formattedDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .semantics {
                contentDescription = cardDescription
            }
            .combinedClickable(
                onClick = onOpen,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
    ) {
        Box {
            Column {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(colors.canvasRaised),
                    contentAlignment = Alignment.Center
                ) {
                    Thumbnail(path = entry.thumbnailPath, title = entry.title)
                }

                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.dialog_rename_title)) },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        onRename()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.dialog_delete), color = colors.error) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = colors.error) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
private fun Thumbnail(path: String?, title: String) {
    val colors = LocalWerkstattColors.current
    var thumbnail by remember(path) { mutableStateOf<ImageBitmap?>(null) }
    val fallbackInitials = stringResource(R.string.gallery_title).take(2)

    LaunchedEffect(path) {
        thumbnail = withContext(Dispatchers.IO) {
            path?.takeIf { File(it).exists() }?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
        }
    }

    val image = thumbnail
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Text(
            text = title.trim().take(2).uppercase(Locale.getDefault()).ifBlank { fallbackInitials },
            style = MaterialTheme.typography.headlineMedium,
            color = colors.text.copy(alpha = 0.16f)
        )
    }
}

@Composable
private fun EmptyGallery(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWerkstattColors.current
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(118.dp)
                .background(colors.surfaceRaised, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(Modifier.height(22.dp))
        Text(
            text = stringResource(R.string.gallery_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = colors.text
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onCreateClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = colors.onAccent)
        ) {
            Text(stringResource(R.string.gallery_empty_action))
        }
    }
}

@Composable
fun RenameCanvasDialog(
    initialTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_rename_title)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.dialog_canvas_title)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title) }) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

@Composable
fun DeleteCanvasDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = LocalWerkstattColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_delete_title)) },
        text = { Text(stringResource(R.string.dialog_delete_body)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_delete), color = colors.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

private fun SortMode.labelRes(): Int = when (this) {
    SortMode.Updated -> R.string.gallery_sort_updated
    SortMode.Created -> R.string.gallery_sort_created
    SortMode.Name -> R.string.gallery_sort_name
}

private fun formatDate(timestamp: Long): String {
    val formatter = DateTimeFormatter
        .ofPattern("MMM d, yyyy")
        .withLocale(Locale.getDefault())
    return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(formatter)
}
