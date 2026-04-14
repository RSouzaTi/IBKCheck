package br.com.ibk.check.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.ibk.check.model.Estufa

@Composable
fun EstufaCard(
    estufa: Estufa,
    valorTemp: String,
    valorPress: String,
    valorUmidade: String, // Novo
    valorTempo: String,   // Novo
    onTempChange: (String) -> Unit,
    onPressChange: (String) -> Unit,
    onUmidadeChange: (String) -> Unit, // Novo
    onTempoChange: (String) -> Unit    // Novo
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = estufa.nome, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Campo Temperatura
                OutlinedTextField(
                    value = valorTemp,
                    onValueChange = onTempChange,
                    label = { Text("Temp °C") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                // Campo Pressão
                OutlinedTextField(
                    value = valorPress,
                    onValueChange = onPressChange,
                    label = { Text(estufa.unidadePressao) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Campo Umidade
                // Campo Umidade
                OutlinedTextField(
                    value = valorUmidade,
                    onValueChange = onUmidadeChange,
                    label = { Text("Umidade %") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Campo Tempo (Ajustado para Horas/Minutos)
                OutlinedTextField(
                    value = valorTempo,
                    onValueChange = onTempoChange,
                    label = { Text("Tempo (h:min)") },
                    placeholder = { Text("Ex: 48:30") }, // Dica visual para o usuário
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text) // Texto para permitir ":"
                )
            }
        }
    }
}