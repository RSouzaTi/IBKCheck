package br.com.ibk.check.ui.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.ibk.check.data.local.AppDatabase
import br.com.ibk.check.data.local.LeituraEntity
import br.com.ibk.check.model.Estufa
import br.com.ibk.check.ui.components.*
import br.com.ibk.check.ui.viewModel.LeituraViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- 1. CONEXÃO COM O VIEWMODEL E DAO ---
    val db = AppDatabase.getDatabase(contexto)
    val dao = db.leituraDao()
    val viewModel: LeituraViewModel = viewModel(factory = LeituraViewModel.Factory(dao))

    var nomeCaldeirista by remember { mutableStateOf("") }
    val leiturasEstufas = remember { mutableStateMapOf<String, String>() }
    val dadosDoBanco by dao.buscarTodasLeituras().collectAsState(initial = emptyList())

    LaunchedEffect(dadosDoBanco) {
        leiturasEstufas.clear()
        dadosDoBanco.forEach { entity ->
            leiturasEstufas[entity.idChave] = entity.valor
        }
        val nomeSalvo = leiturasEstufas["config_nome_caldeirista"] ?: ""
        if (nomeSalvo.isNotEmpty() && nomeCaldeirista.isEmpty()) {
            nomeCaldeirista = nomeSalvo
        }
    }

    // --- 2. LÓGICA DE TURNOS E HORÁRIOS ---
    val horarios1 = listOf("06:30", "08:30", "10:30", "12:30", "14:30", "16:30")
    val horarios2 = listOf("18:30", "20:30", "22:30", "00:30", "02:30", "04:30")
    val turnoAuto = if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) in 5..17) 1 else 2
    var turnoSelecionado by remember { mutableStateOf(turnoAuto) }
    var horarioSelecionado by remember { mutableStateOf(if (turnoAuto == 1) "06:30" else "18:30") }
    val listaDeHorariosExibida = if (turnoSelecionado == 1) horarios1 else horarios2

    // --- 3. ESTADOS DAS VERIFICAÇÕES ---
    var pressaoCompressor by remember { mutableStateOf("") }
    var damperStatus by remember { mutableStateOf("") }
    var damperObs by remember { mutableStateOf("") }
    var vazamentoStatus by remember { mutableStateOf("") }
    var vazamentoObs by remember { mutableStateOf("") }
    var nivelCaixaStatus by remember { mutableStateOf("") }
    var bombaPocoStatus by remember { mutableStateOf("") }
    var tbuEs03Status by remember { mutableStateOf("") }
    var tbuEs03Obs by remember { mutableStateOf("") }
    var tbuEs04Status by remember { mutableStateOf("") }
    var tbuEs04Obs by remember { mutableStateOf("") }

    var mostrarDialogoRelatorio by remember { mutableStateOf(false) }
    var mostrarDialogoReset by remember { mutableStateOf(false) }

    val listaEstufas = remember {
        listOf(
            Estufa("1", "Estufa ES01"), Estufa("2", "Estufa ES02"),
            Estufa("3", "Estufa ES03"), Estufa("4", "Estufa ES04"),
            Estufa("5", "Estufa ES05"), Estufa("6", "Estufa ES06"),
            Estufa("7", "Estufa ES07"),
            Estufa("8", "Estufa ES08", unidadePressao = "MPa"),
            Estufa("9", "Estufa ES09"), Estufa("10", "Estufa ES10"),
            Estufa("11", "Estufa ES11"), Estufa("12", "Estufa ES12", unidadePressao = "MPa"),
            Estufa("13", "Estufa ES13"), Estufa("14", "Estufa ES14")
        )
    }

    Scaffold(
        topBar = {
            IBKTopAppBar(actions = {
                IconButton(onClick = { mostrarDialogoReset = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Limpar", tint = Color.White)
                }
            })
        },
        floatingActionButton = {
            val podeGerar = nomeCaldeirista.isNotBlank()
            ExtendedFloatingActionButton(
                onClick = { if (podeGerar) mostrarDialogoRelatorio = true else Toast.makeText(contexto, "Informe o Caldeirista", Toast.LENGTH_SHORT).show() },
                containerColor = if (podeGerar) Color(0xFF435D56) else Color.Gray,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                text = { Text("Gerar Relatório") }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = nomeCaldeirista,
                    onValueChange = {
                        nomeCaldeirista = it
                        coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("config_nome_caldeirista", it, System.currentTimeMillis())) }
                    },
                    label = { Text("Nome do Caldeirista") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Turno:", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { turnoSelecionado = 1; horarioSelecionado = "06:30" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (turnoSelecionado == 1) Color(0xFF435D56) else Color.Gray)) { Text("1º Turno") }
                    Button(onClick = { turnoSelecionado = 2; horarioSelecionado = "18:30" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (turnoSelecionado == 2) Color(0xFF435D56) else Color.Gray)) { Text("2º Turno") }
                }

                Text("Horário da Coleta:", fontWeight = FontWeight.Bold)
                LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaDeHorariosExibida) { hora ->
                        FilterChip(selected = horarioSelecionado == hora, onClick = { horarioSelecionado = hora }, label = { Text(hora) })
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text("Verificações de Início", style = MaterialTheme.typography.titleMedium, color = Color(0xFF435D56), fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = pressaoCompressor,
                    onValueChange = { pressaoCompressor = it },
                    label = { Text("1. Pressão do Compressor (Bar)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                ChecklistItem("2. Posição damper", damperStatus, damperObs, { damperStatus = it }, { damperObs = it })
                ChecklistItem("3. Vazamento rede", vazamentoStatus, vazamentoObs, { vazamentoStatus = it }, { vazamentoObs = it })

                Spacer(modifier = Modifier.height(16.dp))
                Text("Verificações Periódicas ($horarioSelecionado)", style = MaterialTheme.typography.titleMedium, color = Color(0xFF435D56), fontWeight = FontWeight.Bold)
                ChecklistItem("4. Nível caixa d'água", nivelCaixaStatus, "", { nivelCaixaStatus = it }, {})
                ChecklistItem("5. Bomba poço artesiano", bombaPocoStatus, "", { bombaPocoStatus = it }, {})
                ChecklistItem("6. Nível TBU ES03", tbuEs03Status, tbuEs03Obs, { tbuEs03Status = it }, { tbuEs03Obs = it })
                ChecklistItem("7. Nível TBU ES04", tbuEs04Status, tbuEs04Obs, { tbuEs04Status = it }, { tbuEs04Obs = it })

                Spacer(modifier = Modifier.height(24.dp))
                Text("Medições das Estufas", style = MaterialTheme.typography.titleMedium, color = Color(0xFF435D56), fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            items(listaEstufas) { estufa ->
                val p = "${estufa.id}_$horarioSelecionado"
                EstufaCard(
                    estufa = estufa,
                    valorTemp = leiturasEstufas["${p}_temp"] ?: "",
                    valorPress = leiturasEstufas["${p}_press"] ?: "",
                    valorUmidade = leiturasEstufas["${p}_umid"] ?: "",
                    valorTempo = leiturasEstufas["${p}_ciclo"] ?: "",
                    valorStatus = leiturasEstufas["${p}_status"] ?: "Operação",
                    labelTempo = "Horas acumulada : horas",
                    onTempChange = { n -> leiturasEstufas["${p}_temp"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_temp", n, System.currentTimeMillis())) } },
                    onPressChange = { n -> leiturasEstufas["${p}_press"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_press", n, System.currentTimeMillis())) } },
                    onUmidadeChange = { n -> leiturasEstufas["${p}_umid"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_umid", n, System.currentTimeMillis())) } },
                    onTempoChange = { n -> leiturasEstufas["${p}_ciclo"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_ciclo", n, System.currentTimeMillis())) } },
                    onStatusChange = { n -> leiturasEstufas["${p}_status"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_status", n, System.currentTimeMillis())) } }
                )
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    // --- DIÁLOGOS ---
    if (mostrarDialogoRelatorio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRelatorio = false },
            confirmButton = {
                Button(onClick = {
                    val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val relatorio = gerarTextoRelatorioFinal(
                        nome = nomeCaldeirista, data = dataAtual, pressaoComp = pressaoCompressor,
                        damper = "$damperStatus $damperObs", vazamento = "$vazamentoStatus $vazamentoObs",
                        caixa = nivelCaixaStatus, bomba = bombaPocoStatus,
                        tbu03 = "$tbuEs03Status $tbuEs03Obs", tbu04 = "$tbuEs04Status $tbuEs04Obs",
                        estufas = listaEstufas, horarioSelecionado = horarioSelecionado, leituras = leiturasEstufas
                    )
                    compartilharRelatorio(contexto, relatorio)
                    mostrarDialogoRelatorio = false
                }) { Text("Enviar via WhatsApp") }
            },
            title = { Text("Confirmar Relatório") },
            text = { Text("Deseja enviar as medições das $horarioSelecionado?") }
        )
    }

    if (mostrarDialogoReset) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoReset = false },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        dao.limparDadosDoTurno()
                        launch(Dispatchers.Main) {
                            leiturasEstufas.clear()
                            pressaoCompressor = ""; damperStatus = ""; vazamentoStatus = ""
                            nivelCaixaStatus = ""; bombaPocoStatus = ""
                            tbuEs03Status = ""; tbuEs04Status = ""
                            mostrarDialogoReset = false
                        }
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Limpar", color = Color.White) }
            },
            title = { Text("Resetar Tudo") },
            text = { Text("Isso apagará todas as medições do turno.") }
        )
    }
}

// --- FUNÇÕES DE SUPORTE (FORA DA MAINSCREEN) ---

fun gerarTextoRelatorioFinal(
    nome: String, data: String, pressaoComp: String, damper: String, vazamento: String,
    caixa: String, bomba: String, tbu03: String, tbu04: String,
    estufas: List<Estufa>, horarioSelecionado: String, leituras: Map<String, String>
): String {
    val sb = StringBuilder()
    sb.append("📋 *RELATÓRIO DE COLETA IBK*\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("🕒 *HORÁRIO:* $horarioSelecionado\n")
    sb.append("👤 *CALDEIRISTA:* ${nome.uppercase()}\n")
    sb.append("📅 *DATA:* $data\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n\n")

    sb.append("⚙️ *SISTEMA*\n")
    sb.append("┣ 🔘 Compressor: $pressaoComp Bar\n")
    sb.append("┣ 🆗 Damper: $damper\n")
    sb.append("┣ 🆗 Vazamento: $vazamento\n")
    sb.append("┣ 💧 Caixa d'água: $caixa\n")
    sb.append("┣ 🚀 Bomba Poço: $bomba\n")
    sb.append("┣ 🧪 TBU ES03: $tbu03\n")
    sb.append("┗ 🧪 TBU ES04: $tbu04\n\n")

    sb.append("🌡️ *MEDIÇÕES DAS ESTUFAS*\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")

    estufas.forEach { estufa ->
        val prefix = "${estufa.id}_$horarioSelecionado"
        val status = leituras["${prefix}_status"] ?: "Operação"
        val t = leituras["${prefix}_temp"] ?: ""
        val u = leituras["${prefix}_umid"] ?: ""
        val p = leituras["${prefix}_press"] ?: ""
        val c = leituras["${prefix}_ciclo"] ?: ""

        if (t.isNotEmpty() || u.isNotEmpty() || status != "Operação") {
            if (status == "Operação") {
                val tempoFormatado = if (c.isNotEmpty()) converterHorasParaDias(c) else ""
                sb.append("🏗️ *${estufa.nome}*\n")
                sb.append("┗ 💧 $u% | 🌡️ $t°C | 💨 $p ${estufa.unidadePressao} | ⏳ $tempoFormatado\n\n")
            } else {
                sb.append("🛠️ *${estufa.nome}*: ${status.uppercase()}\n\n")
            }
        }
    }
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("✅ *Fim do Relatório*")
    return sb.toString()
}

fun compartilharRelatorio(contexto: Context, texto: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    contexto.startActivity(Intent.createChooser(intent, "Compartilhar"))
}

fun converterHorasParaDias(input: String): String {
    return try {
        if (input.contains(":")) {
            val partes = input.split(":")
            val horasAcumuladas = partes[0].trim().toLongOrNull() ?: 0L
            val horasCorridas = partes[1].trim()
            val dias = horasAcumuladas / 24
            val horasRestantes = horasAcumuladas % 24
            if (dias > 0) "${dias}d ${horasRestantes}h e ${horasCorridas}min" else "${horasRestantes}h e ${horasCorridas}min"
        } else {
            val total = input.toLongOrNull() ?: 0L
            val dias = total / 24
            val resto = total % 24
            if (dias > 0) "${dias}d ${resto}h" else "${resto}h"
        }
    } catch (e: Exception) { input }
}