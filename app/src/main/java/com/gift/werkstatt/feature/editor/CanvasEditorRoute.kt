package com.gift.werkstatt.feature.editor

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gift.werkstatt.R
import java.io.File
import kotlinx.coroutines.launch

@Composable
fun CanvasEditorRoute(
    onNavigateBack: () -> Unit,
    viewModel: CanvasEditorViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val routeScope = rememberCoroutineScope()
    val exportedMessage = androidx.compose.ui.res.stringResource(R.string.canvas_exported)
    val exportFailedMessage = androidx.compose.ui.res.stringResource(R.string.canvas_export_failed)

    LaunchedEffect(viewModel) {
        viewModel.exportEvents.collect { result ->
            result.onSuccess { file ->
                shareCanvas(context, file)
                snackbarHostState.showSnackbar(exportedMessage)
            }.onFailure {
                snackbarHostState.showSnackbar(exportFailedMessage)
            }
        }
    }

    CanvasEditorScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onStrokeStart = viewModel::startStroke,
        onStrokeMove = viewModel::moveStroke,
        onStrokeEnd = viewModel::endStroke,
        onGesture = viewModel::applyGesture,
        onNavigateBack = {
            routeScope.launch {
                viewModel.saveBeforeExit(refreshThumbnail = true)
                onNavigateBack()
            }
        },
        onUndo = viewModel::undo,
        onRedo = viewModel::redo,
        onCycleGrid = viewModel::cycleGrid,
        onToggleEraser = viewModel::toggleEraser,
        onBrushSelected = viewModel::setBrush,
        onBrushSizeChanged = viewModel::setBrushSize,
        onColorSelected = viewModel::setColor,
        onEditTitle = viewModel::beginTitleEdit,
        onDismissTitle = viewModel::dismissTitleEdit,
        onConfirmTitle = viewModel::updateTitle,
        onExport = viewModel::export
    )
}

private fun shareCanvas(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(intent, context.getString(R.string.share_canvas_chooser))
    context.startActivity(chooser)
}
