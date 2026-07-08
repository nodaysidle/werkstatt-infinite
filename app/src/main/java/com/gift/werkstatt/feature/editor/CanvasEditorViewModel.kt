package com.gift.werkstatt.feature.editor

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gift.werkstatt.core.navigation.WerkstattRoute
import com.gift.werkstatt.data.files.CanvasExportStore
import com.gift.werkstatt.domain.canvas.model.BrushPresets
import com.gift.werkstatt.domain.canvas.model.BrushType
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.model.DEFAULT_INK
import com.gift.werkstatt.domain.canvas.model.GridMode
import com.gift.werkstatt.domain.canvas.model.Stroke
import com.gift.werkstatt.domain.canvas.model.StrokePoint
import com.gift.werkstatt.domain.canvas.usecase.BuildStrokeUseCase
import com.gift.werkstatt.domain.canvas.usecase.EraseStrokesUseCase
import com.gift.werkstatt.domain.canvas.usecase.GetCanvasUseCase
import com.gift.werkstatt.domain.canvas.usecase.SaveCanvasUseCase
import com.gift.werkstatt.rendering.spatial.StrokeSpatialIndex
import com.gift.werkstatt.rendering.transform.CanvasTransformMapper
import com.gift.werkstatt.rendering.transform.ViewportTransform
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CanvasEditorUiState(
    val entry: CanvasEntry? = null,
    val currentStroke: List<StrokePoint> = emptyList(),
    val viewportOffset: Offset = Offset.Zero,
    val zoom: Float = 1f,
    val gridMode: GridMode = GridMode.None,
    val gridSize: Float = 40f,
    val brushType: BrushType = BrushType.Pen,
    val brushSize: Float = BrushPresets.forType(BrushType.Pen).size,
    val brushOpacity: Float = BrushPresets.forType(BrushType.Pen).opacity,
    val color: Long = DEFAULT_INK,
    val recentColors: List<Long> = emptyList(),
    val eraserMode: Boolean = false,
    val canRedo: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val titleEditValue: String? = null,
    val errorMessage: String? = null
) {
    val strokes: List<Stroke> = entry?.strokes.orEmpty()
}

