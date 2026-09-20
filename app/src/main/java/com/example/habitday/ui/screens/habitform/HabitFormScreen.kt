package com.example.habitday.ui.screens.habitform

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habitday.data.local.HabitEntity
import com.example.habitday.data.local.NotificationHelper
import com.example.habitday.ui.components.ProfessionalButton
import com.example.habitday.ui.theme.*
import com.example.habitday.viewmodel.HabitViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormScreen(
    habitId: Long,
    viewModel: HabitViewModel,
    notificationHelper: NotificationHelper,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Diário") }
    var category by remember { mutableStateOf("Geral") }
    var isHydration by remember { mutableStateOf(false) }
    var hydrationGoal by remember { mutableStateOf("8") }
    var reminderTime by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#007AFF") }
    
    var existingHabit by remember { mutableStateOf<HabitEntity?>(null) }

    LaunchedEffect(habitId) {
        if (habitId != -1L) {
            val habit = viewModel.getHabitById(habitId)
            habit?.let {
                existingHabit = it
                name = it.name
                description = it.description
                frequency = it.frequency
                category = it.category
                isHydration = it.isHydration
                hydrationGoal = it.hydrationGoal.toString()
                reminderTime = it.reminderTime ?: ""
                selectedColor = it.color
            }
        }
    }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            TopAppBar(
                title = { Text(if (habitId == -1L) "Novo hábito" else "Editar hábito", fontWeight = androidx.compose.ui.text.font.FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome do hábito") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    focusedLabelColor = BrandBlue
                )
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    focusedLabelColor = BrandBlue
                )
            )

            Column {
                Text("Categoria", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Saúde", "Estudos", "Trabalho", "Casa", "Autocuidado", "Outro")) { option ->
                        FilterChip(
                            selected = category == option,
                            onClick = { category = option },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Column {
                Text("Frequência", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Diário", "Semanal", "Mensal").forEach { option ->
                        FilterChip(
                            selected = frequency == option,
                            onClick = { frequency = option },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hábito de Hidratação?", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Switch(
                        checked = isHydration,
                        onCheckedChange = { isHydration = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandBlue)
                    )
                }
                
                if (isHydration) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = hydrationGoal,
                        onValueChange = { hydrationGoal = it.filter { char -> char.isDigit() } },
                        label = { Text("Meta diária (copos)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            focusedLabelColor = BrandBlue
                        )
                    )
                }
            }

            OutlinedTextField(
                value = reminderTime,
                onValueChange = { reminderTime = it },
                label = { Text("Horário do lembrete (ex: 08:00)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    focusedLabelColor = BrandBlue
                )
            )

            Column {
                Text("Cor", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    listOf("#007AFF", "#102A43", "#4CAF50", "#FFC107", "#E53935").forEach { colorHex ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                .border(
                                    width = if (selectedColor == colorHex) 3.dp else 0.dp,
                                    color = if (selectedColor == colorHex) Color.Gray else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ProfessionalButton(
                text = if (habitId == -1L) "Criar hábito" else "Salvar alterações",
                onClick = {
                    if (name.isNotBlank()) {
                        scope.launch {
                            val goalInt = hydrationGoal.toIntOrNull() ?: 8
                            if (habitId == -1L) {
                                val newId = viewModel.insertHabit(
                                    name, description, frequency, 
                                    reminderTime.ifBlank { null }, category, "H", selectedColor,
                                    isHydration = isHydration, hydrationGoal = goalInt
                                )
                                if (reminderTime.isNotBlank()) {
                                    notificationHelper.scheduleNotification(newId, name, reminderTime)
                                }
                            } else {
                                existingHabit?.let {
                                    val updated = it.copy(
                                        name = name,
                                        description = description,
                                        frequency = frequency,
                                        category = category,
                                        isHydration = isHydration,
                                        hydrationGoal = goalInt,
                                        reminderTime = reminderTime.ifBlank { null },
                                        color = selectedColor
                                    )
                                    viewModel.updateHabit(updated)
                                    if (reminderTime.isNotBlank()) {
                                        notificationHelper.scheduleNotification(it.id, name, reminderTime)
                                    } else {
                                        notificationHelper.cancelNotification(it.id)
                                    }
                                }
                            }
                            onBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
