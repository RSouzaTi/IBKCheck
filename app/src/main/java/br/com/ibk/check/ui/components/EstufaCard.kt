package br.com.ibk.check.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.ibk.check.model.Estufa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstufaCard(
    estufa: Estufa,
    valorTemp: String,
    valorPress: String,
    valorUmidade: String,
    valorTempo: String,
    valorStatus: String, // Novo: Recebe o status atual (Operação, Troca, Manutenção)
    onTempChange: (String) -> Unit,
    onPressChange: (String) -> Unit,
    onUmidadeChange: (String) -> Unit,
    onTempoChange: (String) -> Unit,
    onStatusChange: (String) -> Unit, // Novo: Função para mudar o status
    labelTempo: String
) {
    // Definimos uma cor de fundo se não estiver em operação para ajudar o caldeirista visualmente
    val corFundo = when (valorStatus) {
        "Troca" -> Color(0xFFFFF9C4) // Amarelo claro
        "Manutenção" -> Color(0xFFFFEBEE) // Vermelho claro
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = corFundo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = estufa.nome, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                // Exibe um selo de Status se não estiver em operação
                if (valorStatus != "Operação") {
                    Text(text = valorStatus.uppercase(), color = Color.Red, fontWeight = FontWeight.ExtraBold)
                }
            }

            // --- SELETOR DE STATUS (Botões rápidos) ---
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = valorPress,
                        onValueChange = onPressChange,
                        label = { Text(estufa.unidadePressao) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = valorUmidade,
                        onValueChange = onUmidadeChange,
                        label = { Text("Umidade %") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = valorTempo,
                        onValueChange = onTempoChange,
                        label = { Text(labelTempo) },
                        placeholder = { Text("Ex: 345:23") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
            } else {
                // Mensagem quando a estufa está parada
                Text(
                    text = "Estufa está em modo: $valorStatus. Campos de medição ocultos.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Gray
                )
            }
        }
    }
}