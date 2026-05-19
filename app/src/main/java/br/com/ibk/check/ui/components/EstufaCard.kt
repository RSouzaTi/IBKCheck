package br.com.ibk.check.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibk.check.model.Estufa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstufaCard(
    estufa: Estufa,
    valorTemp: String,
    valorPress: String,
    valorUmidade: String,
    valorTempo: String,
    valorStatus: String,
    labelTempo: String,
    onTempChange: (String) -> Unit,
    onPressChange: (String) -> Unit,
    onUmidadeChange: (String) -> Unit,
    onTempoChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onVerGraficoClick: () -> Unit // 💡 Parâmetro de clique conectado com sucesso!
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- CABEÇALHO DO CARD COM O BOTÃO NOVO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = estufa.nome,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF435D56),
                    fontWeight = FontWeight.Bold
                )

                // 💡 Botão discreto posicionado ao lado do título da estufa
                TextButton(onClick = onVerGraficoClick) {
                    Text("📊 Ver Gráfico", fontSize = 12.sp)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- SELETOR DE STATUS (Botões rápidos) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val opcoes = listOf("Operação", "Troca", "Manutenção")
                opcoes.forEach { opcao ->
                    InputChip(
                        selected = valorStatus == opcao,
                        onClick = { onStatusChange(opcao) },
                        label = { Text(opcao, style = MaterialTheme.typography.bodySmall) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = Color(0xFF435D56),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // SÓ MOSTRA OS CAMPOS SE ESTIVER EM OPERAÇÃO
            if (valorStatus == "Operação") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = valorTemp,
                        onValueChange = onTempChange,
                        label = { Text("Temp °C") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            autoCorrectEnabled = false
                        )
                    )
                    OutlinedTextField(
                        value = valorPress,
                        onValueChange = onPressChange,
                        label = { Text(estufa.unidadePressao) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            autoCorrectEnabled = false
                        )
                    )
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = valorUmidade,
                        onValueChange = onUmidadeChange,
                        label = { Text("Umidade %") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            autoCorrectEnabled = false
                        )
                    )
                    OutlinedTextField(
                        value = valorTempo,
                        onValueChange = onTempoChange,
                        label = { Text(labelTempo) },
                        placeholder = { Text("Ex: 345:23") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            autoCorrectEnabled = false
                        )
                    )
                }
            } else {
                // Mensagem limpa quando a estufa está em troca de lote ou manutenção
                Text(
                    text = "Estufa está em modo: ${valorStatus.uppercase()}. Campos de medição ocultos.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Gray
                )
            }
        }
    }
}