package com.example.habitday.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habitday.ui.theme.TextPrimary
import com.example.habitday.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacidade") },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Sua privacidade é nossa prioridade",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "O HabitDay foi projetado com o princípio de 'Privacy by Design'. Isso significa que temos controle total sobre seus dados:",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            PrivacyItem(
                title = "Armazenamento Local",
                description = "Todos os seus hábitos e registros são salvos exclusivamente no banco de dados Room do seu dispositivo Android. Eles não são enviados para nenhum servidor sem sua ação explícita."
            )
            
            PrivacyItem(
                title = "Sincronização",
                description = "Ao utilizar a função de sincronização, apenas os dados necessários para o backup acadêmico são enviados via Retrofit para a API de demonstração (DummyJSON)."
            )
            
            PrivacyItem(
                title = "Notificações",
                description = "Nossos lembretes são processados localmente pelo AlarmManager do Android. Não rastreamos se você clicou ou não na notificação para fins de marketing."
            )
        }
    }
}

@Composable
fun PrivacyItem(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = description, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
    }
}
