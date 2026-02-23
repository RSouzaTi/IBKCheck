package br.com.ibk.check.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.ibk.check.model.Estufa

@Composable
fun EstufaCard(
    estufa: Estufa,
    valorTemp: String,           // Novo: Recebe o valor da MainScreen
    valorPress: String,          // Novo: Recebe o valor da MainScreen
    onTempChange: (String) -> Unit, // Novo: Avisa a MainScreen quando mudar
    onPressChange: (String) -> Unit // Novo: Avisa a MainScreen quando mudar
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFF435D56).copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Temperatura",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface // Isso muda automaticamente entre preto e branco
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Campo Temperatura
                OutlinedTextField(
                    value = valorTemp, // Agora usa o valor que vem de fora
                    onValueChange = onTempChange, // Avisa quem o chamou
                    label = { Text("Temp. °C") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF435D56),
                        focusedLabelColor = Color(0xFF435D56)
                    )
                )

                // Campo Pressão
                OutlinedTextField(
                    value = valorPress, // Agora usa o valor que vem de fora
                    onValueChange = onPressChange, // Avisa quem o chamou
                    label = { Text("Pressão (${estufa.unidadePressao})") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF435D56),
                        focusedLabelColor = Color(0xFF435D56)
                    )
                )
            }
        }
    }
}