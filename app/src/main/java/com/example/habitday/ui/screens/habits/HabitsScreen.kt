package com.example.habitday.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habitday.data.local.HabitEntity
import com.example.habitday.ui.components.HabitDayHeader
import com.example.habitday.ui.components.HabitEmptyState
import com.example.habitday.ui.theme.*
import com.example.habitday.viewmodel.HabitViewModel

@Composable
fun HabitsScreen(
    viewModel: HabitViewModel,
    onAddHabit: () -> Unit,
    onEditHabit: (Long) -> Unit
) {
    val habits by viewModel.allHabits.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }

    if (habitToDelete != null) {
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Excluir hábito?") },
            text = { Text("Os registros relacionados também serão removidos. Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        habitToDelete?.let { viewModel.deleteHabit(it) }
                        habitToDelete = null
                    }
                ) {
                    Text("Excluir", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                containerColor = BrandBlue,
                contentColor = BackgroundWhite,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar hábito")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                HabitDayHeader(
                    title = "Hábitos",
                    subtitle = "Organize sua rotina com foco e clareza."
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    label = { Text("Pesquisar hábitos") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        focusedLabelColor = BrandBlue
                    )
                )
            }

            if (habits.isEmpty()) {
                item {
                    HabitEmptyState(
                        message = "Você ainda não tem hábitos",
                        subMessage = "Crie uma rotina que faça sentido para você começando pelo primeiro hábito.",
                        actionLabel = "Adicionar hábito",
                        onAction = onAddHabit
                    )
                }
            } else {
                items(habits) { habit ->
                    HabitManageCard(
                        habit = habit,
                        onEdit = { onEditHabit(habit.id) },
                        onDelete = { habitToDelete = habit },
                        onDuplicate = { viewModel.duplicateHabit(habit) }
                    )
                }
            }
        }
    }
}

@Composable
fun HabitManageCard(
    habit: HabitEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val accentColor = try {
        Color(android.graphics.Color.parseColor(habit.color))
    } catch (e: Exception) {
        BrandBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(2.dp, BrandBlueLight)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(accentColor.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (habit.isHydration) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                } else {
                    Box(modifier = Modifier.size(12.dp).background(accentColor, androidx.compose.foundation.shape.CircleShape))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = "${habit.category} • ${habit.frequency}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções", tint = TextSecondary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        onClick = { showMenu = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicar") },
                        onClick = { showMenu = false; onDuplicate() },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Excluir", color = ErrorRed) },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed) }
                    )
                }
            }
        }
    }
}
