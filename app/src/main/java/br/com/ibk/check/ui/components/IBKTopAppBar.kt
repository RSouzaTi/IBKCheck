package br.com.ibk.check.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// Certifique-se de que este import aponta para o SEU pacote
import br.com.ibk.check.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IBKTopAppBar(actions: @Composable RowScope.() -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 1. A sua Logo
                Image(
                    painter = painterResource(id = R.drawable.icon__ibk),
                    contentDescription = "Logo IBK",
                    modifier = Modifier.height(35.dp).padding(bottom = 2.dp)
                )
                // 2. O Texto de Identificação abaixo da logo
                Text(
                    text = "RQ-019 Caldeira / Estufas",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        },
        // Removemos o navigationIcon (Menu Sandwich) conforme solicitado
        navigationIcon = {},
        actions = actions, // Onde vai aparecer o ícone de lixo (Delete)
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF435D56), // Verde IBK
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

