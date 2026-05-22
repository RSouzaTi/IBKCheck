package br.com.ibk.check.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ibk.check.data.local.AppDatabase
import br.com.ibk.check.data.local.LeituraEntity
import br.com.ibk.check.model.Estufa
import br.com.ibk.check.ui.components.ChecklistItem
import br.com.ibk.check.ui.components.EstufaCard
import br.com.ibk.check.ui.components.GraficoBarrasEstufa
import br.com.ibk.check.ui.components.IBKTopAppBar
import br.com.ibk.check.ui.viewModel.LeituraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- 1. CONEXÃO COM O VIEWMODEL E DAO ---
    val db = remember { AppDatabase.getDatabase(contexto) }
    val dao = remember { db.leituraDao() }
    viewModel(factory = LeituraViewModel.Factory(dao))
    var nomeCaldeirista by remember { mutableStateOf("") }
    val leiturasEstufas = remember { mutableStateMapOf<String, String>() }
    val dadosDoBanco by dao.buscarTodasLeituras().collectAsState(initial = emptyList())

    LaunchedEffect(dadosDoBanco) {
        val novoMapa = dadosDoBanco.associate { it.idChave to it.valor }

        // Remove chaves que não existem mais no banco
        val chavesParaRemover = leiturasEstufas.keys.filter { !novoMapa.containsKey(it) }
        chavesParaRemover.forEach { leiturasEstufas.remove(it) }

        // Atualiza apenas o que mudou
        novoMapa.forEach { (chave, valor) ->
            if (leiturasEstufas[chave] != valor) {
                leiturasEstufas[chave] = valor
            }
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
    var turnoSelecionado by remember { mutableIntStateOf(turnoAuto) }
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

    // Controle do Pop-up do gráfico da estufa selecionada
    var estufaSelecionadaParaGrafico by remember { mutableStateOf<Estufa?>(null) }

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

    fun criarRelatorioPdfIBK(
        contexto: Context,
        nome: String,
        data: String,
        listaEstufas: List<Estufa>,
        leiturasEstufas: Map<String, String>
    ): File? {
        val pdfDocument = PdfDocument()
        // Folha A4 em modo Paisagem (Deitada): Largura 842 x Altura 595
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // 1. Cabeçalho Corporativo
        paint.color = "#435D56".toColorInt()
        canvas.drawRect(0f, 0f, 842f, 90f, paint)

        paint.color = android.graphics.Color.WHITE
        paint.textSize = 22f
        paint.isFakeBoldText = true
        canvas.drawText("IBK CHECK - HISTÓRICO GERAL DE UMIDADE", 40f, 40f, paint)

        paint.textSize = 11f
        paint.isFakeBoldText = false
        canvas.drawText("Painel Monitorado com Alertas Dinâmicos de Processo", 40f, 65f, paint)

        // 2. Informações Gerais do Turno
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 11f
        canvas.drawText("Caldeirista: ${nome.uppercase()}", 40f, 120f, paint)
        canvas.drawText("Data: $data", 300f, 120f, paint)

        paint.color = android.graphics.Color.LTGRAY
        canvas.drawLine(40f, 135f, 802f, 135f, paint)

        // 💡 3. RECALIBRAÇÃO DE COORDENADAS PARA CABER AS 14 ESTUFAS COMPLETA
        val xInicio = 50f                // Recuado ligeiramente para a esquerda para abrir espaço
        val yBaseGrafico = 480f
        val alturaMaximaGrafico = 240f
        val listaHorarios = listOf("06:30", "08:30", "10:30", "12:30", "14:30", "16:30")

        val larguraBarraIndividual = 4.5f // Ajuste fino milimétrico na espessura
        val espacoEntreHorarios = 0.8f    // Compactado sutilmente para não estourar
        val espacoEntreEstufas = 11f      // Espaço entre blocos reduzido para caber de 1 a 14

        // 4. IMPLEMENTAÇÃO DA GRADE DE FUNDO (LINHAS GUIA HORIZONTAIS)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.5f
        paint.isFakeBoldText = false
        paint.textSize = 8f

        val porcentagensGrade = listOf(20, 40, 60, 80, 100)
        porcentagensGrade.forEach { pct ->
            val yGrade = yBaseGrafico - ((pct / 100f) * alturaMaximaGrafico)
            paint.color = "#E0E0E0".toColorInt()
            canvas.drawLine(40f, yGrade, 802f, yGrade, paint)

            paint.color = android.graphics.Color.GRAY
            paint.style = Paint.Style.FILL
            canvas.drawText("$pct%", 18f, yGrade + 3f, paint)
        }

        // Restaura o estilo de preenchimento para as barras
        paint.style = Paint.Style.FILL

        // 5. DESENHO DAS BARRAS
        listaEstufas.forEachIndexed { indiceEstufa, estufa ->
            val larguraBlocoEstufa = (larguraBarraIndividual + espacoEntreHorarios) * listaHorarios.size
            val xBlocoInicio = xInicio + indiceEstufa * (larguraBlocoEstufa + espacoEntreEstufas)

            listaHorarios.forEachIndexed { indiceHorario, horario ->
                val prefix = "${estufa.id}_$horario"
                val status = leiturasEstufas["${prefix}_status"] ?: "Operação"
                val xBarra = xBlocoInicio + indiceHorario * (larguraBarraIndividual + espacoEntreHorarios)

                if (status == "Operação") {
                    val umidadeTexto = leiturasEstufas["${prefix}_umid"] ?: ""
                    val apenasNumeros = umidadeTexto.replace(",", ".")
                    val umidade = apenasNumeros.toFloatOrNull() ?: 0f

                    if (umidade > 0f) {
                        val alturaBarra = (umidade / 100f) * alturaMaximaGrafico
                        val yBarraTop = yBaseGrafico - alturaBarra

                        val corBarra = when {
                            umidade <= 15f -> "#435D56" // Verde IBK
                            umidade <= 25f -> "#FBC02D" // Amarelo
                            else -> "#D32F2F"           // Vermelho
                        }

                        paint.color = corBarra.toColorInt()
                        canvas.drawRect(xBarra, yBarraTop, xBarra + larguraBarraIndividual, yBaseGrafico, paint)

                        // Mostra o valor numérico acima da barra no fechamento ou em picos críticos
                        if (horario == "16:30" || umidade > 25f) {
                            paint.color = android.graphics.Color.DKGRAY
                            paint.textSize = 7f
                            paint.isFakeBoldText = true
                            canvas.drawText("${umidade.toInt()}%", xBarra - 2f, yBarraTop - 5f, paint)
                        }
                    }
                }
            }

            // LINHA DIVISÓRIA VERTICAL SUAVE
            paint.color = "#F0F0F0".toColorInt()
            paint.strokeWidth = 1f
            val xDivisoria = xBlocoInicio + larguraBlocoEstufa + (espacoEntreEstufas / 2)
            if (indiceEstufa < listaEstufas.size - 1) {
                canvas.drawLine(xDivisoria, yBaseGrafico - alturaMaximaGrafico, xDivisoria, yBaseGrafico + 25f, paint)
            }

            // Nome da Estufa Corrigido (Se o nome for longo como "Estufa 01", reduz para "ES01")
            paint.color = android.graphics.Color.BLACK
            paint.textSize = 8.5f
            paint.isFakeBoldText = true
            val nomeCurto = estufa.nome.replace("Estufa ", "ES")
            canvas.drawText(nomeCurto, xBlocoInicio + 2f, yBaseGrafico + 18f, paint)
        }

        // Desenha a linha firme do chão do gráfico (Eixo X)
        paint.color = android.graphics.Color.GRAY
        paint.strokeWidth = 2f
        canvas.drawLine(40f, yBaseGrafico, 802f, yBaseGrafico, paint)

        // 6. LEGENDA DE STATUS DE COR E HORÁRIOS
        var xLegenda = 40f
        paint.textSize = 9f
        paint.isFakeBoldText = true
        canvas.drawText("FAIXAS DE PROCESSO:", xLegenda, 525f, paint)

        xLegenda += 120f
        paint.color = "#435D56".toColorInt()
        canvas.drawRect(xLegenda, 516f, xLegenda + 12f, 526f, paint)
        paint.color = android.graphics.Color.BLACK
        paint.isFakeBoldText = false
        canvas.drawText("Até 15% (Ideal)", xLegenda + 16f, 525f, paint)

        xLegenda += 110f
        paint.color = "#FBC02D".toColorInt()
        canvas.drawRect(xLegenda, 516f, xLegenda + 12f, 526f, paint)
        paint.color = android.graphics.Color.BLACK
        canvas.drawText("16% a 25% (Atenção)", xLegenda + 16f, 525f, paint)

        xLegenda += 130f
        paint.color = "#D32F2F".toColorInt()
        canvas.drawRect(xLegenda, 516f, xLegenda + 12f, 526f, paint)
        paint.color = android.graphics.Color.BLACK
        canvas.drawText("> 25% (Crítico)", xLegenda + 16f, 525f, paint)

        paint.color = android.graphics.Color.DKGRAY
        paint.textSize = 8.5f
        paint.isFakeBoldText = true
        canvas.drawText("Ordem Cronológica das Barras (Esquerda para Direita):  06:30  ➔  08:30  ➔  10:30  ➔  12:30  ➔  14:30  ➔  16:30", 40f, 550f, paint)

        // Rodapé padrão
        paint.color = android.graphics.Color.GRAY
        paint.textSize = 8f
        paint.isFakeBoldText = false
        canvas.drawText("Relatório gerado via IBK Check App em ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}", 40f, 575f, paint)

        pdfDocument.finishPage(page)

        val file = File(contexto.cacheDir, "Grafico_Historico_Turno.pdf")
        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
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
                        coroutineScope.launch(Dispatchers.IO) {
                            dao.salvarLeitura(LeituraEntity("config_nome_caldeirista", it, System.currentTimeMillis()))
                        }
                    },
                    label = { Text("Nome do Caldeirista") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Unspecified,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Unspecified,
                        imeAction = ImeAction.Unspecified
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Turno:", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { turnoSelecionado = 1; horarioSelecionado = "06:30" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (turnoSelecionado == 1) Color(0xFF435D56) else Color.Gray
                        )
                    ) { Text("1º Turno") }
                    Button(
                        onClick = { turnoSelecionado = 2; horarioSelecionado = "18:30" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (turnoSelecionado == 2) Color(0xFF435D56) else Color.Gray
                        )
                    ) { Text("2º Turno") }
                }

                Text("Horário da Coleta:", fontWeight = FontWeight.Bold)
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaDeHorariosExibida) { hora ->
                        FilterChip(
                            selected = horarioSelecionado == hora,
                            onClick = { horarioSelecionado = hora },
                            label = { Text(hora) })
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    "Verificações de Início",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF435D56),
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = pressaoCompressor,
                    onValueChange = { pressaoCompressor = it },
                    label = { Text("1. Pressão do Compressor (Bar)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Unspecified,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Unspecified
                    )
                )
                ChecklistItem(
                    "2. Posição damper",
                    damperStatus,
                    damperObs,
                    { damperStatus = it },
                    { damperObs = it })
                ChecklistItem(
                    "3. Vazamento rede",
                    vazamentoStatus,
                    vazamentoObs,
                    { vazamentoStatus = it },
                    { vazamentoObs = it })

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Verificações Periódicas ($horarioSelecionado)",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF435D56),
                    fontWeight = FontWeight.Bold
                )
                ChecklistItem(
                    "4. Nível caixa d'água",
                    nivelCaixaStatus,
                    "",
                    { nivelCaixaStatus = it },
                    {})
                ChecklistItem(
                    "5. Bomba poço artesiano",
                    bombaPocoStatus,
                    "",
                    { bombaPocoStatus = it },
                    {})
                ChecklistItem(
                    "6. Nível TBU ES03",
                    tbuEs03Status,
                    tbuEs03Obs,
                    { tbuEs03Status = it },
                    { tbuEs03Obs = it })
                ChecklistItem(
                    "7. Nível TBU ES04",
                    tbuEs04Status,
                    tbuEs04Obs,
                    { tbuEs04Status = it },
                    { tbuEs04Obs = it })

                Spacer(modifier = Modifier.height(24.dp))
            }

            items(listaEstufas) { estufa ->
                val p = "${estufa.id}_$horarioSelecionado"
                EstufaCard(
                    estufa = estufa,
                    valorTemp = leiturasEstufas["${p}_temp"] ?: "",
                    valorPress = leiturasEstufas["${p}_press"] ?: "",
                    valorUmidade = leiturasEstufas["${p}_umid"] ?: "",
                    valorTempo = leiturasEstufas["${p}_ciclo"] ?: "", // Mantido conforme seu fluxo local
                    valorStatus = leiturasEstufas["${p}_status"] ?: "Operação",
                    labelTempo = "Horas acumulada : horas",
                    onTempChange = { n -> leiturasEstufas["${p}_temp"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_temp", n, System.currentTimeMillis())) } },
                    onPressChange = { n -> leiturasEstufas["${p}_press"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_press", n, System.currentTimeMillis())) } },
                    onUmidadeChange = { n -> leiturasEstufas["${p}_umid"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_umid", n, System.currentTimeMillis())) } },
                    onTempoChange = { n -> leiturasEstufas["${p}_ciclo"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_ciclo", n, System.currentTimeMillis())) } },
                    onStatusChange = { n -> leiturasEstufas["${p}_status"] = n; coroutineScope.launch(Dispatchers.IO) { dao.salvarLeitura(LeituraEntity("${p}_status", n, System.currentTimeMillis())) } },
                    onVerGraficoClick = { estufaSelecionadaParaGrafico = estufa } // Captura a estufa do clique
                )
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    // --- DIÁLOGO DO GRÁFICO INDIVIDUAL DA ESTUFA (CARD) ---
    if (estufaSelecionadaParaGrafico != null) {
        // 💡 CORREÇÃO: lançamos um LaunchedEffect para buscar os dados uma vez sempre que a estufa mudar,
        // evitando que o collectAsState trave a renderização do botão Fechar.
        var historicoUmidadeLocal by remember { mutableStateOf<List<LeituraEntity>>(emptyList()) }

        LaunchedEffect(estufaSelecionadaParaGrafico) {
            dao.buscarHistoricoUmidade(estufaSelecionadaParaGrafico!!.id).collect { lista ->
                historicoUmidadeLocal = lista
            }
        }

        AlertDialog(
            onDismissRequest = { estufaSelecionadaParaGrafico = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 💡 Limpa o estado imediatamente na thread principal, destravando o fechamento
                        estufaSelecionadaParaGrafico = null
                    }
                ) {
                    Text("Fechar")
                }
            },
            title = {
                Text(
                    text = "Tendência de Umidade: ${estufaSelecionadaParaGrafico?.nome}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF435D56)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Histórico das últimas 6 medições registradas neste dispositivo:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Passa a lista segura e local para o gráfico
                    GraficoBarrasEstufa(historico = historicoUmidadeLocal)
                }
            }
        )
    }

    // --- DIÁLOGOS DE ENVIO DE RELATÓRIO E RESET ---
    if (mostrarDialogoRelatorio) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // BOTÃO 1: Envia apenas o texto para o WhatsApp
                    Button(
                        onClick = {
                            val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            val relatorioTexto = gerarTextoRelatorioFinal(
                                nome = nomeCaldeirista, data = dataAtual, pressaoComp = pressaoCompressor,
                                damper = "$damperStatus $damperObs", vazamento = "$vazamentoStatus $vazamentoObs",
                                caixa = nivelCaixaStatus, bomba = bombaPocoStatus,
                                tbu03 = "$tbuEs03Status $tbuEs03Obs", tbu04 = "$tbuEs04Status $tbuEs04Obs",
                                estufas = listaEstufas, horarioSelecionado = horarioSelecionado, leituras = leiturasEstufas
                            )

                            compartilharRelatorioTexto(contexto, relatorioTexto)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("1. Enviar Checklist no WhatsApp")
                    }
                    // BOTÃO 2: Gera e compartilha o PDF Geral com todas as rodadas históricas
                    Button(
                        onClick = {
                            val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                            // 💡 Chama a função passando o mapa de leituras bruto com todos os horários salvos
                            val arquivoPdf = criarRelatorioPdfIBK(
                                contexto = contexto,
                                nome = nomeCaldeirista,
                                data = dataAtual,
                                listaEstufas = listaEstufas,
                                leiturasEstufas = leiturasEstufas
                            )

                            if (arquivoPdf != null && arquivoPdf.exists()) {
                                compartilharApenasPdf(contexto, arquivoPdf)
                            } else {
                                Toast.makeText(contexto, "Erro ao gerar o PDF Histórico.", Toast.LENGTH_LONG).show()
                            }

                            mostrarDialogoRelatorio = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF435D56))
                    ) {
                        Text("2. Gerar PDF do Gráfico Geral (Todas as Coletas)")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoReset = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Ações do Relatório") },
            text = { Text("Escolha se deseja enviar o checklist em texto no WhatsApp ou gerar o documento PDF com o gráfico.") }
        )
    }

    if (mostrarDialogoReset) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            dao.limparDadosDoTurno()
                            launch(Dispatchers.Main) {
                                leiturasEstufas.clear()
                                pressaoCompressor = ""; damperStatus = ""; damperObs = ""; vazamentoStatus = ""; vazamentoObs = ""
                                nivelCaixaStatus = ""; bombaPocoStatus = ""
                                tbuEs03Status = ""; tbuEs04Status = ""; tbuEs04Obs = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Limpar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Resetar Tudo") },
            text = { Text("Isso apagará todas as medições do turno.") }
        )
    }
} // FIM DA MAINSCREEN

// --- FUNÇÃO DO TEXTO CORRIGIDA (leituras) ---
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

fun compartilharRelatorioTexto(contexto: Context, texto: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    contexto.startActivity(Intent.createChooser(intent, "Enviar Checklist via WhatsApp"))
}

fun compartilharApenasPdf(contexto: Context, arquivoPdf: File) {
    try {
        val uri = FileProvider.getUriForFile(
            contexto,
            "${contexto.packageName}.provider",
            arquivoPdf
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        contexto.startActivity(Intent.createChooser(intent, "Compartilhar PDF do Gráfico"))
    } catch (e: Exception) {
        Toast.makeText(contexto, "Erro ao compartilhar PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
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
    } catch (_: Exception) { input }
}