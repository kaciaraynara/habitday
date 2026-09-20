package com.example.habitday.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habitday.ui.theme.*

@Composable
fun EmptyStateWithAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HabitinhoMascot(
            mood = MascotMood.EMPTY,
            size = 120.dp,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Seu dia começa aqui",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Adicione um hábito para começar a acompanhar sua rotina.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )
    }
}

@Composable
fun HabitEmptyState(
    message: String,
    subMessage: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(32.dp))
            ProfessionalButton(text = actionLabel, onClick = onAction)
        }
    }
}

@Composable
fun HabitLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = BrandBlue,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun HabitErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            color = ErrorRed
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) {
            Text("Tentar novamente", color = BrandBlue)
        }
    }
}
