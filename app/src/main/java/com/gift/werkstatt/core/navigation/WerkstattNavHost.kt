package com.gift.werkstatt.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gift.werkstatt.feature.editor.CanvasEditorRoute
import com.gift.werkstatt.feature.gallery.GalleryRoute

@Composable
fun WerkstattNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = WerkstattRoute.Gallery.route
    ) {
        composable(WerkstattRoute.Gallery.route) {
            GalleryRoute(
                onOpenCanvas = { canvasId ->
                    navController.navigate(WerkstattRoute.Editor.create(canvasId))
                }
            )
        }
        composable(
            route = WerkstattRoute.Editor.route,
            arguments = listOf(navArgument(WerkstattRoute.Editor.ARG_CANVAS_ID) { type = NavType.StringType })
        ) {
            CanvasEditorRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

