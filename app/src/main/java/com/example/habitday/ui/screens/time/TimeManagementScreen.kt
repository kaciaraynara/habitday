package com.example.habitday.ui.screens.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitday.ui.components.HabitDayHeader
import com.example.habitday.ui.theme.*

data class TimeTip(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeManagementScreen(onBack: () -> Unit) {
    val tips = listOf(
        TimeTip("Técnica Pomodoro", "Divida o trabalho em blocos de 25 minutos com 5 de descanso. Isso mantém o foco e evita a exaustão.", Icons.Default.HourglassTop),
        TimeTip("Regra dos 2 Minutos", "Se algo leva menos de 2 minutos para ser feito, faça agora. Isso evita o acúmulo de pequenas tarefas.", Icons.Default.HourglassTop),
        TimeTip("Bloqueio de Tempo", "Reserve horários fixos na sua agenda para tarefas específicas. Trate esses blocos como compromissos inadiáveis.", Icons.Default.HourglassTop),
        TimeTip("Priorização ABC", "Classifique suas tarefas por importância. Foque primeiro no 'A' (crítico) antes de olhar para o 'C'.", Icons.Default.HourglassTop)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestão de Tempo", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BackgroundWhite),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                HabitDayHeader(
                    title = "Tempo & Foco",
                    subtitle = "Dominar seu tempo é o segredo para a constância nos hábitos."
                )
            }

            items(tips) { tip ->
                TipCard(tip)
            }
        }
    }
}

@Composable
fun TipCard(tip: TimeTip) {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BrandBlueLight.copy(alpha = 0.4f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(tip.icon, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(text = tip.title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = tip.description, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }
        }
    }
}