@HiltViewModel
class CanvasEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCanvas: GetCanvasUseCase,
    private val saveCanvas: SaveCanvasUseCase,
    private val buildStroke: BuildStrokeUseCase,
    private val eraseStrokes: EraseStrokesUseCase,
    private val exportStore: CanvasExportStore,
    private val spatialIndex: StrokeSpatialIndex,
    private val transformMapper: CanvasTransformMapper
) : ViewModel() {
    private val canvasId: String = checkNotNull(savedStateHandle[WerkstattRoute.Editor.ARG_CANVAS_ID])

    private val _uiState = MutableStateFlow(CanvasEditorUiState())
    val uiState: StateFlow<CanvasEditorUiState> = _uiState.asStateFlow()

    private val _exportEvents = MutableSharedFlow<Result<File>>()
    val exportEvents: SharedFlow<Result<File>> = _exportEvents.asSharedFlow()

    private val redoStack = ArrayDeque<Stroke>()
    private var saveJob: Job? = null
    private var dirty = false
    private var dirtyContent = false
    private var lastThumbnailRefresh = 0L
    private val strokeBuffer = ArrayList<StrokePoint>(256)

    init {
        viewModelScope.launch {
            try {
                val entry = getCanvas(canvasId)
                if (entry != null) {
                    spatialIndex.rebuild(entry.strokes)
                    _uiState.update {
                        it.copy(
                            entry = entry,
                            viewportOffset = Offset(entry.viewportX, entry.viewportY),
                            zoom = entry.zoom,
                            gridMode = entry.gridMode,
                            gridSize = entry.gridSize,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Canvas not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun startStroke(point: StrokePoint) {
        if (_uiState.value.eraserMode) {
            erase(listOf(point))
            return
        }
        strokeBuffer.clear()
        strokeBuffer.add(point)
        _uiState.update { it.copy(currentStroke = strokeBuffer.toList()) }
    }

    fun moveStroke(points: List<StrokePoint>) {
        if (points.isEmpty()) return
        if (_uiState.value.eraserMode) {
            erase(points)
            return
        }
        strokeBuffer.addAll(points)
        _uiState.update { it.copy(currentStroke = strokeBuffer.toList()) }
    }

    fun endStroke() {
        if (_uiState.value.eraserMode) return
        val points = strokeBuffer.toList()
        strokeBuffer.clear()
        if (points.isEmpty()) {
            _uiState.update { it.copy(currentStroke = emptyList()) }
            return
        }

        val state = _uiState.value
        val entry = state.entry ?: return
        val nextEntry = buildStroke.execute(
            entry = entry,
            points = points,
            color = state.color,
            width = state.brushSize,
            brushType = state.brushType,
            opacity = state.brushOpacity
        )
        redoStack.clear()
        _uiState.update {
            it.copy(
                entry = nextEntry,
                currentStroke = emptyList(),
                canRedo = false
            )
        }
        markDirty(contentChanged = true)
    }

    fun applyGesture(zoomChange: Float, pan: Offset, centroid: Offset) {
        val current = ViewportTransform(_uiState.value.zoom, _uiState.value.viewportOffset)
        val next = transformMapper.applyGesture(current, zoomChange, pan, centroid)
        _uiState.update { it.copy(zoom = next.zoom, viewportOffset = next.offset) }
        markDirty(contentChanged = false)
    }

    fun cycleGrid() {
        val entry = _uiState.value.entry ?: return
        val next = when (_uiState.value.gridMode) {
            GridMode.None -> GridMode.Lines
            GridMode.Lines -> GridMode.Dots
            GridMode.Dots -> GridMode.None
        }
        _uiState.update { it.copy(entry = entry.copy(gridMode = next), gridMode = next) }
        markDirty(contentChanged = false)
    }

    fun toggleEraser() {
        _uiState.update { it.copy(eraserMode = !it.eraserMode) }
    }

    fun setBrush(type: BrushType) {
        val preset = BrushPresets.forType(type)
        _uiState.update {
            it.copy(
                brushType = type,
                brushSize = preset.size,
                brushOpacity = preset.opacity,
                eraserMode = false
            )
        }
    }

    fun setBrushSize(size: Float) {
        _uiState.update { it.copy(brushSize = size.coerceIn(1f, 50f)) }
    }

    fun setColor(color: Long) {
        val recent = _uiState.value.recentColors.toMutableList()
        recent.remove(color)
        recent.add(0, color)
        while (recent.size > 8) recent.removeAt(recent.lastIndex)
        _uiState.update { it.copy(color = color, recentColors = recent, eraserMode = false) }
    }

    fun undo() {
        val entry = _uiState.value.entry ?: return
        if (entry.strokes.isEmpty()) return
        val removed = entry.strokes.last()
        redoStack.addLast(removed)
        val nextStrokes = entry.strokes.dropLast(1)
        spatialIndex.rebuild(nextStrokes)
        _uiState.update {
            it.copy(entry = entry.copy(strokes = nextStrokes), canRedo = redoStack.isNotEmpty())
        }
        markDirty(contentChanged = true)
    }

    fun redo() {
        val entry = _uiState.value.entry ?: return
        val restored = redoStack.removeLastOrNull() ?: return
        val nextStrokes = entry.strokes + restored
        spatialIndex.rebuild(nextStrokes)
        _uiState.update {
            it.copy(entry = entry.copy(strokes = nextStrokes), canRedo = redoStack.isNotEmpty())
        }
        markDirty(contentChanged = true)
    }

    fun beginTitleEdit() {
        _uiState.update { it.copy(titleEditValue = it.entry?.title) }
    }

    fun dismissTitleEdit() {
        _uiState.update { it.copy(titleEditValue = null) }
    }

    fun updateTitle(title: String) {
        val entry = _uiState.value.entry ?: return
        if (title.isBlank()) return
        _uiState.update { it.copy(entry = entry.copy(title = title), titleEditValue = null) }
        markDirty(contentChanged = false)
        saveNow(refreshThumbnail = false)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveNow(refreshThumbnail: Boolean = dirtyContent) {
        saveJob?.cancel()
        viewModelScope.launch {
            saveCurrent(refreshThumbnail = refreshThumbnail)
        }
    }

    suspend fun saveBeforeExit(refreshThumbnail: Boolean = dirtyContent) {
        saveJob?.cancel()
        saveCurrent(refreshThumbnail = refreshThumbnail)
    }

    fun export() {
        val entry = _uiState.value.entry ?: return
        viewModelScope.launch {
            saveCurrent(refreshThumbnail = dirtyContent)
            _exportEvents.emit(exportStore.export(entry.copy(strokes = _uiState.value.strokes)))
        }
    }

    override fun onCleared() {
        saveJob?.cancel()
        super.onCleared()
    }

    private fun erase(points: List<StrokePoint>) {
        val entry = _uiState.value.entry ?: return
        val nextEntry = eraseStrokes.execute(entry, points)
        if (nextEntry.strokes.size == entry.strokes.size) return
        _uiState.update { it.copy(entry = nextEntry) }
        markDirty(contentChanged = true)
    }

    private fun markDirty(contentChanged: Boolean) {
        dirty = true
        dirtyContent = dirtyContent || contentChanged
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(1_800L)
            saveCurrent(refreshThumbnail = shouldRefreshThumbnail())
        }
    }

    private fun shouldRefreshThumbnail(): Boolean {
        val now = System.currentTimeMillis()
        return dirtyContent && now - lastThumbnailRefresh > 5_000L
    }

    private suspend fun saveCurrent(refreshThumbnail: Boolean) {
        if (!dirty && !dirtyContent) return
        val entry = _uiState.value.entry ?: return
        _uiState.update { it.copy(isSaving = true) }
        val persisted = saveCanvas(
            entry = entry.copy(
                viewportX = _uiState.value.viewportOffset.x,
                viewportY = _uiState.value.viewportOffset.y,
                zoom = _uiState.value.zoom,
                gridMode = _uiState.value.gridMode,
                gridSize = _uiState.value.gridSize
            ),
            refreshThumbnail = refreshThumbnail
        )
        if (refreshThumbnail) lastThumbnailRefresh = System.currentTimeMillis()
        dirty = false
        dirtyContent = false
        _uiState.update { it.copy(entry = persisted, isSaving = false) }
    }
}
