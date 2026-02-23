package br.com.ibk.check.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    // 1. Contexto e Escopo
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 2. Banco de Dados e Estados
    val db = AppDatabase.getDatabase(contexto)
    val dao = db.leituraDao()
    val leiturasEstufas = remember { mutableStateMapOf<String, String>() }

    val dadosDoBanco by dao.buscarTodasLeituras().collectAsState(initial = emptyList())

    LaunchedEffect(dadosDoBanco) {
        dadosDoBanco.forEach { entity ->
            leiturasEstufas[entity.idChave] = entity.valor
        }
    }

    // 3. Lógica de Tempo e Turno
    val horariosPrimeiro = listOf("06:30", "08:30", "10:30", "12:30", "14:30", "16:30")
    val horariosSegundo = listOf("18:30", "20:30", "22:30", "00:30", "02:30", "04:30")

    val agora = Calendar.getInstance()
    val horaRelogio = agora.get(Calendar.HOUR_OF_DAY)
    val turnoAuto = if (horaRelogio in 5..17) 1 else 2
    val horarioSugerido = if (turnoAuto == 1) {
        when {
            horaRelogio < 8 -> "06:30"
            horaRelogio < 10 -> "08:30"
            horaRelogio < 12 -> "10:30"
            horaRelogio < 14 -> "12:30"
            horaRelogio < 16 -> "14:30"
            else -> "16:30"
        }
    } else {
        when {
            horaRelogio in 18..19 -> "18:30"
            horaRelogio in 20..21 -> "20:30"
            horaRelogio in 22..23 -> "22:30"
            horaRelogio == 0 || horaRelogio == 1 -> "00:30"
            horaRelogio == 2 || horaRelogio == 3 -> "02:30"
            else -> "04:30"
        }
    }

    // 4. Estados de Interface
    var turnoSelecionado by remember { mutableStateOf(turnoAuto) }
    var horarioSelecionado by remember { mutableStateOf(horarioSugerido) }
    val listaDeHorariosExibida = if (turnoSelecionado == 1) horariosPrimeiro else horariosSegundo

    var nomeCaldeirista by remember { mutableStateOf("") }
    val dataAtual = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }

    // Estados dos Diálogos
    var mostrarDialogoRelatorio by remember { mutableStateOf(false) }
    var mostrarDialogoReset by remember { mutableStateOf(false) }

    // Estados do Checklist
    var drenarStatus by remember { mutableStateOf("") }
    var drenarObs by remember { mutableStateOf("") }
    var damperStatus by remember { mutableStateOf("") }
    var damperObs by remember { mutableStateOf("") }
    var vazamentoStatus by remember { mutableStateOf("") }
    var vazamentoObs by remember { mutableStateOf("") }

    val listaEstufas = remember {
        listOf(
            Estufa("1", "Estufa ES01"), Estufa("2", "Estufa ES02"),
            Estufa("3", "Estufa ES03"), Estufa("4", "Estufa ES04"),
            Estufa("5", "Estufa ES05"), Estufa("6", "Estufa ES06"),
            Estufa("7", "Estufa ES07", unidadePressao = "MPa"),
            Estufa("8", "Estufa ES08", unidadePressao = "MPa"),
            Estufa("9", "Estufa ES09"), Estufa("10", "Estufa ES10")
        )
    }

    Scaffold(
        topBar = {
            IBKTopAppBar(
                actions = {
                    IconButton(onClick = { mostrarDialogoReset = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Limpar Turno",
                            tint = Color.White
                        )
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

                OutlinedTextField(
                    value = nomeCaldeirista,
                    onValueChange = { nomeCaldeirista = it },
                    label = { Text("Nome do Caldeirista") },
                    modifier = Modifier.fillMaxWidth(),
                    // Remova qualquer 'color = Color.Black' daqui de dentro
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF435D56),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF435D56),
                        cursorColor = Color(0xFF435D56)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Turno de Trabalho:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { turnoSelecionado = 1; horarioSelecionado = "06:30" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if(turnoSelecionado == 1) Color(0xFF435D56) else Color.Gray)
                    ) { Text("1º Turno") }
                    Button(
                        onClick = { turnoSelecionado = 2; horarioSelecionado = "18:30" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if(turnoSelecionado == 2) Color(0xFF435D56) else Color.Gray)
                    ) { Text("2º Turno") }
                }

                Text("Horário da Coleta Atual:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaDeHorariosExibida) { hora ->
                        FilterChip(
                            selected = horarioSelecionado == hora,
                            onClick = { horarioSelecionado = hora },
                            label = { Text(hora) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF435D56), selectedLabelColor = Color.White)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text("Verificações de Início", style = MaterialTheme.typography.titleMedium, color = Color(0xFF435D56), fontWeight = FontWeight.Bold)
                ChecklistItem("1. Drenar compressor", drenarStatus, drenarObs, { drenarStatus = it }, { drenarObs = it })
                ChecklistItem("2. Posição damper", damperStatus, damperObs, { damperStatus = it }, { damperObs = it })
                ChecklistItem("3. Vazamento rede", vazamentoStatus, vazamentoObs, { vazamentoStatus = it }, { vazamentoObs = it })

                Spacer(modifier = Modifier.height(24.dp))
                Text("Medições ($horarioSelecionado)", style = MaterialTheme.typography.titleMedium, color = Color(0xFF435D56), fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            items(listaEstufas) { estufa ->
                val chaveTemp = "${estufa.id}_${horarioSelecionado}_temp"
                val chavePress = "${estufa.id}_${horarioSelecionado}_press"

                EstufaCard(
                    estufa = estufa,
                    valorTemp = leiturasEstufas[chaveTemp] ?: "",
                    valorPress = leiturasEstufas[chavePress] ?: "",
                    onTempChange = { novo ->
                        leiturasEstufas[chaveTemp] = novo
                        coroutineScope.launch(Dispatchers.IO) {
                            dao.salvarLeitura(LeituraEntity(chaveTemp, novo))
                        }
                    },
                    onPressChange = { novo ->
                        leiturasEstufas[chavePress] = novo
                        coroutineScope.launch(Dispatchers.IO) {
                            dao.salvarLeitura(LeituraEntity(chavePress, novo))
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    // --- DIÁLOGOS ---

    if (mostrarDialogoRelatorio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRelatorio = false },
            title = { Text("Confirmar Envio") },
            text = { Text("Deseja enviar o relatório acumulado via WhatsApp?") },
            confirmButton = {
                Button(
                    onClick = {
                        val relatorio = gerarTextoRelatorioAcumulado(
                            nomeCaldeirista, dataAtual,
                            "$drenarStatus $drenarObs", "$damperStatus $damperObs", "$vazamentoStatus $vazamentoObs",
                            listaEstufas, listaDeHorariosExibida, leiturasEstufas
                        )
                        mostrarDialogoRelatorio = false
                        compartilharRelatorio(contexto, relatorio)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF435D56))
                ) { Text("Enviar WhatsApp") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoRelatorio = false }) { Text("Cancelar") } }
        )
    }

    if (mostrarDialogoReset) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoReset = false },
            title = { Text("Limpar Dados") },
            text = { Text("Deseja apagar todas as medições salvas deste turno?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            dao.limparDadosDoTurno()
                            leiturasEstufas.clear()
                        }
                        mostrarDialogoReset = false
                        Toast.makeText(contexto, "Dados apagados!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Apagar Tudo", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoReset = false }) { Text("Cancelar") } }
        )
    }
}

