package com.matedroid.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Custom icons not available in Material Icons Extended.
 * These are converted from Material Symbols (Google Fonts).
 */
object CustomIcons {
    /**
     * Road icon from Material Symbols Outlined.
     * Source: https://fonts.google.com/icons?icon.query=road
     */
    val Road: ImageVector by lazy {
        ImageVector.Builder(
            name = "Road",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Original SVG path with Y-axis transformation (viewBox was "0 -960 960 960")
                // M160-160v-640h80v640h-80Z -> left lane line
                moveTo(160f, 800f)
                verticalLineToRelative(-640f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(640f)
                close()

                // m280 0v-160h80v160h-80Z -> bottom center dashed line
                moveTo(440f, 800f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                close()

                // m280 0v-640h80v640h-80Z -> right lane line
                moveTo(720f, 800f)
                verticalLineToRelative(-640f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(640f)
                close()

                // M440-400v-160h80v160h-80Z -> middle center dashed line
                moveTo(440f, 560f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                close()

                // m0-240v-160h80v160h-80Z -> top center dashed line
                moveTo(440f, 320f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                close()
            }
        }.build()
    }

    /**
     * Steering wheel icon from Material Symbols Outlined (search_hands_free).
     * Source: https://fonts.google.com/icons?icon.query=search+hands+free
     */
    val SteeringWheel: ImageVector by lazy {
        ImageVector.Builder(
            name = "SteeringWheel",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1f, pathFillType = PathFillType.EvenOdd) {
                // Outer circle (converted y: -80 -> 880, -480 -> 480, -880 -> 80, etc.)
                // M480-80 -> M480,880
                moveTo(480f, 880f)
                // q-83 0-156-31.5T197-197 -> curves for circle
                quadToRelative(-83f, 0f, -156f, -31.5f)
                reflectiveQuadTo(197f, 763f)
                quadToRelative(-54f, -54f, -85.5f, -127f)
                reflectiveQuadTo(80f, 480f)
                quadToRelative(0f, -83f, 31.5f, -156f)
                reflectiveQuadTo(197f, 197f)
                quadToRelative(54f, -54f, 127f, -85.5f)
                reflectiveQuadTo(480f, 80f)
                quadToRelative(83f, 0f, 156f, 31.5f)
                reflectiveQuadTo(763f, 197f)
                quadToRelative(54f, 54f, 85.5f, 127f)
                reflectiveQuadTo(880f, 480f)
                quadToRelative(0f, 83f, -31.5f, 156f)
                reflectiveQuadTo(763f, 763f)
                quadToRelative(-54f, 54f, -127f, 85.5f)
                reflectiveQuadTo(480f, 880f)
                close()

                // Bottom left section: Zm-40-84v-120q-60-12-102-54t-54-102H164q12 109 89.5 185T440-164
                // -84 -> 796, -164 -> 796
                moveTo(440f, 796f)
                verticalLineToRelative(-120f)
                quadToRelative(-60f, -12f, -102f, -54f)
                reflectiveQuadToRelative(-54f, -102f)
                horizontalLineTo(164f)
                quadToRelative(12f, 109f, 89.5f, 185f)
                reflectiveQuadTo(440f, 796f)
                close()

                // Bottom right section: Zm80 0q109-12 186.5-89.5T796-440H676q-12 60-54 102t-102 54v120Z
                moveTo(520f, 796f)
                quadToRelative(109f, -12f, 186.5f, -89.5f)
                reflectiveQuadTo(796f, 520f)
                horizontalLineTo(676f)
                quadToRelative(-12f, 60f, -54f, 102f)
                reflectiveQuadToRelative(-102f, 54f)
                verticalLineToRelative(120f)
                close()

                // Top section with steering wheel grip: ZM164-520h116l120-120h160l120 120h116q-15-121-105-200.5T480-800q-121 0-211 79.5T164-520Z
                // -520 -> 440, -800 -> 160
                moveTo(164f, 440f)
                horizontalLineToRelative(116f)
                lineToRelative(120f, -120f)
                horizontalLineToRelative(160f)
                lineToRelative(120f, 120f)
                horizontalLineToRelative(116f)
                quadToRelative(-15f, -121f, -105f, -200.5f)
                reflectiveQuadTo(480f, 160f)
                quadToRelative(-121f, 0f, -211f, 79.5f)
                reflectiveQuadTo(164f, 440f)
                close()
            }
        }.build()
    }
}
