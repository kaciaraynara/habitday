package com.example.habitday.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.habitday.ui.theme.*
import kotlin.math.sin

@Composable
fun AnimatedWaterCup(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Canvas(modifier = modifier.size(size)) {
        val width = size.toPx()
        val height = size.toPx()
        
        val cupPath = Path().apply {
            moveTo(width * 0.15f, height * 0.1f)
            lineTo(width * 0.25f, height * 0.9f)
            lineTo(width * 0.75f, height * 0.9f)
            lineTo(width * 0.85f, height * 0.1f)
            close()
        }
        
        drawPath(
            path = cupPath,
            color = Color(0xFF007AFF).copy(alpha = 0.2f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
        )

        clipPath(cupPath) {
            val waterLevelY = height * (0.9f - (animatedProgress * 0.8f))
            
            val waterPath = Path().apply {
                moveTo(0f, waterLevelY)
                for (x in 0..width.toInt()) {
                    val y = waterLevelY + sin(x * 0.08f + waveOffset) * 8f
                    lineTo(x.toFloat(), y)
                }
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            
            drawPath(
                path = waterPath,
                color = Color(0xFF007AFF).copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun HydrationCard(
    name: String,
    current: Int,
    goal: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    val progress = if (goal > 0) current.toFloat() / goal else 0f
    
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(BackgroundWhite)
            .border(2.dp, BrandBlueLight, MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedWaterCup(progress = progress.coerceIn(0f, 1f), size = 64.dp)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BrandBlue)
                Text(text = "$current de $goal copos", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp).background(BrandBlueLight, CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = onAdd,
                    modifier = Modifier.size(40.dp).background(BrandBlue, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
