package br.com.ibk.check.ui.screens

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
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- 1. BANCO DE DADOS E ESTADOS ---
    val db = AppDatabase.getDatabase(contexto)
    val dao = db.leituraDao()
    val leiturasEstufas = remember { mutableStateMapOf<String, String>() }
    val dadosDoBanco by dao.buscarTodasLeituras().collectAsState(initial = emptyList())

    LaunchedEffect(dadosDoBanco) {
        leiturasEstufas.clear()
        dadosDoBanco.forEach { entity ->
            leiturasEstufas[entity.idChave] = entity.valor
        }
    }

    // --- 2. LOGICA DE TURNOS E HORÁRIOS ---
    val horarios1 = listOf("06:30", "08:30", "10:30", "12:30", "14:30", "16:30")
    val horarios2 = listOf("18:30", "20:30", "22:30", "00:30", "02:30", "04:30")

    val agora = Calendar.getInstance()
    val horaRelogio = agora.get(Calendar.HOUR_OF_DAY)
    val turnoAuto = if (horaRelogio in 5..17) 1 else 2

    var turnoSelecionado by remember { mutableStateOf(turnoAuto) }
    var horarioSelecionado by remember { mutableStateOf(if (turnoAuto == 1) "06:30" else "18:30") }
    val listaDeHorariosExibida = if (turnoSelecionado == 1) horarios1 else horarios2

    var nomeCaldeirista by remember { mutableStateOf("") }
    val dataAtual = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }

    var mostrarDialogoRelatorio by remember { mutableStateOf(false) }
    var mostrarDialogoReset by remember { mutableStateOf(false) }

    // Estados de Verificação
    var pressaoCompressor by remember { mutableStateOf("") }
    var damperStatus by remember { mutableStateOf("") }
    var damperObs by remember { mutableStateOf("") }
    var vazamentoStatus by remember { mutableStateOf("") }
    var vazamentoObs by remember { mutableStateOf("") }
    var nivelCaixaStatus by remember { mutableStateOf("") }
    var bombaPocoStatus by remember { mutableStateOf("") }

    // --- 3. LISTA DAS 14 ESTUFAS ---
    val listaEstufas = remember {
        listOf(
            Estufa("1", "Estufa ES01"), Estufa("2", "Estufa ES02"),
            Estufa("3", "Estufa ES03"), Estufa("4", "Estufa ES04"),
            Estufa("5", "Estufa ES05"), Estufa("6", "Estufa ES06"),
            Estufa("7", "Estufa ES07"),
            Estufa("8", "Estufa ES08", unidadePressao = "MPa"),
            Estufa("9", "Estufa ES09"), Estufa("10", "Estufa ES10"),
            Estufa("11", "Estufa ES11"), Estufa("12", "Estufa ES12"),
            Estufa("13", "Estufa ES13"), Estufa("14", "Estufa ES14")
        )
    }

    Scaffold(
        topBar = {
            IBKTopAppBar(
                actions = {
                    IconButton(onClick = { mostrarDialogoReset = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Limpar", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            val nomeValido = nomeCaldeirista.trim().isNotEmpty()
            ExtendedFloatingActionButton(
                onClick = {
                    if (nomeValido) mostrarDialogoRelatorio = true
                    else Toast.makeText(contexto, "Digite o nome do caldeirista", Toast.LENGTH_SHORT).show()
                },
                containerColor = if (nomeValido) Color(0xFF435D56) else Color.Gray,
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
                    onValueChange = { nomeCaldeirista = it },
                    label = { Text("Nome do Caldeirista") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Turno:", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { turnoSelecionado = 1; horarioSelecionado = "06:30" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if(turnoSelecionado == 1) Color(0xFF435D56) else Color.Gray)) { Text("1º Turno") }
                    Button(onClick = { turnoSelecionado = 2; horarioSelecionado = "18:30" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if(turnoSelecionado == 2) Color(0xFF435D56) else Color.Gray)) { Text("2º Turno") }
                }

                Text("Horário da Coleta:", fontWeight = FontWeight.Bold)
                LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaDeHorariosExibida) { hora ->
                        FilterChip(selected = horarioSelecionado == hora, onClick = { horarioSelecionado = hora }, label = { Text(hora) })
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Checklist Inicial
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

                Spacer(modifier = Modifier.height(24.dp))
                Text("Medições das Estufas", style = MaterialTheme.typography.titleMedium, color = Color(0xFF435D56), fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            items(listaEstufas) { estufa ->
                val cT = "${estufa.id}_${horarioSelecionado}_temp"
                val cP = "${estufa.id}_${horarioSelecionado}_press"
                val cU = "${estufa.id}_${horarioSelecionado}_umid"
                val cC = "${estufa.id}_${horarioSelecionado}_ciclo"
                val cS = "${estufa.id}_${horarioSelecionado}_status" // Chave do status

                EstufaCard(
                    estufa = estufa,
                    valorTemp = leiturasEstufas[cT] ?: "",
                    valorPress = leiturasEstufas[cP] ?: "",
                    valorUmidade = leiturasEstufas[cU] ?: "",
                    valorTempo = leiturasEstufas[cC] ?: "",
                    valorStatus = leiturasEstufas[cS] ?: "Operação", // Passa o status salvo
                    labelTempo = "Horas acumulada : horas",
                    onTempChange = { n -> leiturasEstufas[cT] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(cT, n)) } },
                    onPressChange = { n -> leiturasEstufas[cP] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(cP, n)) } },
                    onUmidadeChange = { n -> leiturasEstufas[cU] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(cU, n)) } },
                    onTempoChange = { n -> leiturasEstufas[cC] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(cC, n)) } },
                    onStatusChange = { n ->
                        leiturasEstufas[cS] = n
                        coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(cS, n)) }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    // --- DIÁLOGOS E RESET ---
    // (Mantenha os diálogos de Relatório e Reset como estão no seu código original)
}

