package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FuelPrice
import com.example.data.model.PromotionItem
import com.example.ui.theme.OnPrimaryEmerald
import com.example.ui.theme.OnSecondaryGold
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryContainerEmerald
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceDark
import java.util.Locale

@Composable
fun AdminManagerDialog(
    fuelPrices: List<FuelPrice>,
    promotions: List<PromotionItem>,
    onUpdateFuelPrice: (fuelId: String, pumpPrice: Double, clubPrice: Double) -> Unit,
    onSavePromotion: (PromotionItem) -> Unit,
    onTogglePromotion: (promoId: String, isActive: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showNewPromoForm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("admin_manager_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryContainerEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = OnPrimaryEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Gestão Dinâmica",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SecondaryGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "FIRESTORE",
                                        color = SecondaryGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Text(
                                text = "Atualizações em tempo real sem novo deploy",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceContainerLow,
                    contentColor = PrimaryEmerald,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PrimaryEmerald
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Preços no Totem", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Promoções", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (selectedTab == 0) {
                        // Fuel Prices Management
                        Text(
                            text = "Altere os preços abaixo para sincronizar instantaneamente com todos os usuários conectados:",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )

                        fuelPrices.forEach { fuel ->
                            FuelPriceEditRow(
                                fuel = fuel,
                                onSave = { pump, club ->
                                    onUpdateFuelPrice(fuel.id, pump, club)
                                }
                            )
                        }
                    } else {
                        // Promotions Management
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Campanhas ativas no app:",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )

                            Button(
                                onClick = { showNewPromoForm = !showNewPromoForm },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerEmerald),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (showNewPromoForm) Icons.Default.Close else Icons.Default.Add,
                                    contentDescription = null,
                                    tint = OnPrimaryEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showNewPromoForm) "Cancelar" else "Nova Campanha",
                                    color = OnPrimaryEmerald,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (showNewPromoForm) {
                            NewPromotionForm(
                                onSave = { newPromo ->
                                    onSavePromotion(newPromo)
                                    showNewPromoForm = false
                                }
                            )
                        }

                        promotions.forEach { promo ->
                            PromotionAdminItemCard(
                                promotion = promo,
                                onToggle = { onTogglePromotion(promo.id, !promo.isActive) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer Done Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Fechar Painel",
                        color = OnSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FuelPriceEditRow(
    fuel: FuelPrice,
    onSave: (pumpPrice: Double, clubPrice: Double) -> Unit
) {
    var pumpPriceText by remember(fuel.pumpPrice) { mutableStateOf(String.format(Locale.US, "%.2f", fuel.pumpPrice)) }
    var clubPriceText by remember(fuel.clubPrice) { mutableStateOf(String.format(Locale.US, "%.2f", fuel.clubPrice)) }
    var isSaved by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = fuel.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = fuel.fuelType,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                val p = pumpPriceText.toDoubleOrNull() ?: fuel.pumpPrice
                val c = clubPriceText.toDoubleOrNull() ?: fuel.clubPrice
                val diff = (p - c).coerceAtLeast(0.0)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SecondaryGold.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Desconto: R$ ${String.format(Locale.US, "%.2f", diff)}/L",
                        color = SecondaryGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = pumpPriceText,
                    onValueChange = {
                        pumpPriceText = it
                        isSaved = false
                    },
                    label = { Text("Preço Bomba (R$)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = SurfaceContainerHigh,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = clubPriceText,
                    onValueChange = {
                        clubPriceText = it
                        isSaved = false
                    },
                    label = { Text("Preço Clube (R$)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryGold,
                        unfocusedBorderColor = SurfaceContainerHigh,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    ),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        val pump = pumpPriceText.replace(",", ".").toDoubleOrNull() ?: fuel.pumpPrice
                        val club = clubPriceText.replace(",", ".").toDoubleOrNull() ?: fuel.clubPrice
                        onSave(pump, club)
                        isSaved = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSaved) PrimaryEmerald else PrimaryContainerEmerald
                    ),
                    modifier = Modifier.height(54.dp)
                ) {
                    if (isSaved) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Salvo", tint = OnPrimaryEmerald, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Salvar", color = OnPrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PromotionAdminItemCard(
    promotion: PromotionItem,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (promotion.isActive) SurfaceContainerLow else SurfaceContainerLowest
        ),
        border = if (promotion.isActive) androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.2f)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (promotion.isActive) SecondaryGold.copy(alpha = 0.15f) else SurfaceContainerHigh
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = promotion.tag,
                            color = if (promotion.isActive) SecondaryGold else OnSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (promotion.isActive) "Ativa no App" else "Inativa",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (promotion.isActive) PrimaryEmerald else OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = promotion.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (promotion.isActive) OnSurface else OnSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = promotion.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Switch(
                checked = promotion.isActive,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OnPrimaryEmerald,
                    checkedTrackColor = PrimaryEmerald,
                    uncheckedThumbColor = OnSurfaceVariant,
                    uncheckedTrackColor = SurfaceContainerHigh
                )
            )
        }
    }
}

@Composable
fun NewPromotionForm(
    onSave: (PromotionItem) -> Unit
) {
    var tag by remember { mutableStateOf("CAMPANHA RELÂMPAGO") }
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var badge by remember { mutableStateOf("OFERTA ESPECIAL") }
    var buttonText by remember { mutableStateOf("Aproveitar") }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Cadastrar Nova Campanha no Firestore:",
                style = MaterialTheme.typography.labelLarge,
                color = SecondaryGold,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                label = { Text("Tag Promocional", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título da Promoção (ex: Cashback Triplo)", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Subtítulo / Descrição Rápida", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = badge,
                onValueChange = { badge = it },
                label = { Text("Selo / Benefício (ex: 3% VOLTA)", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newPromo = PromotionItem(
                            id = "promo-custom-${System.currentTimeMillis()}",
                            tag = tag.ifBlank { "PROMOÇÃO" },
                            title = title,
                            subtitle = subtitle.ifBlank { "Válido por tempo limitado para membros do Clube" },
                            badge = badge.ifBlank { "DESCONTO" },
                            buttonText = buttonText,
                            actionType = "WALLET",
                            iconName = "flash",
                            isActive = true,
                            order = 0
                        )
                        onSave(newPromo)
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerEmerald),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = OnPrimaryEmerald, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Publicar Promoção no Firestore", color = OnPrimaryEmerald, fontWeight = FontWeight.Bold)
            }
        }
    }
}
