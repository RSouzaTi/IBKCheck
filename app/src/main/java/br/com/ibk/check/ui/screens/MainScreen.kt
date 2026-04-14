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

    // Sincroniza o Banco com a Interface
    LaunchedEffect(dadosDoBanco) {
        leiturasEstufas.clear()
        dadosDoBanco.forEach { entity ->
            leiturasEstufas[entity.idChave] = entity.valor
        }
    }

    // --- 2. LÓGICA DE TURNO E HORÁRIOS ---
    val horariosPrimeiro = listOf("06:30", "08:30", "10:30", "12:30", "14:30", "16:30")
    val horariosSegundo = listOf("18:30", "20:30", "22:30", "00:30", "02:30", "04:30")

    val agora = Calendar.getInstance()
    val horaRelogio = agora.get(Calendar.HOUR_OF_DAY)
    val turnoAuto = if (horaRelogio in 5..17) 1 else 2

    var turnoSelecionado by remember { mutableStateOf(turnoAuto) }
    var horarioSelecionado by remember { mutableStateOf("06:30") }
    val listaDeHorariosExibida = if (turnoSelecionado == 1) horariosPrimeiro else horariosSegundo

    var nomeCaldeirista by remember { mutableStateOf("") }
    val dataAtual = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }

    // Estados dos Diálogos
    var mostrarDialogoRelatorio by remember { mutableStateOf(false) }
    var mostrarDialogoReset by remember { mutableStateOf(false) }

    // Estados do Checklist Inicial
    var pressaoCompressor by remember { mutableStateOf("") }
    var damperStatus by remember { mutableStateOf("") }
    var damperObs by remember { mutableStateOf("") }
    var vazamentoStatus by remember { mutableStateOf("") }
    var vazamentoObs by remember { mutableStateOf("") }

    // --- 3. LISTA DAS 14 ESTUFAS ---
    val listaEstufas = remember {
        listOf(
            Estufa("1", "Estufa ES01"), Estufa("2", "Estufa ES02"),
            Estufa("3", "Estufa ES03"), Estufa("4", "Estufa ES04"),
            Estufa("5", "Estufa ES05"), Estufa("6", "Estufa ES06"),
            Estufa("7", "Estufa ES07", unidadePressao = "MPa"),
            Estufa("8", "Estufa ES08", unidadePressao = "MPa"),
            Estufa("9", "Estufa ES09"), Estufa("10", "Estufa ES10"),
            Estufa("11", "Estufa ES11"), Estufa("12", "Estufa ES12"),
            Estufa("13", "Estufa ES13"), Estufa("14", "Estufa ES14")
        )
    }

    // --- 4. INTERFACE PRINCIPAL (SCAFFOLD) ---
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Nome do Caldeirista
                OutlinedTextField(
                    value = nomeCaldeirista,
                    onValueChange = { nomeCaldeirista = it },
                    label = { Text("Nome do Caldeirista") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF435D56),
                        focusedLabelColor = Color(0xFF435D56)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Seleção de Turno
                Text("Turno de Trabalho:", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { turnoSelecionado = 1 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if(turnoSelecionado == 1) Color(0xFF435D56) else Color.Gray)
                    ) { Text("1º Turno") }
                    Button(
                        onClick = { turnoSelecionado = 2 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if(turnoSelecionado == 2) Color(0xFF435D56) else Color.Gray)
                    ) { Text("2º Turno") }
                }

                // Seleção de Horário
                Text("Horário da Coleta Atual:", fontWeight = FontWeight.Bold)
                LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaDeHorariosExibida) { hora ->
                        FilterChip(
                            selected = horarioSelecionado == hora,
                            onClick = { horarioSelecionado = hora },
                            label = { Text(hora) }
                        )
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

                Spacer(modifier = Modifier.height(24.dp))
                Text("Medições ($horarioSelecionado)", style = MaterialTheme.typography.titleMedium, color = Color(0xFF435D56), fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // --- 5. CARDS DAS ESTUFAS ---
            items(listaEstufas) { estufa ->
                val chaveTemp = "${estufa.id}_${horarioSelecionado}_temp"
                val chavePress = "${estufa.id}_${horarioSelecionado}_press"
                val chaveUmid = "${estufa.id}_${horarioSelecionado}_umid"
                val chaveCiclo = "${estufa.id}_${horarioSelecionado}_ciclo"

                EstufaCard(
                    estufa = estufa,
                    valorTemp = leiturasEstufas[chaveTemp] ?: "",
                    valorPress = leiturasEstufas[chavePress] ?: "",
                    valorUmidade = leiturasEstufas[chaveUmid] ?: "",
                    valorTempo = leiturasEstufas[chaveCiclo] ?: "",
                    onTempChange = { novo ->
                        leiturasEstufas[chaveTemp] = novo
                        coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(chaveTemp, novo)) }
                    },
                    onPressChange = { novo ->
                        leiturasEstufas[chavePress] = novo
                        coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(chavePress, novo)) }
                    },
                    onUmidadeChange = { novo ->
                        leiturasEstufas[chaveUmid] = novo
                        coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(chaveUmid, novo)) }
                    },
                    onTempoChange = { novo ->
                        leiturasEstufas[chaveCiclo] = novo
                        coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity(chaveCiclo, novo)) }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    // --- 6. DIÁLOGOS (RELATÓRIO E RESET) ---

    if (mostrarDialogoRelatorio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRelatorio = false },
            title = { Text("Confirmar Envio") },
            text = { Text("Deseja enviar o relatório via WhatsApp?") },
            confirmButton = {
                Button(onClick = {
                    val relatorio = gerarTextoRelatorioAcumulado(
                        nomeCaldeirista, dataAtual, pressaoCompressor,
                        "$damperStatus $damperObs", "$vazamentoStatus $vazamentoObs",
                        listaEstufas, listaDeHorariosExibida, leiturasEstufas
                    )
                    mostrarDialogoRelatorio = false
                    compartilharRelatorio(contexto, relatorio)
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF435D56))) {
                    Text("Enviar")
                }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoRelatorio = false }) { Text("Cancelar") } }
        )
    }

    if (mostrarDialogoReset) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoReset = false },
            title = { Text("Limpar Tudo") },
            text = { Text("Isso apagará todas as medições salvas. Confirmar?") },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        dao.limparDadosDoTurno() // Certifique-se que o DAO tem essa função
                        launch(Dispatchers.Main) {
                            leiturasEstufas.clear()
                            pressaoCompressor = ""
                            damperStatus = ""
                            damperObs = ""
                            vazamentoStatus = ""
                            vazamentoObs = ""
                            mostrarDialogoReset = false
                            Toast.makeText(contexto, "Dados resetados!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Apagar", color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoReset = false }) { Text("Cancelar") } }
        )
    }
}

