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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelPrice
import com.example.data.model.PaymentCard
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
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
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryGreen
import java.util.Locale

@Composable
fun WalletPaymentScreen(
    userProfile: UserProfile,
    pumpNumber: String,
    fuelPrice: FuelPrice?,
    volumeLiters: Double,
    useCashback: Boolean,
    selectedPaymentMethod: String,
    savedCards: List<PaymentCard>,
    transactions: List<TransactionRecord>,
    isProcessingPayment: Boolean,
    onPumpNumberChange: (String) -> Unit,
    onUseCashbackChange: (Boolean) -> Unit,
    onSelectPaymentMethod: (String) -> Unit,
    onScanQrClick: () -> Unit,
    onAddNewCardClick: () -> Unit,
    onAuthorizePayment: () -> Unit,
    onViewReceipt: (TransactionRecord) -> Unit,
    onViewAllHistory: () -> Unit
) {
    val fuel = fuelPrice ?: FuelPrice(
        id = "gas-aditivada",
        name = "Gasolina Aditivada",
        pumpPrice = 6.19,
        clubPrice = 5.89,
        discountPerLiter = 0.30,
        fuelType = "Max Clean"
    )

    val subtotal = volumeLiters * fuel.pumpPrice
    val discount = volumeLiters * fuel.discountPerLiter
    val totalWithDiscount = subtotal - discount
    val cashbackToDeduct = if (useCashback) {
        userProfile.cashbackBalance.coerceAtMost(totalWithDiscount)
    } else 0.0
    val finalTotal = (totalWithDiscount - cashbackToDeduct).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Context Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = PrimaryEmerald,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TRANSAÇÃO 100% CRIPTOGRAFADA",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceContainerHigh)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SecondaryGold))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Pista Ativa",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. Pump Identification Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pump_identifier_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Identificar Bomba", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                            Text(text = "Informe os 2 dígitos ou aponte a câmera", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                    Icon(imageVector = Icons.Default.Sensors, contentDescription = null, tint = Outline, modifier = Modifier.size(20.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pump Number Box
                    OutlinedTextField(
                        value = pumpNumber,
                        onValueChange = onPumpNumberChange,
                        label = { Text("Bomba", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedContainerColor = SurfaceContainerLowest,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(56.dp)
                            .testTag("input_pump_number")
                    )

                    // Escanear QR Button
                    Button(
                        onClick = onScanQrClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("scan_qr_button")
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Escanear QR", color = OnSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Live Fueling Summary Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fueling_summary_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pumpNumber,
                                color = PrimaryEmerald,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = fuel.name, style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = TertiaryGreen, modifier = Modifier.size(16.dp))
                            }
                            Text(text = "Bico 02 • Posto 01 Nações", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "DESCONTO CLUBE", style = MaterialTheme.typography.labelSmall, color = SecondaryGold, fontSize = 9.sp)
                        Text(
                            text = "-R$ ${String.format(Locale.GERMAN, "%.2f", discount)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = SecondaryGold,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Volume and Price/L Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Volume", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            Text(
                                text = "${String.format(Locale.GERMAN, "%.2f", volumeLiters)} L",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Preço/Litro", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            Text(
                                text = "R$ ${String.format(Locale.GERMAN, "%.2f", fuel.clubPrice)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Breakdown list
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal da Bomba", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        Text(
                            text = "R$ ${String.format(Locale.GERMAN, "%.2f", subtotal)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Redeem, contentDescription = null, tint = TertiaryGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Economia Aplicada", style = MaterialTheme.typography.bodySmall, color = TertiaryGreen)
                        }
                        Text(
                            text = "- R$ ${String.format(Locale.GERMAN, "%.2f", discount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TertiaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(text = "Total com Desconto", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            Text(
                                text = "Você economizou R$ ${String.format(Locale.GERMAN, "%.2f", discount)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "R$ ${String.format(Locale.GERMAN, "%.2f", totalWithDiscount)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // 4. Cashback Toggle Pill
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceContainer,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cashback_toggle_surface")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SecondaryGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Usar saldo de cashback", style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceContainerHigh)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "R$ ${String.format(Locale.GERMAN, "%.2f", userProfile.cashbackBalance)}",
                                    color = SecondaryGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(text = "Abater valor diretamente do total", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 11.sp)
                    }
                }

                Switch(
                    checked = useCashback,
                    onCheckedChange = onUseCashbackChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OnPrimaryEmerald,
                        checkedTrackColor = PrimaryEmerald,
                        uncheckedThumbColor = OnSurfaceVariant,
                        uncheckedTrackColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier.testTag("cashback_switch")
                )
            }
        }

        // 5. Payment Methods Selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Forma de Pagamento", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                Text(
                    text = "Gerenciar",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryEmerald,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onAddNewCardClick() }
                )
            }

            // Option 1: Pix Instantâneo
            PaymentMethodItem(
                title = "Pix Instantâneo",
                subtitle = "Receba +1% de cashback na carteira",
                badge = "MAIS RÁPIDO",
                isSelected = selectedPaymentMethod == "PIX",
                icon = Icons.Default.Bolt,
                iconTint = PrimaryEmerald,
                onClick = { onSelectPaymentMethod("PIX") }
            )

            // Option 2: Mastercard 4821
            PaymentMethodItem(
                title = "Mastercard •••• 4821",
                subtitle = "Crédito Digital • Auto Posto VIP",
                badge = "À vista (1x de R$ ${String.format(Locale.GERMAN, "%.2f", finalTotal)})",
                isSelected = selectedPaymentMethod == "CREDIT_4821",
                icon = Icons.Default.CreditCard,
                iconTint = SecondaryGold,
                onClick = { onSelectPaymentMethod("CREDIT_4821") }
            )

            // Option 3: Visa Débito 1092
            PaymentMethodItem(
                title = "Visa Débito •••• 1092",
                subtitle = "Débito em Conta • Banco Inter",
                badge = null,
                isSelected = selectedPaymentMethod == "DEBIT_1092",
                icon = Icons.Default.CreditCard,
                iconTint = OnSurfaceVariant,
                onClick = { onSelectPaymentMethod("DEBIT_1092") }
            )

            // Add new card button
            Button(
                onClick = onAddNewCardClick,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("add_card_button")
            ) {
                Icon(imageVector = Icons.Default.AddCard, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Adicionar Novo Cartão", color = OnSurface, fontWeight = FontWeight.Bold)
            }
        }

        // 6. Primary Biometric Confirmation Action Trigger
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
                onClick = onAuthorizePayment,
                enabled = !isProcessingPayment,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("biometric_pay_button")
            ) {
                if (isProcessingPayment) {
                    CircularProgressIndicator(color = OnPrimaryContainerEmerald, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Autorizando no Petros...", color = OnPrimaryContainerEmerald, fontWeight = FontWeight.Bold)
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = OnPrimaryContainerEmerald, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "TOQUE PARA AUTORIZAR", style = MaterialTheme.typography.labelSmall, color = OnPrimaryContainerEmerald.copy(alpha = 0.9f), fontSize = 10.sp)
                                Text(
                                    text = "Pagar R$ ${String.format(Locale.GERMAN, "%.2f", finalTotal)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnPrimaryContainerEmerald,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(OnPrimaryContainerEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "CONFIRMAR", color = OnPrimaryContainerEmerald, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = OnPrimaryContainerEmerald, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Text(
                text = "Ao confirmar, o valor é debitado e o bico liberado automaticamente.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 11.sp
            )
        }

        // 7. Recent Receipts Section (Simplified History)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Comprovantes Recentes", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onViewAllHistory() }
                ) {
                    Text(text = "Ver extrato", style = MaterialTheme.typography.bodySmall, color = SecondaryGold, fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                }
            }

            transactions.take(2).forEach { record ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewReceipt(record) }
                        .testTag("recent_receipt_${record.code}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = TertiaryGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${record.fuelType} • ${String.format(Locale.GERMAN, "%.2f", record.liters)} L",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = record.dateFormatted, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 11.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "R$ ${String.format(Locale.GERMAN, "%.2f", record.finalAmount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (record.paymentMethod.contains("Pix")) "PAGO VIA PIX" else record.paymentMethod.take(15).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (record.paymentMethod.contains("Pix")) TertiaryGreen else OnSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    subtitle: String,
    badge: String?,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) SurfaceContainerHigh else SurfaceContainer,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) PrimaryEmerald.copy(alpha = 0.5f) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("payment_method_${title.replace(" ", "_")}")
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLowest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = title, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                        if (badge != null && badge == "MAIS RÁPIDO") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SecondaryGold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = badge, color = OnSecondaryGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = if (isSelected && iconTint == PrimaryEmerald) PrimaryEmerald else OnSurfaceVariant, fontSize = 11.sp)
                    if (badge != null && badge != "MAIS RÁPIDO") {
                        Text(text = badge, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 10.sp)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PrimaryEmerald else SurfaceContainerLowest),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = OnPrimaryEmerald, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
