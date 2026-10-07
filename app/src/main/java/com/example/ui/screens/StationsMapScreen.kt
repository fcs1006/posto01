package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
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
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
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
    var mapZoomLevel by remember { mutableStateOf(1.0f) }

    val filters = listOf("Todos", "24 Horas", "Conveniência", "Troca de Óleo", "Calibrador", "Ducha", "GNV", "Eletroposto EV")

    val filteredStations = stations.filter { s ->
        val matchesSearch = searchQuery.isBlank() ||
                s.name.contains(searchQuery, ignoreCase = true) ||
                s.address.contains(searchQuery, ignoreCase = true) ||
                s.neighborhood.contains(searchQuery, ignoreCase = true) ||
                s.code.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "24 Horas" -> s.isOpen24h
            "Conveniência" -> s.hasConvenience
            "Troca de Óleo" -> s.hasOilChange
            "Calibrador" -> s.hasTireCalibration
            "Ducha" -> s.hasCarWash
            "GNV" -> s.hasGnv
            "Eletroposto EV" -> s.hasEvCharging
            else -> true
        }
        matchesSearch && matchesFilter
    }

    // Determine current active station
    val activeStation = selectedStation ?: stations.firstOrNull()

    // Dynamic GPS coordinate bounding box from Firestore stations
    val lats = stations.map { it.latitude }
    val lngs = stations.map { it.longitude }
    val minLatVal = lats.minOrNull() ?: -12.1460
    val maxLatVal = lats.maxOrNull() ?: -12.1380
    val minLngVal = lngs.minOrNull() ?: -44.9930
    val maxLngVal = lngs.maxOrNull() ?: -44.9770

    val latSpan = (maxLatVal - minLatVal).coerceAtLeast(0.008)
    val lngSpan = (maxLngVal - minLngVal).coerceAtLeast(0.012)

    val baseMinLat = minLatVal - latSpan * 0.25
    val baseMaxLat = maxLatVal + latSpan * 0.25
    val baseMinLng = minLngVal - lngSpan * 0.25
    val baseMaxLng = maxLngVal + lngSpan * 0.25

    // Reference User Location in Barreiras - BA
    val userLat = (minLatVal + maxLatVal) / 2.0 - (latSpan * 0.1)
    val userLng = (minLngVal + maxLngVal) / 2.0 + (lngSpan * 0.1)
    val userNormX = ((userLng - baseMinLng) / (baseMaxLng - baseMinLng)).toFloat().coerceIn(0.12f, 0.88f)
    val userNormY = (1f - ((userLat - baseMinLat) / (baseMaxLat - baseMinLat)).toFloat()).coerceIn(0.12f, 0.88f)

    // Pulsing animation for selected pin
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Search Bar & Filter Chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar posto por nome, bairro ou serviço...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Outline)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Limpar", tint = OnSurfaceVariant)
                        }
                    } else {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = "Filtros", tint = OnSurfaceVariant)
                        }
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

        // 2. Interactive Map Viewport with Firestore Coordinates
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(SurfaceContainerLowest)
                .testTag("interactive_map_canvas")
        ) {
            val mapWidth = constraints.maxWidth.toFloat()
            val mapHeight = constraints.maxHeight.toFloat()

            // Tactical Radar & Real-Time Road Arteries Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw coordinate grid lines
                for (x in 0..w.toInt() step 65) {
                    drawLine(
                        color = Color(0x124EDEA3),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), h),
                        strokeWidth = 1f
                    )
                }
                for (y in 0..h.toInt() step 55) {
                    drawLine(
                        color = Color(0x124EDEA3),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(w, y.toFloat()),
                        strokeWidth = 1f
                    )
                }

                // Reference: User Location in Barreiras - BA
                val userPos = Offset(userNormX * w, userNormY * h)

                // Draw connecting road networks between Firestore stations
                val activeNormX = activeStation?.let { ((it.longitude - baseMinLng) / (baseMaxLng - baseMinLng)).toFloat() } ?: 0.5f
                val activeNormY = activeStation?.let { (1f - ((it.latitude - baseMinLat) / (baseMaxLat - baseMinLat)).toFloat()) } ?: 0.5f
                val activePos = Offset(activeNormX * w, activeNormY * h)

                // Active Route to selected station
                val routePath = Path().apply {
                    moveTo(userPos.x, userPos.y)
                    val midX = (userPos.x + activePos.x) / 2f
                    val midY = (userPos.y + activePos.y) / 2f - 25f
                    quadraticTo(midX, midY, activePos.x, activePos.y)
                }

                // Route Glow
                drawPath(
                    path = routePath,
                    color = Color(0x3310B981),
                    style = Stroke(width = 14f)
                )

                // Dashed GPS Route Line
                drawPath(
                    path = routePath,
                    color = Color(0xFF4EDEA3),
                    style = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), 0f)
                    )
                )

                // Draw secondary road networks between other stations
                stations.forEachIndexed { i, st ->
                    if (i < stations.size - 1) {
                        val next = stations[i + 1]
                        val sx = ((st.longitude - baseMinLng) / (baseMaxLng - baseMinLng)).toFloat() * w
                        val sy = (1f - ((st.latitude - baseMinLat) / (baseMaxLat - baseMinLat)).toFloat()) * h
                        val nx = ((next.longitude - baseMinLng) / (baseMaxLng - baseMinLng)).toFloat() * w
                        val ny = (1f - ((next.latitude - baseMinLat) / (baseMaxLat - baseMinLat)).toFloat()) * h

                        drawLine(
                            color = Color(0x2586948A),
                            start = Offset(sx, sy),
                            end = Offset(nx, ny),
                            strokeWidth = 2.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )
                    }
                }
            }

            // Top Status Bar: Firestore Cloud Coordinates Badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerHigh.copy(alpha = 0.92f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Barreiras - BA • ${stations.size} Postos (Grupo 01)",
                            color = OnSurface,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Top-Right Map Controls (Zoom / Recenter)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHigh.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerLowest),
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(
                        onClick = { mapZoomLevel = (mapZoomLevel + 0.2f).coerceAtMost(2.0f) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = OnSurface, modifier = Modifier.size(18.dp))
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHigh.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerLowest),
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(
                        onClick = { mapZoomLevel = (mapZoomLevel - 0.2f).coerceAtLeast(0.6f) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = OnSurface, modifier = Modifier.size(18.dp))
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = PrimaryEmerald,
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (activeStation != null) {
                                onStationSelect(activeStation.id)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Centralizar", tint = OnPrimaryEmerald, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // User Location Marker (Ponto Azul / Verde Militar)
            val userLeftDp = (userNormX * maxWidth.value - 12).dp
            val userTopDp = (userNormY * maxHeight.value - 12).dp

            Box(
                modifier = Modifier
                    .offset(x = userLeftDp, y = userTopDp)
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size((24 * pulseScale).dp)
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

            // DYNAMIC PINS FOR EVERY STATION IN FIRESTORE
            filteredStations.forEach { station ->
                val normX = ((station.longitude - baseMinLng) / (baseMaxLng - baseMinLng)).toFloat().coerceIn(0.08f, 0.92f)
                val normY = (1f - ((station.latitude - baseMinLat) / (baseMaxLat - baseMinLat)).toFloat()).coerceIn(0.10f, 0.86f)

                val isSelected = activeStation?.id == station.id
                val pinWidth = if (isSelected) 100.dp else 78.dp
                val pinHeight = if (isSelected) 36.dp else 28.dp

                val leftOffset = (normX * maxWidth.value - (pinWidth.value / 2)).dp
                val topOffset = (normY * maxHeight.value - pinHeight.value).dp

                Box(
                    modifier = Modifier
                        .offset(x = leftOffset, y = topOffset)
                        .clickable {
                            onStationSelect(station.id)
                        }
                        .testTag("map_pin_${station.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        // Pulse glow ring
                        Box(
                            modifier = Modifier
                                .size(pinWidth * 1.15f, pinHeight * 1.4f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(PrimaryEmerald.copy(alpha = 0.25f))
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) PrimaryContainerEmerald else SurfaceContainerHigh,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) PrimaryEmerald else SecondaryGold.copy(alpha = 0.5f)
                        ),
                        shadowElevation = if (isSelected) 8.dp else 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when {
                                    station.hasEvCharging -> Icons.Default.ElectricBolt
                                    station.id == 2 -> Icons.Default.LocalShipping
                                    else -> Icons.Default.LocalGasStation
                                },
                                contentDescription = null,
                                tint = if (isSelected) OnPrimaryContainerEmerald else if (station.hasEvCharging) TertiaryGreen else SecondaryGold,
                                modifier = Modifier.size(if (isSelected) 16.dp else 12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = station.code,
                                color = if (isSelected) OnPrimaryContainerEmerald else OnSurface,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = if (isSelected) 11.sp else 10.sp
                            )
                        }
                    }
                }
            }

            // Bottom Floating Card for the Selected Station
            if (activeStation != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainer.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .clickable { detailStation = activeStation }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryContainerEmerald.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = activeStation.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "📍 Lat: ${String.format(Locale.US, "%.4f", activeStation.latitude)}, Lng: ${String.format(Locale.US, "%.4f", activeStation.longitude)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryEmerald,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Direct Navigate Intent Button
                        Button(
                            onClick = {
                                val uri = Uri.parse("geo:${activeStation.latitude},${activeStation.longitude}?q=${activeStation.latitude},${activeStation.longitude}(${Uri.encode(activeStation.name)})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(fallbackIntent)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Directions, contentDescription = null, tint = OnPrimaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Rota", color = OnPrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 3. Station Cards Stack Feed from Firestore
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
                        text = "Unidades Conectadas",
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
                            text = "${filteredStations.size} postos",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryEmerald,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Ordenar por Proximidade",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Cards for every station in Firestore
            filteredStations.forEach { station ->
                StationCardItem(
                    station = station,
                    isSelected = activeStation?.id == station.id,
                    onSelect = { onStationSelect(station.id) },
                    onNavigate = {
                        val uri = Uri.parse("geo:${station.latitude},${station.longitude}?q=${station.latitude},${station.longitude}(${Uri.encode(station.name)})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${station.latitude},${station.longitude}"))
                            context.startActivity(browserIntent)
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
                            text = "Coordenadas Auditadas e Atualizadas",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Coordenadas precisas para sua navegação",
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
                val stId = detailStation?.id ?: 1
                detailStation = null
                onStationSelect(stId)
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
            1.5.dp,
            if (isSelected) PrimaryEmerald.copy(alpha = 0.6f) else Color.Transparent
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                station.hasEvCharging -> Icons.Default.ElectricBolt
                                station.id == 2 -> Icons.Default.LocalShipping
                                else -> Icons.Default.LocalGasStation
                            },
                            contentDescription = null,
                            tint = if (station.hasEvCharging) TertiaryGreen else if (station.id == 2) SecondaryGold else PrimaryEmerald,
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
                                text = "${station.distanceKm} km • ${station.travelTimeMinutes} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Rating Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceContainerLowest,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = station.rating.toString(), color = OnSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Coordinates Badge in Card
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceContainerLowest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Coordenadas GPS:",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.4f", station.latitude)}, ${String.format(Locale.US, "%.4f", station.longitude)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryEmerald,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            // Amenities Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (station.hasConvenience) ServiceTagItem(icon = Icons.Default.Storefront, label = "Conveniência")
                if (station.hasOilChange) ServiceTagItem(icon = Icons.Default.OilBarrel, label = "Troca de Óleo")
                if (station.hasCarWash) ServiceTagItem(icon = Icons.Default.LocalGasStation, label = "Ducha")
                if (station.hasEvCharging) ServiceTagItem(icon = Icons.Default.ElectricBolt, label = "Eletroposto EV")
                if (station.hasGnv) ServiceTagItem(icon = Icons.Default.LocalGasStation, label = "GNV")
                if (station.hasTireCalibration) ServiceTagItem(icon = Icons.Default.Speed, label = "Calibrador")
            }

            // Actions: Rota no Google Maps e Ver Detalhes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Text(text = "Ver Detalhes", color = OnSurface, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigate,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerEmerald),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Icon(imageVector = Icons.Default.Directions, contentDescription = null, tint = OnPrimaryContainerEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Traçar Rota", color = OnPrimaryContainerEmerald, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ServiceTagItem(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = SurfaceContainerLowest
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 10.sp)
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
    val context = LocalContext.current
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
                            Text(text = "POSTO CREDENCIADO • ${station.code}", style = MaterialTheme.typography.labelSmall, color = SecondaryGold, fontWeight = FontWeight.Bold)
                        }
                        Text(text = station.name, style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.ExtraBold)
                        Text(text = station.address, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = OnSurfaceVariant)
                    }
                }

                // Coordinates card in Dialog
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
                            Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Coordenadas:", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 11.sp)
                        }
                        Text(
                            text = "${String.format(Locale.US, "%.5f", station.latitude)}, ${String.format(Locale.US, "%.5f", station.longitude)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }

                // Quick Station Action Bar (Ligar, Compartilhar, Rota)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${station.phone}"))
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Ligar", style = MaterialTheme.typography.bodySmall, color = OnSurface, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, station.name)
                                    putExtra(Intent.EXTRA_TEXT, "Abasteça na ${station.name}\nEndereço: ${station.address}\nCoordenadas: ${station.latitude},${station.longitude}\nLink: https://www.google.com/maps/dir/?api=1&destination=${station.latitude},${station.longitude}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Posto"))
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Compartilhar", style = MaterialTheme.typography.bodySmall, color = OnSurface, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val uri = Uri.parse("geo:${station.latitude},${station.longitude}?q=${station.latitude},${station.longitude}(${Uri.encode(station.name)})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${station.latitude},${station.longitude}"))
                                    context.startActivity(webIntent)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Directions, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Navegar", style = MaterialTheme.typography.bodySmall, color = OnSurface, fontWeight = FontWeight.Bold)
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
