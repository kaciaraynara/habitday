# Plano de Implementação: Perfil, Segurança e Notificações Avançadas

Este plano detalha as melhorias para tornar o HabitDay um ecossistema de hábitos completo, com foco na autonomia do usuário, segurança de dados e interatividade persistente.

## User Review Required

- **Notificações em Segundo Plano**: As notificações serão gerenciadas pelo `WorkManager` para garantir que lembretes sejam reagendados mesmo após o reinício do dispositivo.
- **Acesso à Câmera**: Implementaremos o seletor nativo para que o usuário escolha entre tirar uma foto nova ou usar a galeria.
- **Segurança**: A "verificação de senha" será feita via diálogo de confirmação antes de permitir a troca.

## Mudanças Propostas

### 1. Gestão de Perfil e Segurança
Refatoração da tela de Configurações para incluir uma área dedicada ao usuário.
- **Funcionalidades**:
    - Troca de senha com validação da senha atual.
    - Resumo do usuário (Data de cadastro, total de hábitos).
    - Logout seguro (limpa sessão e volta para tela de Login).

### 2. Recursos Nativos (Câmera e Galeria)
Expansão do sistema de foto de perfil.
- **Funcionalidades**:
    - Botão de "Editar Foto" que abre escolha entre Câmera e Galeria.
    - Persistência da imagem no banco de dados.

### 3. Notificações Persistentes (WorkManager)
Garantir que os lembretes nunca parem de aparecer.
- **Funcionalidades**:
    - `BootReceiver` para reagendar lembretes após o celular ligar.
    - Opção clara nas configurações para o usuário conceder ou revogar permissões de notificação a qualquer momento.

---

## Plano de Ação

### Fase 1: Segurança e Sessão
- [ ] Atualizar `UserDao` para permitir troca de senha e estatísticas.
- [ ] Criar fluxo de "Verificar Senha" e "Trocar Senha" no `SettingsViewModel`.
- [ ] Implementar função de Logout.

### Fase 2: Perfil e Mídia
- [ ] Adicionar suporte a Câmera no `SettingsScreen`.
- [ ] Melhorar o componente de foto de perfil com estados de carregamento.

### Fase 3: Notificações de Segundo Plano
- [ ] Criar `BootReceiver` no pacote `data.local`.
- [ ] Implementar lógica de reagendamento automático.
- [ ] Adicionar aviso visual se as notificações estiverem desativadas no sistema.

## Plano de Verificação

### Testes Manuais
- [ ] Realizar troca de senha e tentar logar com a nova.
- [ ] Verificar se a foto tirada pela câmera aparece corretamente no cabeçalho da Home.
- [ ] Simular reinício do dispositivo e verificar se os lembretes continuam agendados.
