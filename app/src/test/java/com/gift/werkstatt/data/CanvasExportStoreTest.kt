package com.gift.werkstatt.data

import com.gift.werkstatt.data.files.buildExportFileName
import org.junit.Assert.assertEquals
import org.junit.Test

class CanvasExportStoreTest {
    @Test
    fun buildExportFileNameNormalizesTitle() {
        assertEquals("My_Canvas_1_123.png", buildExportFileName("My Canvas #1", 123L))
    }

    @Test
    fun buildExportFileNameFallsBackForBlankTitle() {
        assertEquals("Untitled_42.png", buildExportFileName("   ", 42L))
    }
}

