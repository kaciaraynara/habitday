package com.example.habitday.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitday.HabitApplication
import com.example.habitday.ui.components.*
import com.example.habitday.ui.theme.*
import com.example.habitday.viewmodel.ApiState
import com.example.habitday.viewmodel.ProgressViewModel
import com.example.habitday.viewmodel.SettingsViewModel
import com.example.habitday.viewmodel.SettingsViewModelFactory
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    application: HabitApplication,
    mascotStyle: MascotStyle = MascotStyle.CLASSICO
) {
    val todosState by viewModel.todosState.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val totalHabits by viewModel.totalHabits.collectAsState(initial = 0)
    
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(application.repository))
    val prefs by settingsViewModel.preferences.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchExternalData()
    }

    val bgColor = try { 
        Color(android.graphics.Color.parseColor(prefs?.backgroundColor ?: "#FFFFFF")) 
    } catch (e: Exception) { 
        BackgroundWhite 
    }

    Scaffold(
        containerColor = bgColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                HabitDayHeader(
                    title = "Evolução",
                    subtitle = "Cada hábito concluído é um passo para o seu melhor eu.",
                    showMascot = true,
                    mood = MascotMood.IDLE,
                    mascotStyle = mascotStyle,
                    profileImageUri = prefs?.profileImageUri
                )
            }

            item {
                UserSummarySection(totalHabits)
            }

            item {
                ProgressSummaryCard(mascotStyle)
            }

            item {
                Text(
                    "Histórico Recente",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                RecentCalendarView()
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = BrandBlue)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Dicas de Produtividade", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }

            when (val state = todosState) {
                is ApiState.Loading -> {
                    item { Box(modifier = Modifier.fillMaxWidth().height(150.dp)) { HabitLoadingState() } }
                }
                is ApiState.Success -> {
                    val tips = state.data.map { todo ->
                        when {
                            todo.todo.contains("water", ignoreCase = true) -> "A hidratação é a base da sua energia diária."
                            todo.todo.contains("walk", ignoreCase = true) -> "Caminhar limpa a mente e fortalece o corpo."
                            todo.todo.contains("book", ignoreCase = true) -> "A leitura diária constrói sabedoria a longo prazo."
                            todo.todo.contains("sleep", ignoreCase = true) -> "O sono de qualidade é essencial para a constância."
                            else -> "Mantenha o foco e comemore cada pequena vitória."
                        }
                    }.distinct()
                    
                    items(tips.take(5)) { tip ->
                        TipItem(tip)
                    }
                    
                    item {
                        SyncActionSection(syncState, onSync = { viewModel.syncHabit("Meu Progresso") })
                    }
                }
                is ApiState.Error -> {
                    item {
                        HabitErrorState(
                            message = "Não foi possível carregar as sugestões.",
                            onRetry = { viewModel.fetchExternalData() }
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun UserSummarySection(total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryMiniCard(
            label = "Hábitos ativos",
            value = total.toString(),
            modifier = Modifier.weight(1f)
        )
        SummaryMiniCard(
            label = "Nível",
            value = (total / 2 + 1).toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryMiniCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = BrandBlue, fontWeight = FontWeight.Black)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun RecentCalendarView() {
    val today = LocalDate.now()
    val lastSevenDays = (0..6).map { today.minusDays(it.toLong()) }.reversed()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(lastSevenDays) { date ->
            DayItem(date = date, isToday = date == today)
        }
    }
}

@Composable
fun DayItem(date: LocalDate, isToday: Boolean) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
    val dayNumber = date.dayOfMonth.toString()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(if (isToday) BrandBlue else SurfaceLight)
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .width(44.dp)
    ) {
        Text(
            text = dayName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) Color.White else TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = dayNumber,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = if (isToday) Color.White else TextPrimary
        )
    }
}

@Composable
fun ProgressSummaryCard(mascotStyle: MascotStyle) {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Constância e Foco",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Sua jornada visual começará a ganhar forma aqui conforme você completa seus hábitos diários. Mantenha o ritmo!",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun SyncActionSection(state: ApiState<String>, onSync: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 16.dp))
        Text(
            "Sincronização Cloud",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Salve seus dados em nossa nuvem acadêmica para garantir a integridade do seu progresso.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        when (state) {
            is ApiState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
            is ApiState.Success -> Text("Backup realizado com sucesso!", color = SuccessGreen, fontWeight = FontWeight.Bold)
            is ApiState.Error -> Text("Falha na sincronização. Tente novamente.", color = ErrorRed)
            else -> {
                ProfessionalButton(
                    text = "Sincronizar agora",
                    onClick = onSync,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun TipItem(text: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
