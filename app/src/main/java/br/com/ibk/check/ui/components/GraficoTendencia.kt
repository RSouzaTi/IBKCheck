package br.com.ibk.check.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun GraficoTendencia(dadosUmidade: List<Float>) {
    // Mentor: Criamos o modelo de dados que o Vico entende
    val model = entryModelOf(*dadosUmidade.toTypedArray())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color.White)
            .padding(8.dp)
    ) {
        Chart(
            chart = lineChart(),
            model = model,
            startAxis = rememberStartAxis(title = "Umid %"),
            bottomAxis = rememberBottomAxis(title = "Últimas Coletas"),
            modifier = Modifier.fillMaxSize()
        )
    }
}
