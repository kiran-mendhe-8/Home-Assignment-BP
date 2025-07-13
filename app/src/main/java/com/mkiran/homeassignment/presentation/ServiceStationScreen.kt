package com.mkiran.homeassignment.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mkiran.homeassignment.R
import com.mkiran.homeassignment.domain.model.ServiceStationDto

@Composable
fun ServiceStationScreen(viewModel: ServiceStationViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Radius", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                RadiusDropdown(radius = filter.radius, onRadiusChange = viewModel::setRadius)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { showFilterDialog = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filters"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (uiState) {
            is ServiceStationUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is ServiceStationUiState.Error -> {
                val message = (uiState as ServiceStationUiState.Error).message
                Text("Error: $message", color = MaterialTheme.colorScheme.error)
            }
            is ServiceStationUiState.Empty -> {
                Text("No stations found.", color = MaterialTheme.colorScheme.onBackground)
            }
            is ServiceStationUiState.Success -> {
                val stations = (uiState as ServiceStationUiState.Success).stations
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(stations) { station ->
                        ServiceStationListItem(station)
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            filter = filter,
            onDismiss = { showFilterDialog = false },
            onApply = { newFilter ->
                viewModel.updateFilter(newFilter)
                showFilterDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadiusDropdown(radius: Double, onRadiusChange: (Double) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(0.5, 1.0, 5.0)
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = "$radius miles",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .width(190.dp)
                .menuAnchor(),
            label = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text("$option miles") },
                    onClick = {
                        onRadiusChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FilterDialog(
    filter: ServiceStationFilter,
    onDismiss: () -> Unit,
    onApply: (ServiceStationFilter) -> Unit
) {
    var open24 by remember { mutableStateOf(filter.isOpen24Hours) }
    var store by remember { mutableStateOf(filter.hasConvenienceStore) }
    var hotFood by remember { mutableStateOf(filter.hasHotFood) }
    var bpCard by remember { mutableStateOf(filter.acceptsBpFuelCards) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filters") },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { open24 = !open24 }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = open24, onCheckedChange = { open24 = it })
                    Text("Open 24 Hours")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { store = !store }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = store, onCheckedChange = { store = it })
                    Text("Has Convenience Store")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hotFood = !hotFood }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = hotFood, onCheckedChange = { hotFood = it })
                    Text("Hot Food Available")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { bpCard = !bpCard }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = bpCard, onCheckedChange = { bpCard = it })
                    Text("Accepts BP Fuel Cards")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onApply(
                    filter.copy(
                        isOpen24Hours = open24,
                        hasConvenienceStore = store,
                        hasHotFood = hotFood,
                        acceptsBpFuelCards = bpCard
                    )
                )
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ServiceStationListItem(station: ServiceStationDto) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (station.imageUrl != null) {
            AsyncImage(
                model = station.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.bp),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(station.name, fontWeight = FontWeight.Bold)
            Text(station.address)
            Text("${station.distanceMiles} mi", style = MaterialTheme.typography.bodySmall)
        }
    }
} 