package com.example.trabalhofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AnimalScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastagemDropdown(
    selecionada: TipoPastagem,
    onSelecionar: (TipoPastagem) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = selecionada.rotulo,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de pastagem") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TipoPastagem.values().forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo.rotulo) },
                    onClick = {
                        onSelecionar(tipo)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AnimalScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = state.hectareTexto,
            onValueChange = viewModel::onHectareChange,
            label = { Text("Área (hectares)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        PastagemDropdown(
            selecionada = state.pastagem,
            onSelecionar = viewModel::onPastagemChange
        )

        Button(
            onClick = viewModel::calcular,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Text("Calcular")
        }

        state.resultado?.let { r ->
            Text(
                text = "Capacidade de suporte: ${"%.1f".format(r.unidadesAnimais)} UA",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Matéria seca aproveitável: ${"%.0f".format(r.materiaSecaKgAno)} kg/ano")
        }
    }
}