package com.gift.werkstatt.core.navigation

sealed interface WerkstattRoute {
    val route: String

    data object Gallery : WerkstattRoute {
        override val route = "gallery"
    }

    data object Editor : WerkstattRoute {
        const val ARG_CANVAS_ID = "canvasId"
        override val route = "editor/{$ARG_CANVAS_ID}"

        fun create(canvasId: String) = "editor/$canvasId"
    }
}