// --- 7. FUNÇÕES AUXILIARES ---

fun gerarTextoRelatorioAcumulado(
    nome: String, data: String, pressaoComp: String, damper: String, vazamento: String,
    estufas: List<Estufa>, horarios: List<String>, leituras: Map<String, String>
): String {
    val sb = StringBuilder()
    sb.append("*RELATÓRIO IBK CHECK*\n")
    sb.append("Caldeirista: $nome\nData: $data\n")
    sb.append("----------------------------\n")
    sb.append("*CHECKLIST INICIAL*\n")
    sb.append("Pressão Comp: $pressaoComp Bar\nDamper: $damper\nVazamento: $vazamento\n\n")

    horarios.forEach { hora ->
        val sbHora = StringBuilder()
        var temDadosNaHora = false
        sbHora.append("*COLETA $hora*\n")

        estufas.forEach { estufa ->
            val t = leituras["${estufa.id}_${hora}_temp"] ?: ""
            val p = leituras["${estufa.id}_${hora}_press"] ?: ""
            val u = leituras["${estufa.id}_${hora}_umid"] ?: ""
            val c = leituras["${estufa.id}_${hora}_ciclo"] ?: ""

            if (t.isNotEmpty() || p.isNotEmpty()) {
                sbHora.append("- ${estufa.nome}: $t°C | $p ${estufa.unidadePressao} | $u% Umid | Ciclo: $c\n")
                temDadosNaHora = true
            }
        }

        if (temDadosNaHora) {
            sb.append(sbHora.toString()).append("\n")
        }
    }

    sb.append("_Enviado via App IBK Check_")
    return sb.toString()
}

fun compartilharRelatorio(contexto: Context, texto: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    contexto.startActivity(Intent.createChooser(intent, "Compartilhar Relatório"))
}