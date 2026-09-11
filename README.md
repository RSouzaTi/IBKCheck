# 📱 IBKCheck

Aplicativo Android desenvolvido para **digitalização e padronização de checklists operacionais**, utilizado diariamente em ambiente real.

O IBKCheck foi desenvolvido como um **projeto comercial**, entregue e vendido para utilização na operação do cliente. A aplicação substitui registros manuais por um fluxo digital de inspeção, permitindo registrar responsáveis, turnos, horários, medições, verificações e não conformidades.

> 🚀 **Projeto em produção:** aplicação desenvolvida para um cliente e utilizada diariamente na operação.

---

## 🎯 Objetivo

O aplicativo foi criado para apoiar o acompanhamento de rotinas operacionais relacionadas a caldeiras e estufas, tornando o processo de inspeção mais organizado, rastreável e padronizado.

Entre os principais objetivos estão:

- Digitalizar checklists operacionais;
- Reduzir registros manuais;
- Padronizar as inspeções por turno;
- Registrar medições e verificações realizadas durante a operação;
- Identificar condições **OK / NC (Não Conforme)**;
- Manter os dados armazenados localmente;
- Facilitar a geração e o compartilhamento dos relatórios.

---

## ⚙️ Principais funcionalidades

### 👷 Controle operacional

- Identificação do caldeirista responsável;
- Seleção do turno de trabalho;
- Seleção dos horários de coleta;
- Sugestão automática de turno conforme o horário do dispositivo;
- Sugestão do próximo horário de coleta.

### ✅ Checklist de inspeção

- Registro da pressão do compressor;
- Verificação da posição do damper;
- Registro de vazamentos;
- Verificação do nível da caixa;
- Verificação da bomba do poço;
- Verificações relacionadas às estufas;
- Registro de observações e situações de não conformidade.

### 💾 Persistência de dados

Os registros são armazenados localmente utilizando **Room Database**. As alterações podem ser persistidas durante o preenchimento do checklist, permitindo recuperar os dados mesmo após o fechamento do aplicativo.

A camada de acesso aos dados utiliza **DAO + Kotlin Coroutines/Flow**, mantendo a interface sincronizada com os registros armazenados.

### 📊 Monitoramento das estufas

O aplicativo possui acompanhamento das leituras das estufas e geração de visualizações gráficas para análise do histórico de umidade.

### 📄 Geração de relatórios

O sistema gera relatórios em **PDF**, contendo informações do turno e dados das inspeções realizadas.

O relatório possui formato próprio para utilização operacional e pode ser compartilhado diretamente pelo Android.

### 🔄 Gerenciamento do turno

- Salvamento das informações durante o preenchimento;
- Consulta do histórico armazenado;
- Limpeza/reset dos dados para iniciar um novo período de trabalho;
- Validação do preenchimento do responsável antes da geração do relatório.

---

## 🏗️ Arquitetura

O projeto foi organizado separando responsabilidades entre interface, estado da aplicação, modelos e persistência local:

```text
┌─────────────────────────────────────┐
│            Jetpack Compose          │
│       Interface / componentes       │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│             ViewModel               │
│       Estado e lógica da tela       │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│              DAO / Flow             │
│       Acesso aos dados locais       │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│            Room Database            │
│          Persistência local         │
└─────────────────────────────────────┘
```

A estrutura do código está organizada principalmente em:

```text
br.com.ibk.check
├── data
│   └── local
│       ├── AppDatabase
│       ├── LeituraDao
│       └── LeituraEntity
├── model
├── ui
│   ├── components
│   ├── screens
│   ├── theme
│   └── viewModel
└── MainActivity
```

---

## 🛠️ Tecnologias utilizadas

| Tecnologia | Utilização |
|---|---|
| **Kotlin** | Linguagem principal |
| **Android** | Plataforma da aplicação |
| **Jetpack Compose** | Construção da interface |
| **Material 3** | Componentes e identidade visual da interface |
| **Room** | Banco de dados local |
| **Kotlin Coroutines** | Operações assíncronas |
| **Kotlin Flow** | Observação e atualização dos dados |
| **ViewModel** | Gerenciamento de estado e lógica da UI |
| **KSP** | Processamento de código do Room |
| **Vico** | Gráficos e visualização de dados |
| **Android PDF API** | Geração dos relatórios em PDF |
| **FileProvider** | Compartilhamento seguro dos arquivos |

### Ambiente de desenvolvimento

- Java 17
- Android SDK 36
- Kotlin 2.3.x
- Gradle
- Android Studio

---

## 📱 Fluxo de utilização

```text
Identificação do responsável
          ↓
Seleção do turno
          ↓
Seleção do horário de coleta
          ↓
Preenchimento das verificações
          ↓
Registro das medições
          ↓
Persistência local dos dados
          ↓
Geração do relatório
          ↓
Compartilhamento do PDF
```

---

## 💼 Experiência profissional

O IBKCheck representa um projeto de desenvolvimento de software aplicado a uma necessidade real de negócio.

Além da implementação técnica, o projeto envolve a transformação de um processo operacional em uma solução digital utilizada diariamente, considerando aspectos como usabilidade, persistência de dados, validação das informações, geração de documentos e facilidade de utilização no ambiente de trabalho.

> **Projeto comercial / em produção** — desenvolvido, entregue e utilizado diariamente por um cliente.

---

## 🔒 Sobre os dados do cliente

Por se tratar de uma aplicação comercial utilizada em ambiente real, informações identificáveis do cliente, dados operacionais sensíveis e configurações específicas não são documentados neste README.

As funcionalidades e tecnologias apresentadas aqui têm como objetivo demonstrar a arquitetura e as capacidades técnicas do projeto.

---

## 👨‍💻 Autor

**Ricardo Souza**  
Desenvolvedor de Software | Backend Java & Node.js | Mobile

- GitHub: [RSouzaTi](https://github.com/RSouzaTi)
- Portfolio: [rsouzati.github.io/site](https://rsouzati.github.io/site/)

---

⭐ Projeto desenvolvido com foco em resolver uma necessidade real de operação por meio de uma solução mobile.