// --- FUNÇÕES AUXILIARES ---

fun converterHorasParaDias(textoTempo: String): String {
    return try {
        val partes = textoTempo.split(":")
        val horasTotais = partes[0].toLong()
        val minutos = if (partes.size > 1) partes[1] else "00"
        val dias = horasTotais / 24
        val horasRestantes = horasTotais % 24
        if (dias > 0) "${dias}d ${horasRestantes}h e ${minutos}min"
        else "${horasRestantes}h e ${minutos}min"
    } catch (e: Exception) { textoTempo }
}

fun gerarTextoRelatorioAcumulado(
    nome: String, data: String, pressaoComp: String, damper: String, vazamento: String,
    caixa: String, bomba: String,
    estufas: List<Estufa>, horarios: List<String>, leituras: Map<String, String>
): String {
    val sb = StringBuilder()
    sb.append("*RELATÓRIO IBK*\nCaldeirista: $nome\nData: $data\n")
    sb.append("----------------------------\n")
    sb.append("*CHECKLIST INICIAL*\n")
    sb.append("Compressor: $pressaoComp Bar\nDamper: $damper\nVazamento: $vazamento\n\n")

    horarios.forEach { hora ->
        val sbHora = StringBuilder()
        var temDados = false
        sbHora.append("*COLETA $hora*\n")

        if(caixa.isNotEmpty() || bomba.isNotEmpty()){
            sbHora.append("Caixa d'água: $caixa | Bomba Poço: $bomba\n")
        }

        estufas.forEach { estufa ->
            val status = leituras["${estufa.id}_${hora}_status"] ?: "Operação"
            val t = leituras["${estufa.id}_${hora}_temp"] ?: ""
            val p = leituras["${estufa.id}_${hora}_press"] ?: ""
            val u = leituras["${estufa.id}_${hora}_umid"] ?: ""
            val c = leituras["${estufa.id}_${hora}_ciclo"] ?: ""

            // Se o status for diferente de operação, escreve o aviso
            if (status != "Operação") {
                sbHora.append("- ${estufa.nome}: *STATUS: ${status.uppercase()}*\n")
                temDados = true
            }
            // Se estiver em operação, verifica se há dados preenchidos
            else if (t.isNotEmpty() || p.isNotEmpty() || u.isNotEmpty() || c.isNotEmpty()) {
                val tempoFormatado = if (c.isNotEmpty()) converterHorasParaDias(c) else ""
                sbHora.append("- ${estufa.nome}: $u% Umid | $t°C | $p ${estufa.unidadePressao} | Ciclo: $tempoFormatado\n")
                temDados = true
            }
        }
        if (temDados) sb.append(sbHora.toString()).append("\n")
    }
    return sb.toString()
}

// (Mantenha a função compartilharRelatorio)
fun compartilharRelatorio(contexto: Context, texto: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    contexto.startActivity(Intent.createChooser(intent, "Compartilhar Relatório"))
}