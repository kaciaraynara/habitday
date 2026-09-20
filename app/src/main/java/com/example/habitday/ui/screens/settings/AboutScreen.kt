package com.example.habitday.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habitday.ui.components.HabitDayHeader
import com.example.habitday.ui.theme.TextPrimary
import com.example.habitday.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre o HabitDay") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HabitDayHeader(
                title = "HabitDay",
                subtitle = "v1.0.0",
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Text(
                text = "Pequenas escolhas, grandes mudanças.",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "O HabitDay foi desenvolvido para ajudar você a construir uma rotina mais saudável e consciente. Através da persistência real de dados e uma interface intuitiva, cada hábito registrado é um passo em direção aos seus objetivos.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Desenvolvido com Jetpack Compose, Room e Retrofit como projeto final de Android Avançado.",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = TextSecondary.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
