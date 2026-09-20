package com.example.habitday.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitday.HabitApplication
import com.example.habitday.ui.components.*
import com.example.habitday.ui.theme.*
import com.example.habitday.viewmodel.HomeViewModel
import com.example.habitday.viewmodel.SettingsViewModel
import com.example.habitday.viewmodel.SettingsViewModelFactory
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    application: HabitApplication,
    onAddHabit: () -> Unit,
    onSettings: () -> Unit,
    mascotStyle: MascotStyle = MascotStyle.CLASSICO
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(application.repository))
    val prefs by settingsViewModel.preferences.collectAsState()

    var showShakeTip by remember { mutableStateOf(false) }
    var shakeTipText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        application.sensorHelper.startListening {
            val tips = listOf(
                "Tente beber um copo de água agora!",
                "Que tal 5 minutos de alongamento?",
                "Respire fundo por 30 segundos.",
                "Sua constância é sua maior força!",
                "Um pequeno passo hoje é um grande salto amanhã."
            )
            shakeTipText = tips.random()
            showShakeTip = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { application.sensorHelper.stopListening() }
    }

    if (showShakeTip) {
        AlertDialog(
            onDismissRequest = { showShakeTip = false },
            title = { Text("Dica do Habitinho", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
            text = { Text(shakeTipText) },
            confirmButton = {
                TextButton(onClick = { showShakeTip = false }) {
                    Text("Entendido", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }

    val mascotMood = when {
        uiState.totalCount == 0 -> MascotMood.EMPTY
        uiState.completionPercentage == 100 -> MascotMood.SUCCESS
        else -> MascotMood.IDLE
    }

    Scaffold(
        containerColor = Color.Transparent, 
        topBar = {
            CenterAlignedTopAppBar(
                title = { HabitDayLogo(mascotStyle = mascotStyle) },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings, 
                            contentDescription = "Configurações", 
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onAddHabit,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar hábito", modifier = Modifier.size(32.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                HabitDayHeader(
                    title = "HabitDay",
                    subtitle = "$greeting, ${uiState.dateText}",
                    showMascot = true,
                    mood = mascotMood,
                    mascotStyle = mascotStyle,
                    profileImageUri = prefs?.profileImageUri
                )
            }

            item {
                HabitProgressBar(
                    completed = uiState.completedCount,
                    total = uiState.totalCount,
                    percentage = uiState.completionPercentage
                )
                if (uiState.totalCount > 0) {
                    Text(
                        text = "Hoje",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 12.dp)
                    )
                }
            }

            item {
                MascotDialogue(uiState.completionPercentage, mascotMood)
            }

            if (uiState.habits.isEmpty()) {
                item {
                    EmptyStateWithAnimation()
                }
            } else {
                itemsIndexed(uiState.habits) { index, item ->
                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(index * 50L)
                        isVisible = true
                    }
                    
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                    ) {
                        if (item.habit.isHydration) {
                            HydrationCard(
                                name = item.habit.name,
                                current = item.hydrationAmount,
                                goal = item.habit.hydrationGoal,
                                onAdd = { viewModel.updateHydration(item.habit.id, 1) },
                                onRemove = { viewModel.updateHydration(item.habit.id, -1) }
                            )
                        } else {
                            HabitItemCard(
                                name = item.habit.name,
                                time = item.habit.reminderTime,
                                isCompleted = item.isCompleted,
                                color = item.habit.color,
                                onToggle = { viewModel.toggleHabitCompletion(item.habit.id, !item.isCompleted) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MascotDialogue(percentage: Int, mood: MascotMood) {
    val message = when {
        percentage == 100 -> "Incrível! Você completou todos os seus hábitos hoje!"
        percentage > 50 -> "Você está indo muito bem, continue assim!"
        percentage > 0 -> "Ótimo começo! Vamos completar mais um?"
        else -> "Seu dia está apenas começando. Vamos planejar algo bom?"
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.medium),
        color = SurfaceLight,
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBlue.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HabitinhoMascot(mood = mood, size = 48.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HomeHeroSection(percentage: Int, mood: MascotMood, style: MascotStyle) {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HabitinhoMascot(mood = mood, style = style, size = 80.dp)
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = if (percentage == 100) "Dia incrível!" else "Sua evolução",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White
                )
                Text(
                    text = "$percentage% concluído hoje",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun HabitItemCard(
    name: String,
    time: String?,
    isCompleted: Boolean,
    color: String,
    onToggle: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isCompleted) 0.dp else 4.dp,
        label = "elevation"
    )
    
    val accentColor = try { 
        Color(android.graphics.Color.parseColor(color)) 
    } catch (e: Exception) { 
        MaterialTheme.colorScheme.primary 
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(32.dp).border(2.dp, accentColor, CircleShape),
                color = if (isCompleted) accentColor else Color.Transparent,
                shape = CircleShape
            ) {
                if (isCompleted) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isCompleted) TextSecondary else TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                if (time != null) {
                    Text(text = time, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                }
            }
        }
    }
}
