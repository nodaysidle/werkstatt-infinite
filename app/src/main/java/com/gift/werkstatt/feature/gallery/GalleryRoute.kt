package com.gift.werkstatt.feature.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gift.werkstatt.R
import com.gift.werkstatt.domain.canvas.model.PaperTemplate
import com.gift.werkstatt.feature.templates.TemplatePickerDialog

@Composable
fun GalleryRoute(
    onOpenCanvas: (String) -> Unit,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel) {
        viewModel.openCanvasEvents.collect(onOpenCanvas)
    }

    GalleryScreen(
        state = state,
        onCreateClick = viewModel::requestCreate,
        onCycleSort = viewModel::cycleSortMode,
        onOpenCanvas = onOpenCanvas,
        onRequestRename = viewModel::requestRename,
        onRequestDelete = viewModel::requestDelete
    )

    if (state.showTemplatePicker) {
        val defaultTitle = androidx.compose.ui.res.stringResource(R.string.canvas_untitled)
        TemplatePickerDialog(
            onTemplateSelected = { template: PaperTemplate ->
                viewModel.create(template, defaultTitle)
            },
            onDismiss = viewModel::dismissTemplatePicker
        )
    }

    state.renameTarget?.let { entry ->
        RenameCanvasDialog(
            initialTitle = entry.title,
            onDismiss = viewModel::dismissRename,
            onConfirm = { title -> viewModel.rename(entry, title) }
        )
    }

    state.deleteTarget?.let { entry ->
        DeleteCanvasDialog(
            onDismiss = viewModel::dismissDelete,
            onConfirm = { viewModel.delete(entry) }
        )
    }
}
