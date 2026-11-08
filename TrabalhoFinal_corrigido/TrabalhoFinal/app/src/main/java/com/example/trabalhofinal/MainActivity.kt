package com.example.trabalhofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

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
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
            TipoPastagem.entries.forEach { tipo ->
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
    val contatos by viewModel.contatos.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.nomeTexto,
            onValueChange = viewModel::onNomeChange,
            label = { Text("Nome da sessão (opcional)") },
            singleLine = true
        )

        state.areas.forEachIndexed { index, area ->
            key(index) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = area.hectareTexto,
                        onValueChange = { viewModel.onHectareChange(index, it) },
                        label = { Text("Hectares") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    if (state.areas.size > 1) {
                        TextButton(onClick = { viewModel.removerArea(index) }) { Text("−") }
                    }
                    if (index == state.areas.lastIndex) {
                        TextButton(onClick = viewModel::adicionarArea) { Text("+") }
                    }
                }

                PastagemDropdown(
                    selecionada = area.pastagem,
                    onSelecionar = { viewModel.onPastagemChange(index, it) }
                )
            }
        }


        Button(onClick = viewModel::calcular, modifier = Modifier.fillMaxWidth()) {
            Text("Calcular")
        }

        state.resultado?.let { r ->
            Text("Total: ${"%.2f".format(r.totalHectares)} ha")
            Text("Matéria seca: ${"%.1f".format(r.materiaSecaKgAno)} kg/ano")
            Text("Capacidade: ${"%.2f".format(r.unidadesAnimais)} UA")
            Button(onClick = viewModel::salvar, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.editandoId != null) "Atualizar sessão" else "Salvar sessão")
            }
        }

        if (state.editandoId != null) {
            TextButton(onClick = viewModel::cancelarEdicao, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar edição")
            }
        }

        Spacer(Modifier.height(8.dp))

        if (contatos.isNotEmpty()) {
            Text("Sessões salvas", style = MaterialTheme.typography.titleMedium)
        }
        contatos.forEach { c ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(c.nome, style = MaterialTheme.typography.bodyLarge)
                    Text("${c.hectare} ha · ${c.pastagem}", style = MaterialTheme.typography.bodySmall)
                }
                TextButton(onClick = { viewModel.excluir(c) }) { Text("Excluir") }
                TextButton(onClick = {viewModel.editar(c)
                scope.launch {scrollState.animateScrollTo(0)
                }}) {Text ("Editar")}
            }
        }
    }
}
