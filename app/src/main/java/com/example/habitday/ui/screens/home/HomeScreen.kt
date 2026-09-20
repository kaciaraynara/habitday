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
import kotlinx.coroutines.delay

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
                "Vamos regar seus hábitos hoje?",
                "Pequenos brotos levam tempo para crescer.",
                "Sua constância é como o sol para seu progresso.",
                "Um passo de cada vez, sem pressa.",
                "O Habitinho está orgulhoso da sua jornada!"
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
            title = { Text("Dica do Habitinho", color = BrandBlue, fontWeight = FontWeight.Bold) },
            text = { Text(shakeTipText) },
            confirmButton = {
                TextButton(onClick = { showShakeTip = false }) {
                    Text("Entendido", color = BrandBlue)
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

    // BACKGROUND PERSONALIZÁVEL (Pedido: Fundo branco e escolha de cores)
    val bgColor = try { 
        Color(android.graphics.Color.parseColor(prefs?.backgroundColor ?: "#FFFFFF")) 
    } catch (e: Exception) { 
        BackgroundWhite 
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { HabitDayLogo(mascotStyle = mascotStyle) },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings, 
                            contentDescription = "Configurações", 
                            tint = TextPrimary
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
                containerColor = BrandBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar", modifier = Modifier.size(32.dp))
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
                HomeHeroSection(uiState.completionPercentage, mascotMood, mascotStyle)
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
                        delay(index * 50L)
                        isVisible = true
                    }
                    
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 30 })
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
        percentage == 100 -> "Incrível! Tudo floresceu hoje!"
        percentage > 50 -> "Continue cuidando da sua rotina!"
        percentage > 0 -> "Ótimo começo! Vamos crescer mais?"
        else -> "Seu dia é como terra fértil. O que vamos plantar?"
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.medium),
        color = BackgroundWhite,
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
            containerColor = BrandBlue.copy(alpha = 0.05f)
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
                    text = if (percentage == 100) "Dia Concluído!" else "Sua Jornada",
                    style = MaterialTheme.typography.headlineSmall,
                    color = BrandBlue,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                    color = BrandBlue,
                    trackColor = Color.White
                )
                Text(
                    text = "$percentage% da evolução",
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandBlue,
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
        BrandBlue 
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
            containerColor = if (isCompleted) SurfaceLight.copy(alpha = 0.5f) else BackgroundWhite
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (isCompleted) null else androidx.compose.foundation.BorderStroke(2.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) accentColor else Color.Transparent)
                    .border(2.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
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
