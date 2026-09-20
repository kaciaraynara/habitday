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

@Composable
fun HabitinhoMascot(
    mood: MascotMood,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    style: MascotStyle = MascotStyle.CLASSICO,
    colorOverride: Color? = null
) {
    val dropColor = colorOverride ?: when (style) {
        MascotStyle.CALMO -> Color(0xFF64B5F6)
        MascotStyle.ENERGETICO -> Color(0xFFF06292)
        MascotStyle.FOCADO -> Color(0xFF4DB6AC)
        MascotStyle.MINIMALISTA -> Color(0xFF90A4AE)
        else -> BrandBlue
    }

    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()

        // Gota principal (Corpo)
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            cubicTo(w * 0.4f, h * 0.2f, w * 0.1f, h * 0.5f, w * 0.1f, h * 0.7f)
            cubicTo(w * 0.1f, h * 0.95f, w * 0.9f, h * 0.95f, w * 0.9f, h * 0.7f)
            cubicTo(w * 0.9f, h * 0.5f, w * 0.6f, h * 0.2f, w * 0.5f, h * 0.1f)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(dropColor.copy(alpha = 0.9f), dropColor)
            )
        )

        // Borda branca do logo
        drawPath(
            path = path,
            color = Color.White,
            style = Stroke(width = w * 0.04f)
        )

        // Brilho na cabeça
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = w * 0.08f,
            center = Offset(w * 0.35f, h * 0.45f)
        )

        // Expressão (Fiel ao logo)
        val eyeY = h * 0.65f
        if (mood == MascotMood.SUCCESS) {
            // Piscadinha
            drawArc(
                color = Color.White,
                startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(w * 0.25f, eyeY - 5f), size = Size(w * 0.15f, h * 0.08f),
                style = Stroke(6f, cap = StrokeCap.Round)
            )
            drawCircle(color = Color.White, radius = 6f, center = Offset(w * 0.65f, eyeY))
        } else {
            drawCircle(color = Color.White, radius = 8f, center = Offset(w * 0.38f, eyeY))
            drawCircle(color = Color.White, radius = 8f, center = Offset(w * 0.62f, eyeY))
        }

        // Sorriso Amigável
        drawArc(
            color = Color.White,
            startAngle = 0f, sweepAngle = 180f, useCenter = false,
            topLeft = Offset(w * 0.42f, h * 0.72f), size = Size(w * 0.16f, h * 0.1f),
            style = Stroke(5f, cap = StrokeCap.Round)
        )
        
        // Mãozinha (Joinha do logo)
        if (mood == MascotMood.SUCCESS) {
             drawCircle(color = dropColor, radius = w * 0.1f, center = Offset(w * 0.15f, h * 0.65f))
             drawPath(
                path = Path().apply {
                    moveTo(w * 0.1f, h * 0.6f)
                    lineTo(w * 0.15f, h * 0.55f)
                    lineTo(w * 0.2f, h * 0.6f)
                },
                color = Color.White,
                style = Stroke(4f, cap = StrokeCap.Round)
             )
        }
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
