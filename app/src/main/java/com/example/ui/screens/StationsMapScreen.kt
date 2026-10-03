package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.OilBarrel
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Station
import com.example.ui.theme.OnPrimaryContainerEmerald
import com.example.ui.theme.OnPrimaryEmerald
import com.example.ui.theme.OnSecondaryGold
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Outline
import com.example.ui.theme.PrimaryContainerEmerald
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SecondaryContainerGold
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SurfaceBright
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryGreen
import java.util.Locale

@Composable
fun StationsMapScreen(
    stations: List<Station>,
    selectedStation: Station?,
    onStationSelect: (Int) -> Unit,
    onNavigateToWallet: () -> Unit,
    onOpenBooking: (String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var detailStation by remember { mutableStateOf<Station?>(null) }

    val filters = listOf("Todos", "24 Horas", "Conveniência", "Troca de Óleo", "Calibrador", "Ducha", "GNV")

    val filteredStations = stations.filter { s ->
        val matchesSearch = searchQuery.isBlank() ||
                s.name.contains(searchQuery, ignoreCase = true) ||
                s.address.contains(searchQuery, ignoreCase = true) ||
                s.neighborhood.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "24 Horas" -> s.isOpen24h
            "Conveniência" -> s.hasConvenience
            "Troca de Óleo" -> s.hasOilChange
            "Calibrador" -> s.hasTireCalibration
            "Ducha" -> s.hasCarWash
            "GNV" -> s.hasGnv
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Search and Filter Chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por bairro, rodovia ou serviço...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Outline)
                },
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = "Filtros", tint = OnSurfaceVariant)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceContainerHigh,
                    unfocusedContainerColor = SurfaceContainerHigh,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedBorderColor = PrimaryEmerald,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_station_input")
            )

            // Horizontal Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PrimaryEmerald else SurfaceContainerHigh)
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("filter_chip_$filter"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (filter == "Todos") "Todos (${stations.size})" else filter,
                            color = if (isSelected) OnPrimaryEmerald else OnSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // 2. Map Stage Viewport Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(SurfaceContainerLowest)
                .testTag("interactive_map_canvas")
        ) {
            // Dark Tactical Radar and Roads Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Grid lines
                for (x in 0..w.toInt() step 70) {
                    drawLine(
                        color = Color(0x104EDEA3),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), h),
                        strokeWidth = 1f
                    )
                }
                for (y in 0..h.toInt() step 60) {
                    drawLine(
                        color = Color(0x104EDEA3),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(w, y.toFloat()),
                        strokeWidth = 1f
                    )
                }

                // Road network arcs
                val routePath = Path().apply {
                    moveTo(70f, h - 40f)
                    cubicTo(120f, h - 80f, 180f, h - 140f, 260f, 70f)
                }
                // Glow line
                drawPath(
                    path = routePath,
                    color = Color(0x3310B981),
                    style = Stroke(width = 12f)
                )
                // Active dashed route line
                drawPath(
                    path = routePath,
                    color = Color(0xFF4EDEA3),
                    style = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                    )
                )

                // Secondary road to Station 2
                val secPath = Path().apply {
                    moveTo(70f, h - 40f)
                    cubicTo(80f, h - 130f, 95f, 100f, 120f, 60f)
                }
                drawPath(
                    path = secPath,
                    color = Color(0x4086948A),
                    style = Stroke(
                        width = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                )
            }

            // User location dot (bottom-left)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 55.dp, bottom = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PrimaryEmerald.copy(alpha = 0.25f))
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainerEmerald)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            // Station 1 Pin (Selected / Matriz Central)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp, start = 70.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PrimaryContainerEmerald)
                    .border(1.5.dp, PrimaryEmerald, RoundedCornerShape(20.dp))
                    .clickable { onStationSelect(1) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("map_pin_1"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = null,
                        tint = OnPrimaryContainerEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "R$ 5,59",
                        color = OnPrimaryContainerEmerald,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Station 2 Pin (Rodovia Express)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 45.dp, start = 90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, SecondaryGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable { onStationSelect(2) }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("map_pin_2"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = SecondaryGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "R$ 5,75",
                        color = SecondaryGold,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Station 3 Pin (Jardim América)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 40.dp, top = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, TertiaryGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable { onStationSelect(3) }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("map_pin_3"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = TertiaryGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "R$ 5,55",
                        color = TertiaryGreen,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Floating Route Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceContainer.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.2f)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryContainerEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ROTA RECOMENDADA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryEmerald,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(PrimaryEmerald))
                            }
                            Text(
                                text = "Unidade 01 • Av. Brasil",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "4 min",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "1,2 km livre",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 3. Station Cards Stack Feed
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Unidades Próximas",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHigh)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${filteredStations.size} ativas",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = "Distância ▼",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            filteredStations.forEach { station ->
                StationCardItem(
                    station = station,
                    isSelected = selectedStation?.id == station.id,
                    onSelect = { onStationSelect(station.id) },
                    onNavigate = {
                        val gmmIntentUri = Uri.parse("geo:${station.latitude},${station.longitude}?q=${Uri.encode(station.name)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    },
                    onViewDetails = { detailStation = station }
                )
            }

            // Quality Guarantee Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainerEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Combustível 100% Auditado",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Controle rigoroso de qualidade em todas as bombas da rede",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Detail Station Dialog
    if (detailStation != null) {
        StationDetailDialog(
            station = detailStation!!,
            onDismiss = { detailStation = null },
            onFuelHere = {
                detailStation = null
                onStationSelect(detailStation?.id ?: 1)
                onNavigateToWallet()
            },
            onSchedule = { service ->
                detailStation = null
                onOpenBooking(service)
            }
        )
    }
}

@Composable
fun StationCardItem(
    station: Station,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onNavigate: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceContainerHigh else SurfaceContainer
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) PrimaryEmerald.copy(alpha = 0.5f) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("station_card_${station.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLowest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (station.id == 2) Icons.Default.LocalShipping else Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = if (station.id == 2) SecondaryGold else PrimaryEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = station.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${station.address} • ${station.neighborhood}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (station.isOpen24h) "Aberto 24h" else "Fecha às 23h",
                                    color = PrimaryEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${station.distanceKm} km",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                            if (station.id == 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "⚡ Rota rápida",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Price pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceContainerLowest,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (station.id == 2) "DIESEL S10" else "GASOLINA",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontSize = 9.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "R$ ",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (station.id == 2) SecondaryGold else PrimaryEmerald,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (station.id == 2) "5,75" else if (station.id == 3) "5,55" else "5,59",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (station.id == 2) SecondaryGold else PrimaryEmerald,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = if (station.id == 3) "Melhor Preço" else "Eco R$ 0,30/L",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryGold,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Amenities pills (Horizontal scroll for compact screens)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (station.hasConvenience) {
                    AmenityPill(icon = Icons.Default.Storefront, label = "Conveniência Express")
                }
                if (station.hasOilChange) {
                    AmenityPill(icon = Icons.Default.OilBarrel, label = "Troca de Óleo")
                }
                if (station.hasTireCalibration) {
                    AmenityPill(icon = Icons.Default.Speed, label = "Calibrador Digital")
                }
            }

            // Actions (Como Chegar / Ver Detalhes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigate,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("station_directions_${station.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        tint = OnPrimaryEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Como Chegar (${station.travelTimeMinutes} min)",
                        color = OnPrimaryEmerald,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLowest),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = "Ver Detalhes",
                        color = OnSurface,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AmenityPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = SurfaceContainerLowest
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = OnSurface, fontSize = 11.sp)
        }
    }
}

