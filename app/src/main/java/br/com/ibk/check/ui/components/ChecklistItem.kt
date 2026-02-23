package br.com.ibk.check.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChecklistItem(
    label: String,
    selectedOption: String,
    observation: String,
    onOptionSelected: (String) -> Unit,
    onObservationChange: (String) -> Unit
) {
    // Usamos um Column para que o campo de texto empurre o que estiver embaixo
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp) // Aumentei um pouco o respiro
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            Row {
                // Botão OK
                OutlinedButton(
                    onClick = { onOptionSelected("OK") },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedOption == "OK") Color(0xFF435D56) else Color.Transparent,
                        contentColor = if (selectedOption == "OK") Color.White else Color(0xFF435D56)
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("OK")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botão NC
                OutlinedButton(
                    onClick = { onOptionSelected("NC") },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedOption == "NC") Color(0xFFD32F2F) else Color.Transparent,
                        contentColor = if (selectedOption == "NC") Color.White else Color(0xFFD32F2F)
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("NC")
                }
            }
        }

        // --- SOLUÇÃO COM ANIMAÇÃO ---
        // AnimatedVisibility garante que o Compose "reserve" espaço para o campo
        AnimatedVisibility(visible = selectedOption == "NC") {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = observation,
                    onValueChange = { onObservationChange(it) },
                    label = { Text("Justificativa da NC") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD32F2F),
                        unfocusedBorderColor = Color(0xFFD32F2F).copy(alpha = 0.5f),
                        focusedLabelColor = Color(0xFFD32F2F)
                    ),
                    singleLine = false,
                    minLines = 2 // Força o campo a ter um tamanho mínimo visível
                )
            }
        }
    }
}