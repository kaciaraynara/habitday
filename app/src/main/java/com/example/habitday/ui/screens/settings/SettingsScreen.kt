package com.example.habitday.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.habitday.ui.components.HabitDayHeader
import com.example.habitday.ui.components.HabitinhoMascot
import com.example.habitday.ui.components.MascotMood
import com.example.habitday.ui.components.MascotStyle
import com.example.habitday.ui.components.ProfessionalButton
import com.example.habitday.ui.theme.*
import com.example.habitday.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onAbout: () -> Unit,
    onPrivacy: () -> Unit
) {
    val prefs by viewModel.preferences.collectAsState()
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateProfileImage(it.toString()) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações", fontWeight = androidx.compose.ui.text.font.FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                SettingsSectionHeader("Perfil", Icons.Default.Person)
                ProfileImageSection(
                    uri = prefs?.profileImageUri,
                    onSelectImage = { galleryLauncher.launch("image/*") }
                )
            }

            item {
                SettingsSectionHeader("Aparência", Icons.Default.Palette)
                SettingsAvatarSelector(
                    currentStyle = prefs?.avatarStyle ?: "CLASSICO",
                    onStyleSelected = { viewModel.updateAvatarStyle(it) }
                )
            }

            item {
                SettingsThemeSelector(
                    currentTheme = prefs?.themeMode ?: "SISTEMA",
                    onThemeSelected = { viewModel.updateTheme(it) }
                )
            }

            item {
                SettingsColorSelector(
                    title = "Cor de destaque",
                    selectedColor = prefs?.highlightColor ?: "#007AFF",
                    colors = listOf("#007AFF", "#102A43", "#6A1B9A", "#AD1457", "#E65100", "#00838F", "#FF6D00", "#FF4081", "#AA00FF", "#00B8D4", "#00BFA5"),
                    onColorSelected = { viewModel.updateHighlightColor(it) }
                )
            }

            item {
                SettingsColorSelector(
                    title = "Fundo (Tema Claro)",
                    selectedColor = prefs?.backgroundColor ?: "#FFFFFF",
                    colors = listOf("#FFFFFF", "#F5F7F9", "#FFFDF0", "#F0F7FF", "#F2FAF2"),
                    onColorSelected = { viewModel.updateBackgroundColor(it) }
                )
            }

            item {
                SettingsSectionHeader("Notificações", Icons.Default.Notifications)
                SettingsToggleItem(
                    label = "Ativar lembretes",
                    checked = prefs?.notificationsEnabled ?: true,
                    onCheckedChange = { viewModel.updateNotifications(it) }
                )
            }

            item {
                SettingsSectionHeader("Dados", Icons.Default.Palette)
                ProfessionalButton(
                    text = "Limpar todos os dados",
                    onClick = { viewModel.clearAllData() },
                    modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                    containerColor = ErrorRed,
                    contentColor = Color.White
                )
            }

            item {
                SettingsSectionHeader("Sobre", Icons.Default.Person)
                SettingsAboutItem(onAbout, onPrivacy)
            }
        }
    }
}

@Composable
fun ProfileImageSection(uri: String?, onSelectImage: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(SurfaceLight)
                .clickable { onSelectImage() },
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = TextSecondary
                )
            }
        }
        TextButton(onClick = onSelectImage) {
            Text(if (uri == null) "Adicionar foto" else "Trocar foto", color = BrandBlue)
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    }
}

@Composable
fun SettingsAvatarSelector(currentStyle: String, onStyleSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text("Estilo do Habitinho", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(listOf("CLASSICO", "CALMO", "ENERGETICO", "FOCADO", "MINIMALISTA")) { styleStr ->
                val mascotStyle = when(styleStr) {
                    "CALMO" -> MascotStyle.CALMO
                    "ENERGETICO" -> MascotStyle.ENERGETICO
                    "FOCADO" -> MascotStyle.FOCADO
                    "MINIMALISTA" -> MascotStyle.MINIMALISTA
                    else -> MascotStyle.CLASSICO
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onStyleSelected(styleStr) }
                ) {
                    HabitinhoMascot(
                        mood = MascotMood.IDLE,
                        size = 56.dp,
                        style = mascotStyle,
                        modifier = Modifier.border(
                            width = if (currentStyle == styleStr) 3.dp else 0.dp,
                            color = if (currentStyle == styleStr) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        ).padding(4.dp)
                    )
                    Text(
                        text = when(styleStr) {
                            "CLASSICO" -> "Clássico"
                            "CALMO" -> "Calmo"
                            "ENERGETICO" -> "Vibrante"
                            "FOCADO" -> "Focado"
                            "MINIMALISTA" -> "Minimalista"
                            else -> styleStr
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (currentStyle == styleStr) MaterialTheme.colorScheme.primary else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsThemeSelector(currentTheme: String, onThemeSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Tema", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("CLARO", "ESCURO", "SISTEMA").forEach { theme ->
                FilterChip(
                    selected = currentTheme == theme,
                    onClick = { onThemeSelected(theme) },
                    label = { 
                        Text(when(theme) {
                            "CLARO" -> "Claro"
                            "ESCURO" -> "Escuro"
                            "SISTEMA" -> "Sistema"
                            else -> theme
                        })
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun SettingsColorSelector(
    title: String,
    selectedColor: String,
    colors: List<String>,
    onColorSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(colors) { colorHex ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(colorHex)))
                        .border(
                            width = if (selectedColor == colorHex) 3.dp else 0.dp,
                            color = if (selectedColor == colorHex) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(colorHex) }
                )
            }
        }
    }
}

@Composable
fun SettingsToggleItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun SettingsAboutItem(onAbout: () -> Unit, onPrivacy: () -> Unit) {
    Column {
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clickable { onAbout() },
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("HabitDay v1.0", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text(
                    "Toque para saber mais sobre o projeto.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
        
        TextButton(
            onClick = onPrivacy,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text("Política de Privacidade", color = MaterialTheme.colorScheme.primary)
        }
        
        Text(
            text = "Seus dados de hábitos são armazenados localmente e nunca saem do seu dispositivo, exceto quando você utiliza a função de sincronização acadêmica.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}
