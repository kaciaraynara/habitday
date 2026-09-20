# HabitDay

**Pequenas escolhas, grandes mudanças.**

O HabitDay é um aplicativo profissional de acompanhamento de hábitos, desenvolvido como projeto final do Módulo Avançado de Android. Ele foca em uma experiência lúdica, intuitiva e tecnicamente sólida, utilizando as melhores práticas de desenvolvimento Android moderno.

## Objetivo
Auxiliar usuários a manterem a constância em suas rotinas através de lembretes, visualização de progresso e uma identidade visual acolhedora com o mascote **Habitinho**.

## Funcionalidades
- **Hoje:** Visão geral do dia, progresso real e conclusão rápida de hábitos.
- **Hábitos:** Gerenciamento completo (criar, editar, excluir, categorizar e definir frequências).
- **Progresso:** Histórico visual em calendário e estatísticas baseadas em dados reais.
- **Personalização:** Escolha de temas (Claro/Escuro), cores de destaque e estilos do mascote.
- **Notificações:** Lembretes locais personalizados para cada hábito.
- **Sincronização:** Backup de dados utilizando API externa (Retrofit).
- **Onboarding:** Apresentação curta e acolhedora para o primeiro uso.

## UX e Design
- **100% em Português (Brasil):** Interface humana e clara.
- **Zero Emojis:** Identidade visual baseada em ícones vetoriais e gráficos proprietários.
- **Identidade Própria:** Mascote original (Habitinho) que reage ao progresso do usuário.
- **Fundo Branco:** Design limpo e profissional focado no conteúdo.

## Arquitetura e Tecnologias
- **Jetpack Compose:** Interface declarativa e interativa.
- **Navigation Compose:** Fluxo de navegação fluido entre múltiplas telas.
- **Room Database:** Persistência local robusta (Hábitos, Registros, Preferências).
- **Retrofit:** Comunicação real com API externa (GET/POST).
- **ViewModel & Flow:** Gerenciamento de estado reativo.
- **Coroutines:** Operações assíncronas eficientes.
- **Material 3:** Design system moderno e acessível.

## Matriz de Requisitos Acadêmicos

| Requisito | Implementação (Arquivo/Local) | Função no Produto |
| :--- | :--- | :--- |
| **Mínimo 3 Telas** | `Hoje`, `Hábitos`, `Progresso`, `Configurações` | Core do aplicativo. |
| **Navigation Compose** | `navigation/AppNavigation.kt` | Navegação centralizada. |
| **Jetpack Compose** | `ui/screens/`, `ui/components/` | Toda a interface do usuário. |
| **Room Database** | `data/local/HabitDatabase.kt` | Persistência de dados. |
| **Room Entity** | `HabitEntity`, `HabitRecordEntity`, `UserPreferencesEntity` | Modelagem dos dados locais. |
| **Room DAO** | `HabitDao`, `HabitRecordDao`, `PreferencesDao` | Operações de banco (CRUD). |
| **Room (Leitura/Gravação)** | `HabitRepository.kt` | Persistência real comprovada. |
| **Retrofit (GET)** | `data/remote/HabitApi.kt` | Busca de sugestões de rotina. |
| **Retrofit (POST)** | `data/remote/HabitApi.kt` | Sincronização de progresso. |
| **ViewModel** | `viewmodel/` | Lógica de negócio e estado. |
| **Flow/Coroutines** | `viewmodel/`, `repository/` | Processamento reativo e assíncrono. |
| **Recurso Nativo** | `NotificationHelper.kt` | Notificações locais de lembretes. |

## Como Executar
1. Abra o projeto no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Execute o aplicativo em um emulador ou dispositivo físico (API 26+).
4. Siga o Onboarding e crie seu primeiro hábito!

---
**Desenvolvido por Raynara Kácia**
**Projeto Acadêmico - Android Avançado**
