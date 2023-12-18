@file:OptIn(ExperimentalHorologistApi::class)

package com.example.exercisesamplecompose.presentation.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.example.exercisesamplecompose.R
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.AlertDialog


@Composable
fun ExerciseInProgressAlert(
    onNegative: () -> Unit,
    onPositive: () -> Unit,
    showDialog: Boolean,
) {
    AlertDialog(
        title = stringResource(id = R.string.exercise_in_progress),
        message = stringResource(id = R.string.ending_continue),
        onCancelButtonClick = onNegative,
        onOKButtonClick = onPositive,
        showDialog = showDialog,
        scalingLazyListState = rememberScalingLazyListState()
    )
}

@WearPreviewDevices
@Composable
fun ExerciseInProgressAlertPreview() {
    ExerciseInProgressAlert(onNegative = {}, onPositive = {}, showDialog = true)
}

