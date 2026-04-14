
🚀 Melhorias e Implementações Recentes
Recentemente, o projeto passou por uma grande atualização técnica e visual:

🛠 Funcionalidades Técnicas
Persistência de Dados (Room Database): Implementação de banco de dados local para que os dados não sejam perdidos ao fechar o aplicativo.

Salvamento em Tempo Real: Cada medição digitada é salva automaticamente em segundo plano (Coroutines + DAO).

Lógica de Turnos Automática: O app detecta o horário atual do sistema e sugere automaticamente o turno (1º ou 2º) e o próximo horário de coleta.

Gerador de Relatórios: Função inteligente que compila todas as leituras do turno em um texto formatado para envio profissional via WhatsApp.

Limpeza de Turno: Opção de resetar todos os dados salvos para iniciar um novo período de trabalho.

🎨 Identidade Visual e UX (Experiência do Usuário)
Nova TopAppBar: Interface limpa com a logo da IBK centralizada e identificação do documento técnico (RQ-019).

Splash Screen Personalizada: Tela de abertura com o verde oficial da marca e ícone adaptativo.

Ícone Adaptativo: Criação de um ícone exclusivo que respeita os padrões de design do Android (Foreground e Background camadas).

Validação de Dados: O botão de envio só é habilitado após o preenchimento do nome do caldeirista, evitando relatórios anônimos.

🛠 Tecnologias Utilizadas
Linguagem: Kotlin
