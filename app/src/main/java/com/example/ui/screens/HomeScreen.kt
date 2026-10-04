package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.OilBarrel
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelPrice
import com.example.data.model.PromotionItem
import com.example.data.model.Station
import com.example.data.model.UserProfile
import com.example.ui.theme.EmeraldGradientEnd
import com.example.ui.theme.EmeraldGradientStart
import com.example.ui.theme.GoldGradientEnd
import com.example.ui.theme.GoldGradientStart
import com.example.ui.theme.OnPrimaryContainerEmerald
import com.example.ui.theme.OnPrimaryEmerald
import com.example.ui.theme.OnSecondaryGold
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryContainerEmerald
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SecondaryContainerGold
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryGreen
import java.util.Locale

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    fuelPrices: List<FuelPrice>,
    promotions: List<PromotionItem> = emptyList(),
    currentStation: Station?,
    onNavigateToWallet: () -> Unit,
    onNavigateToStations: () -> Unit,
    onNavigateToStore: () -> Unit,
    onNavigateToClube: () -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenBooking: (String) -> Unit,
    onOpenAdminManager: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Ultra-Modern VIP Economy Bento Hero
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.linearGradient(
                    listOf(
                        PrimaryEmerald.copy(alpha = 0.5f),
                        SecondaryGold.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vip_economy_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF132B1E),
                                Color(0xFF091710),
                                SurfaceDark
                            ),
                            radius = 900f
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Row: Greetings & VIP Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SecondaryGold)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = OnSecondaryGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = userProfile.tier.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSecondaryGold,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.8.sp
                                        )
                                    }
                                }

                                if (userProfile.vehiclePlate.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceContainerHigh.copy(alpha = 0.8f)
                                    ) {
                                        Text(
                                            text = if (userProfile.vehicleModel.isNotBlank()) "${userProfile.vehiclePlate} • ${userProfile.vehicleModel}" else userProfile.vehiclePlate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (userProfile.name.isNotBlank()) "Olá, ${userProfile.name}!" else "Olá, Motorista!",
                                style = MaterialTheme.typography.titleLarge,
                                color = OnSurface,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        // Right: Live Petros Protocol Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainerHigh.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryEmerald)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Petros v4.2",
                                    color = PrimaryEmerald,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Bento Metrics Row (Cashback, Points, Savings)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Metric 1: Cashback
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceContainerLowest.copy(alpha = 0.8f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryGold.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "CASHBACK",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "R$ ${String.format(Locale.GERMAN, "%.2f", userProfile.cashbackBalance)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SecondaryGold,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Disponível",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryGold.copy(alpha = 0.8f),
                                    fontSize = 9.sp
                                )
                            }
                        }

                        // Metric 2: Clube Points
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceContainerLowest.copy(alpha = 0.8f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "PONTOS CLUBE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${userProfile.pointsBalance} pts",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (userProfile.pointsBalance >= 500) "Ducha liberada" else "${500 - userProfile.pointsBalance} pts p/ Ducha",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryEmerald.copy(alpha = 0.8f),
                                    fontSize = 9.sp
                                )
                            }
                        }

                        // Metric 3: Monthly Savings
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceContainerLowest.copy(alpha = 0.8f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TertiaryGreen.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "ECONOMIA MÊS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "R$ ${String.format(Locale.GERMAN, "%.2f", userProfile.monthlySavings)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TertiaryGreen,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "No Clube 01",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TertiaryGreen.copy(alpha = 0.8f),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    // Direct Action CTA Row (Pagar no Bico + QR Code Totem)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToWallet,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerEmerald),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("home_abastecer_hero_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalGasStation,
                                    contentDescription = null,
                                    tint = OnPrimaryContainerEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Abastecer no Bico",
                                    color = OnPrimaryContainerEmerald,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onNavigateToClube,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceContainerHigh),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("home_ver_qr_hero_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = SecondaryGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "QR Bico",
                                    color = OnSurface,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Primary Station Card (Location & Fast Access)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceContainerLow,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToStations() }
                .testTag("primary_station_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentStation?.name ?: "Auto Posto 01 • Matriz Clériston Andrade",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryEmerald)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentStation?.isOpen24h != false) "Aberto 24h" else "Aberto",
                                    color = PrimaryEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${currentStation?.distanceKm ?: 1.2} km de você",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onNavigateToStations,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerHigh)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Alterar Posto",
                        tint = OnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 3. Digital Totem Fuel Price Board
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = SecondaryGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Totem Digital de Preços",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceContainerHigh,
                    modifier = Modifier
                        .clickable { onOpenAdminManager() }
                        .testTag("firestore_admin_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Ao Vivo • Firestore",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Preços",
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Totem Container
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceContainerLowest,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fuel_totem_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fuelPrices.forEach { fuel ->
                        FuelPriceItemRow(
                            fuel = fuel,
                            onClick = { onNavigateToWallet() }
                        )
                    }
                }
            }
        }

        // 4. Primary CTA High Octane Button
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
                onClick = onNavigateToWallet,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("home_abastecer_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = OnPrimaryContainerEmerald,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "Abastecer & Pagar Agora",
                        color = OnPrimaryContainerEmerald,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = OnPrimaryContainerEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Autorização digital direta na bomba com desconto automático",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        // 5. Quick Shortcut Cards (2x2 Modern Bento Grid)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Serviços & Conveniência",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Atendimento VIP",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryGold,
                    fontWeight = FontWeight.Bold
                )
            }

            // Grid Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Shortcut 1: Alcool vs Gasolina
                QuickShortcutCard(
                    title = "Álcool vs Gasolina",
                    subtitle = "Calculadora Flex",
                    icon = Icons.Default.Calculate,
                    iconColor = PrimaryEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCalculator
                )

                // Shortcut 2: Troca de Óleo
                QuickShortcutCard(
                    title = "Troca de Óleo",
                    subtitle = "Pit Stop Lubrax",
                    icon = Icons.Default.OilBarrel,
                    iconColor = SecondaryGold,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenBooking("Pit Stop Troca de Óleo") }
                )
            }

            // Grid Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Shortcut 3: Ducha Express
                QuickShortcutCard(
                    title = "Ducha Express",
                    subtitle = "Voucher Cortesia",
                    icon = Icons.Default.LocalCarWash,
                    iconColor = TertiaryGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenBooking("Ducha Express Gratuita") }
                )

                // Shortcut 4: Conveniência
                QuickShortcutCard(
                    title = "Conveniência",
                    subtitle = "Loja de Ofertas",
                    icon = Icons.Default.Storefront,
                    iconColor = PrimaryEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToStore
                )
            }
        }

        // 6. Promotional Carousels / Active Campaigns
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vantagens do Clube",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryEmerald))
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SurfaceContainerHigh))
                }
            }

            // Dynamic Promotions from Firestore
            val activePromos = promotions.filter { it.isActive }
            if (activePromos.isNotEmpty()) {
                activePromos.forEach { promo ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (promo.actionType) {
                                    "BOOKING" -> onOpenBooking(promo.actionTarget.ifBlank { promo.title })
                                    "CALCULATOR" -> onOpenCalculator()
                                    "STORE" -> onNavigateToStore()
                                    "WALLET" -> onNavigateToWallet()
                                    else -> onNavigateToWallet()
                                }
                            }
                            .testTag("promo_card_${promo.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(SecondaryGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = promo.tag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SecondaryGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = promo.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = promo.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = promo.buttonText,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PrimaryEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLowest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (promo.iconName) {
                                        "oil" -> Icons.Default.OilBarrel
                                        "eco" -> Icons.Default.Eco
                                        "cashback" -> Icons.Default.Paid
                                        "store" -> Icons.Default.Storefront
                                        else -> Icons.Default.LocalCarWash
                                    },
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Fallback default promo card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenBooking("Ducha Express Gratuita") }
                        .testTag("promo_ducha_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SecondaryGold.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "PROMOÇÃO 50 LITROS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryGold,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ducha Express Gratuita",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Abasteça 50L ou mais em uma única compra e ganhe a lavagem de cortesia.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalCarWash,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FuelPriceItemRow(
    fuel: FuelPrice,
    onClick: () -> Unit
) {
    val isAditivada = fuel.id == "gas-aditivada"
    val containerBg = if (isAditivada) SurfaceContainerHigh else SurfaceContainer

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerBg,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isAditivada) SecondaryContainerGold.copy(alpha = 0.2f) else SurfaceContainerHigh
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (fuel.iconType) {
                        "speed" -> Icons.Default.Speed
                        "eco" -> Icons.Default.Eco
                        "local_shipping" -> Icons.Default.LocalShipping
                        else -> Icons.Default.WaterDrop
                    }
                    val iconTint = when (fuel.iconType) {
                        "speed" -> SecondaryGold
                        "eco" -> TertiaryGreen
                        "local_shipping" -> SecondaryGold
                        else -> PrimaryEmerald
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = fuel.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        if (isAditivada) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SecondaryGold))
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "R$ ${String.format(Locale.GERMAN, "%.2f", fuel.pumpPrice)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SecondaryGold.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "-R$ ${String.format(Locale.GERMAN, "%.2f", fuel.discountPerLiter)}/L",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = fuel.badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isAditivada) SecondaryGold else PrimaryEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "R$ ",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isAditivada) SecondaryGold else PrimaryEmerald,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                    Text(
                        text = String.format(Locale.GERMAN, "%.2f", fuel.clubPrice),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isAditivada) SecondaryGold else PrimaryEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun QuickShortcutCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainer,
        modifier = modifier
            .clickable { onClick() }
            .testTag("quick_shortcut_${title.replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}
