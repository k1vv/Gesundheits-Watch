
package com.example.exercisesamplecompose.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text

@Composable
fun DistanceText(distance: Double?) {
    Text(text = formatDistanceKm(distance))
}

@Preview
@Composable
fun DistanceTextPreview() {
    DistanceText(distance = 505.0)
}