// --- FUNÇÕES AUXILIARES ---

fun gerarTextoRelatorioAcumulado(
    nome: String, data: String, drenar: String, damper: String, vazamento: String,
    estufas: List<Estufa>, horarios: List<String>, leituras: Map<String, String>
): String {
    val sb = StringBuilder()
    sb.append("*RELATÓRIO ACUMULADO IBK*\n")
    sb.append("Caldeirista: $nome\nData: $data\n")
    sb.append("----------------------------\n")
    sb.append("*CHECKLIST INICIAL*\n")
    sb.append("Drenar: $drenar\nDamper: $damper\nVazamento: $vazamento\n\n")

    horarios.forEach { hora ->
        sb.append("*COLETA DAS $hora*\n")
        var temDados = false
        estufas.forEach { estufa ->
            val t = leituras["${estufa.id}_${hora}_temp"] ?: ""
            val p = leituras["${estufa.id}_${hora}_press"] ?: ""
            if (t.isNotEmpty() || p.isNotEmpty()) {
                sb.append("- ${estufa.nome}: $t°C | $p ${estufa.unidadePressao}\n")
                temDados = true
            }
        }
        if (!temDados) sb.append("(Sem registros)\n")
        sb.append("\n")
    }
    sb.append("_Gerado via IBK Check_")
    return sb.toString()
}

fun compartilharRelatorio(contexto: Context, texto: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, texto)
        type = "text/plain"
    }
    contexto.startActivity(Intent.createChooser(intent, "Compartilhar Relatório"))
}