package com.example.habitday.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitday.ui.theme.*

enum class MascotMood {
    IDLE, PROGRESS, SUCCESS, EMPTY
}

enum class MascotStyle {
    CLASSICO, CALMO, ENERGETICO, FOCADO, MINIMALISTA
}

/**
 * Habitinho 2.0: O novo avatar original do HabitDay.
 * Um broto de crescimento lúdico que representa a evolução dos hábitos.
 */
@Composable
fun HabitinhoMascot(
    mood: MascotMood,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    style: MascotStyle = MascotStyle.CLASSICO,
    colorOverride: Color? = null
) {
    val mainColor = colorOverride ?: when (style) {
        MascotStyle.CALMO -> Color(0xFF64B5F6)
        MascotStyle.ENERGETICO -> Color(0xFFFF4081)
        MascotStyle.FOCADO -> Color(0xFF00BFA5)
        MascotStyle.MINIMALISTA -> Color(0xFF90A4AE)
        else -> BrandBlue
    }

    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()

        // Corpo do Broto (Base arredondada)
        val bodyPath = Path().apply {
            moveTo(w * 0.2f, h * 0.8f)
            quadraticTo(w * 0.5f, h * 0.95f, w * 0.8f, h * 0.8f)
            lineTo(w * 0.7f, h * 0.4f)
            quadraticTo(w * 0.5f, h * 0.3f, w * 0.3f, h * 0.4f)
            close()
        }

        drawPath(
            path = bodyPath,
            brush = Brush.verticalGradient(
                colors = listOf(mainColor.copy(alpha = 0.9f), mainColor)
            )
        )

        // Folhas no Topo (O símbolo do crescimento)
        val leafPath = Path().apply {
            moveTo(w * 0.5f, h * 0.35f)
            quadraticTo(w * 0.3f, h * 0.15f, w * 0.45f, h * 0.05f)
            quadraticTo(w * 0.55f, h * 0.15f, w * 0.5f, h * 0.35f)
            
            moveTo(w * 0.5f, h * 0.35f)
            quadraticTo(w * 0.7f, h * 0.15f, w * 0.55f, h * 0.05f)
            quadraticTo(w * 0.45f, h * 0.15f, w * 0.5f, h * 0.35f)
        }

        drawPath(
            path = leafPath,
            color = BrandYellow
        )

        // Olhos Expressivos
        val eyeY = h * 0.6f
        when (mood) {
            MascotMood.SUCCESS -> {
                // Piscada de vitória
                drawArc(
                    color = Color.White,
                    startAngle = 180f, sweepAngle = 180f, useCenter = false,
                    topLeft = Offset(w * 0.35f, eyeY - 5f), size = Size(w * 0.1f, h * 0.05f),
                    style = Stroke(4f, cap = StrokeCap.Round)
                )
                drawCircle(color = Color.White, radius = 6f, center = Offset(w * 0.6f, eyeY))
            }
            MascotMood.EMPTY -> {
                // Olhos pequenos (espera)
                drawCircle(color = Color.White, radius = 4f, center = Offset(w * 0.4f, eyeY))
                drawCircle(color = Color.White, radius = 4f, center = Offset(w * 0.6f, eyeY))
            }
            else -> {
                // Olhos normais
                drawCircle(color = Color.White, radius = 7f, center = Offset(w * 0.4f, eyeY))
                drawCircle(color = Color.White, radius = 7f, center = Offset(w * 0.6f, eyeY))
            }
        }

        // Sorriso
        drawArc(
            color = Color.White,
            startAngle = 0f, sweepAngle = 180f, useCenter = false,
            topLeft = Offset(w * 0.45f, h * 0.7f), size = Size(w * 0.1f, h * 0.05f),
            style = Stroke(3f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun HabitDayLogo(
    modifier: Modifier = Modifier,
    mascotStyle: MascotStyle = MascotStyle.CLASSICO,
    sizeMultiplier: Float = 1f
) {
    Surface(
        modifier = modifier,
        color = BackgroundWhite,
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke((2 * sizeMultiplier).dp, BrandBlue.copy(alpha = 0.2f)),
        shadowElevation = (2 * sizeMultiplier).dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = (16 * sizeMultiplier).dp, vertical = (8 * sizeMultiplier).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HabitinhoMascot(mood = MascotMood.IDLE, size = (28 * sizeMultiplier).dp, style = mascotStyle)
            Spacer(modifier = Modifier.width((12 * sizeMultiplier).dp))
            Text(
                text = "HabitDay",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = (22 * sizeMultiplier).sp),
                fontWeight = FontWeight.Black,
                color = BrandBlue,
                letterSpacing = (-1).sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HabitDaySlogan(modifier: Modifier = Modifier) {
    Text(
        text = "Pequenas escolhas, grandes mudanças.",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}
