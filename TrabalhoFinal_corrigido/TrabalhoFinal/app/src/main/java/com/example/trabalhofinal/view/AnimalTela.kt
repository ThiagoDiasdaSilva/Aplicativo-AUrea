package com.example.trabalhofinal.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhofinal.MainViewModel
import com.example.trabalhofinal.TipoPastagem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalTela(
    viewModel: MainViewModel = viewModel(),
    onAbrirSessoes: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.editandoId != null) "Editando Sessão" else "Cálculo de Pastagem"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.nomeTexto,
                onValueChange = viewModel::onNomeChange,
                label = { Text("Nome da sessão (opcional)") },
                singleLine = true
            )

            Text(
                text = "Áreas e Pastagens",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            state.areas.forEachIndexed { index, area ->
                key(index) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Área ${index + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row {
                                    if (state.areas.size > 1) {
                                        TextButton(onClick = { viewModel.removerArea(index) }) {
                                            Text("Remover", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                    if (index == state.areas.lastIndex) {
                                        TextButton(onClick = viewModel::adicionarArea) {
                                            Text("+ Adicionar")
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = area.hectareTexto,
                                onValueChange = { viewModel.onHectareChange(index, it) },
                                label = { Text("Tamanho (Hectares)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )

                            PastagemDropdown(
                                selecionada = area.pastagem,
                                onSelecionar = { viewModel.onPastagemChange(index, it) }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = viewModel::calcular,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcular Capacidade")
            }

            state.resultado?.let { r ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Resultado Estimado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Área total:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${"%.2f".format(r.totalHectares)} ha",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Matéria seca:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${"%.1f".format(r.materiaSecaKgAno)} kg/ano",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Capacidade de suporte:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${"%.2f".format(r.unidadesAnimais)} UA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = viewModel::salvar,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (state.editandoId != null) "Atualizar sessão" else "Salvar sessão")
                        }
                    }
                }
            }

            if (state.editandoId != null) {
                TextButton(
                    onClick = viewModel::cancelarEdicao,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar edição", color = MaterialTheme.colorScheme.error)
                }
            }

            HorizontalDivider()

            OutlinedButton(
                onClick = onAbrirSessoes,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver sessões salvas")
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