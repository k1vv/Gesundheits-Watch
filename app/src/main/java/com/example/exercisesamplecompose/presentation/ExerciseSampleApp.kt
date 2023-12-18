@file:OptIn(ExperimentalHorologistApi::class)

package com.example.exercisesamplecompose.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.exercisesamplecompose.app.Screen.Exercise
import com.example.exercisesamplecompose.app.Screen.ExerciseNotAvailable
import com.example.exercisesamplecompose.app.Screen.PreparingExercise
import com.example.exercisesamplecompose.app.Screen.Summary
import com.example.exercisesamplecompose.app.navigateToTopLevel
import com.example.exercisesamplecompose.presentation.dialogs.ExerciseNotAvailable
import com.example.exercisesamplecompose.presentation.exercise.ExerciseRoute
import com.example.exercisesamplecompose.presentation.preparing.PreparingExerciseRoute
import com.example.exercisesamplecompose.presentation.summary.SummaryRoute
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scrollable

/** Navigation for the exercise app. **/
@Composable
fun ExerciseSampleApp(
    navController: NavHostController,
    onFinishActivity: () -> Unit
) {
    val currentScreen by navController.currentBackStackEntryAsState()

    val isAlwaysOnScreen = currentScreen?.destination?.route in AlwaysOnRoutes

    AmbientAware(
        isAlwaysOnScreen = isAlwaysOnScreen
    ) { ambientStateUpdate ->

        WearNavScaffold(
            navController = navController,
            startDestination = Exercise.route,
            timeText = {
                if (ambientStateUpdate.ambientState is AmbientState.Interactive) {
                    TimeText(
                        modifier = it,
                    )
                }
            }
        ) {
            composable(PreparingExercise.route) {
                PreparingExerciseRoute(
                    ambientState = ambientStateUpdate.ambientState,
                    onStart = {
                        navController.navigate(Exercise.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = false
                            }
                        }
                    },
                    onNoExerciseCapabilities = {
                        navController.navigate(ExerciseNotAvailable.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = false
                            }
                        }
                    },
                    onFinishActivity = onFinishActivity
                )
            }

            scrollable(Exercise.route) {
                ExerciseRoute(
                    ambientState = ambientStateUpdate.ambientState,
                    columnState = it.columnState,
                    onSummary = {
                        navController.navigateToTopLevel(Summary, Summary.buildRoute(it))
                    }
                )
            }

            composable(ExerciseNotAvailable.route) {
                ExerciseNotAvailable()
            }

            scrollable(
                Summary.route + "/{averageHeartRate}/{totalDistance}/{totalCalories}/{elapsedTime}",
                arguments = listOf(
                    navArgument(Summary.averageHeartRateArg) { type = NavType.FloatType },
                    navArgument(Summary.totalDistanceArg) { type = NavType.FloatType },
                    navArgument(Summary.totalCaloriesArg) { type = NavType.FloatType },
                    navArgument(Summary.elapsedTimeArg) { type = NavType.StringType }
                )
            ) {
                SummaryRoute(
                    columnState = it.columnState,
                    onRestartClick = {
                        navController.navigateToTopLevel(PreparingExercise)
                    }
                )
            }
        }
    }
}

val AlwaysOnRoutes = listOf(PreparingExercise.route, Exercise.route)


