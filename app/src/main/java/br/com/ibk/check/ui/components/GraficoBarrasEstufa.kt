package br.com.ibk.check.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibk.check.data.local.LeituraEntity

@Composable
fun GraficoBarrasEstufa(historico: List<LeituraEntity>) {
    // Inverte a lista para que a coleta mais antiga fique na esquerda e a mais recente na direita
    val dadosOrdenados = historico.reversed()

    if (dadosOrdenados.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhuma coleta registrada para esta estufa.", color = Color.Gray, fontSize = 14.sp)
        }
        return
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            val larguraCanvas = size.width
            val alturaCanvas = size.height
            val numBarras = dadosOrdenados.size

            val espacoEntreBarras = 24f
            val larguraBarra = (larguraCanvas - (espacoEntreBarras * (numBarras + 1))) / numBarras
            val alturaMaximaGrafico = alturaCanvas - 60f // Reserva espaço para textos em cima e embaixo

            dadosOrdenados.forEachIndexed { indice, leitura ->
                // Extrai o valor numérico da umidade (ex: "45" ou "12.5")
                val apenasNumeros = leitura.valor.filter { it.isDigit() || it == '.' || it == ',' }
                val umidade = apenasNumeros.replace(",", ".").toFloatOrNull() ?: 0f

                // Extrai o horário da chave (ex: de "1_08:30_umid" extrai "08:30")
                val partes = leitura.idChave.split("_")
                val horario = if (partes.size >= 2) partes[1] else "--:--"

                // Regra de três para calcular a altura proporcional da barra (considerando umidade máxima de 100%)
                val alturaBarra = (umidade / 100f) * alturaMaximaGrafico

                // Posição X e Y da barra
                val x = espacoEntreBarras + indice * (larguraBarra + espacoEntreBarras)
                val y = alturaCanvas - 40f - alturaBarra

                // 1. Desenha a Barra (Cor padrão da IBK)
                drawRect(
                    color = Color(0xFF435D56),
                    topLeft = Offset(x, y),
                    size = Size(larguraBarra, alturaBarra)
                )

                // 2. Desenha o Valor da Umidade (Texto em cima da barra)
                drawContext.canvas.nativeCanvas.apply {
                    val paintValor = android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textSize = 32f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawText("${umidade.toInt()}%", x + (larguraBarra / 2), y - 15f, paintValor)
                }

                // 3. Desenha a Legenda de Horário (Texto embaixo da barra)
                drawContext.canvas.nativeCanvas.apply {
                    val paintHorario = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText(horario, x + (larguraBarra / 2), alturaCanvas - 10f, paintHorario)
                }
            }
        }
    }
}
