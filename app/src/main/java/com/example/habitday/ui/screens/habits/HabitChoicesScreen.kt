package com.example.habitday.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitday.ui.components.HabitDayHeader
import com.example.habitday.ui.theme.*

data class HabitPreset(
    val name: String,
    val category: String,
    val icon: ImageVector,
    val color: String,
    val isHydration: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitChoicesScreen(
    onChoiceSelected: (HabitPreset) -> Unit,
    onCustomHabit: () -> Unit,
    onBack: () -> Unit
) {
    val presets = listOf(
        HabitPreset("Beber Água", "Saúde", Icons.Default.WaterDrop, "#007AFF", true),
        HabitPreset("Exercícios", "Saúde", Icons.Default.FitnessCenter, "#4CAF50"),
        HabitPreset("Ler Livro", "Estudos", Icons.Default.Book, "#FFC107"),
        HabitPreset("Meditar", "Autocuidado", Icons.Default.SelfImprovement, "#6A1B9A")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escolha um Hábito") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BackgroundWhite)
        ) {
            HabitDayHeader(
                title = "Sugestões",
                subtitle = "Comece com algo simples para ganhar ritmo."
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(presets) { preset ->
                    PresetCard(preset) { onChoiceSelected(preset) }
                }
                
                item {
                    CustomChoiceCard(onCustomHabit)
                }
            }
        }
    }
}

@Composable
fun PresetCard(preset: HabitPreset, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() }
            .shadow(4.dp, MaterialTheme.shapes.medium),
        color = BackgroundWhite,
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(2.dp, BrandBlueLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(preset.color)).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    preset.icon,
                    contentDescription = null,
                    tint = Color(android.graphics.Color.parseColor(preset.color)),
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = preset.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CustomChoiceCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(2.dp, BorderBlue)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Personalizar",
                style = MaterialTheme.typography.titleMedium,
                color = BrandBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