@Composable
fun StationDetailDialog(
    station: Station,
    onDismiss: () -> Unit,
    onFuelHere: () -> Unit,
    onSchedule: (String) -> Unit
) {
    var selectedSimAmount by remember { mutableIntStateOf(100) }
    val estimatedLiters = selectedSimAmount / 5.89
    val estimatedEconomy = estimatedLiters * 0.30

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("station_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "POSTO CREDENCIADO • MATRIZ", style = MaterialTheme.typography.labelSmall, color = SecondaryGold, fontWeight = FontWeight.Bold)
                        }
                        Text(text = station.name, style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.ExtraBold)
                        Text(text = station.address, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = OnSurfaceVariant)
                    }
                }

                // Quick Station Action Bar (Ligar, Compartilhar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = station.phone, style = MaterialTheme.typography.bodySmall, color = OnSurface)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${station.rating} (${station.reviewsCount})", style = MaterialTheme.typography.bodySmall, color = OnSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Simulator Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainerLowest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Simulador Rápido de Abastecimento",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(50, 100, 150, 200).forEach { amount ->
                                val isSelected = selectedSimAmount == amount
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PrimaryEmerald else SurfaceContainerHigh)
                                        .clickable { selectedSimAmount = amount }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "R$ $amount",
                                        color = if (isSelected) OnPrimaryEmerald else OnSurface,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Volume Estimado (Aditivada)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 9.sp)
                                Text(
                                    text = "${String.format(Locale.GERMAN, "%.2f", estimatedLiters)} Litros",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Você economiza", style = MaterialTheme.typography.labelSmall, color = SecondaryGold, fontSize = 9.sp)
                                Text(
                                    text = "+ R$ ${String.format(Locale.GERMAN, "%.2f", estimatedEconomy)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = SecondaryGold,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Services and scheduling
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "SERVIÇOS NA UNIDADE", style = MaterialTheme.typography.labelSmall, color = SecondaryGold)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.OilBarrel, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Pit Stop Troca de Óleo", style = MaterialTheme.typography.bodySmall, color = OnSurface, fontWeight = FontWeight.Bold)
                                    Text(text = "Box 01 e 02 • 07h às 20h", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 10.sp)
                                }
                            }
                            Button(
                                onClick = { onSchedule("Pit Stop Troca de Óleo") },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(text = "Agendar", color = OnSecondaryGold, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Primary CTA
                Button(
                    onClick = onFuelHere,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("detail_dialog_abastecer_aqui")
                ) {
                    Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = null, tint = OnPrimaryContainerEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Abastecer Nesta Unidade", color = OnPrimaryContainerEmerald, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}
