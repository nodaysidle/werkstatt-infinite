package com.gift.werkstatt.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gift.werkstatt.domain.canvas.model.CanvasEntry
import com.gift.werkstatt.domain.canvas.model.PaperTemplate
import com.gift.werkstatt.domain.canvas.model.SortMode
import com.gift.werkstatt.domain.canvas.usecase.CreateCanvasUseCase
import com.gift.werkstatt.domain.canvas.usecase.DeleteCanvasUseCase
import com.gift.werkstatt.domain.canvas.usecase.ObserveCanvasesUseCase
import com.gift.werkstatt.domain.canvas.usecase.SaveCanvasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GalleryUiState(
    val entries: List<CanvasEntry> = emptyList(),
    val sortMode: SortMode = SortMode.Updated,
    val showTemplatePicker: Boolean = false,
    val renameTarget: CanvasEntry? = null,
    val deleteTarget: CanvasEntry? = null
) {
    val sortedEntries: List<CanvasEntry>
        get() = when (sortMode) {
            SortMode.Updated -> entries.sortedByDescending { it.updatedAt }
            SortMode.Created -> entries.sortedByDescending { it.createdAt }
            SortMode.Name -> entries.sortedBy { it.title.lowercase() }
        }
}

@HiltViewModel
class GalleryViewModel @Inject constructor(
    observeCanvases: ObserveCanvasesUseCase,
    private val createCanvas: CreateCanvasUseCase,
    private val saveCanvas: SaveCanvasUseCase,
    private val deleteCanvas: DeleteCanvasUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _openCanvasEvents = MutableSharedFlow<String>()
    val openCanvasEvents: SharedFlow<String> = _openCanvasEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            observeCanvases().collect { entries ->
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }

    fun requestCreate() {
        _uiState.update { it.copy(showTemplatePicker = true) }
    }

    fun dismissTemplatePicker() {
        _uiState.update { it.copy(showTemplatePicker = false) }
    }

    fun create(template: PaperTemplate, title: String) {
        viewModelScope.launch {
            val entry = createCanvas(title, template)
            _uiState.update { it.copy(showTemplatePicker = false) }
            _openCanvasEvents.emit(entry.id)
        }
    }

    fun cycleSortMode() {
        val next = when (_uiState.value.sortMode) {
            SortMode.Updated -> SortMode.Created
            SortMode.Created -> SortMode.Name
            SortMode.Name -> SortMode.Updated
        }
        _uiState.update { it.copy(sortMode = next) }
    }

    fun requestRename(entry: CanvasEntry) {
        _uiState.update { it.copy(renameTarget = entry) }
    }

    fun dismissRename() {
        _uiState.update { it.copy(renameTarget = null) }
    }

    fun rename(entry: CanvasEntry, title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            saveCanvas(entry.copy(title = title), refreshThumbnail = false)
            _uiState.update { it.copy(renameTarget = null) }
        }
    }

    fun requestDelete(entry: CanvasEntry) {
        _uiState.update { it.copy(deleteTarget = entry) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(deleteTarget = null) }
    }

    fun delete(entry: CanvasEntry) {
        viewModelScope.launch {
            deleteCanvas(entry.id)
            _uiState.update { it.copy(deleteTarget = null) }
        }
    }
}

