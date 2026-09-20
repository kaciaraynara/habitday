package com.example.habitday.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitday.ui.components.HabitinhoMascot
import com.example.habitday.ui.components.MascotMood
import com.example.habitday.ui.components.ProfessionalButton
import com.example.habitday.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(0) }
    
    val title = when(step) {
        0 -> "Bem-vinda ao HabitDay"
        1 -> "Crie sua rotina"
        2 -> "Acompanhe sua evolução"
        else -> ""
    }
    
    val description = when(step) {
        0 -> "Pequenas escolhas, grandes mudanças. Vamos começar sua jornada de hábitos hoje."
        1 -> "Adicione hábitos personalizados e defina lembretes para nunca esquecer de cuidar de você."
        2 -> "Visualize seu progresso e comemore cada pequena vitória com o Habitinho."
        else -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HabitinhoMascot(
            mood = when(step) {
                2 -> MascotMood.SUCCESS
                else -> MascotMood.IDLE
            },
            size = 180.dp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        ProfessionalButton(
            text = if (step < 2) "Próximo" else "Começar",
            onClick = {
                if (step < 2) step++ else onComplete()
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (i == step) BrandBlue else BorderBlue,
                            CircleShape
                        )
                )
            }
        }
    }
}
