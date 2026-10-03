package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionRecord
import com.example.ui.theme.OnPrimaryEmerald
import com.example.ui.theme.OnSecondaryGold
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryContainerEmerald
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryGreen
import java.util.Locale

@Composable
fun HistoryScreen(
    transactions: List<TransactionRecord>,
    onBackClick: () -> Unit,
    onReceiptClick: (TransactionRecord) -> Unit
) {
    val totalLiters = transactions.sumOf { it.liters }
    val totalEconomy = transactions.sumOf { it.discount }
    val totalSpent = transactions.sumOf { it.finalAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("history_screen")
    ) {
        // Summary stats bar
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = SurfaceContainerHigh,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "VOLUME TOTAL", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 9.sp)
                    Text(
                        text = "${String.format(Locale.GERMAN, "%.1f", totalLiters)} L",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryEmerald,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(modifier = Modifier.size(1.dp, 30.dp).background(SurfaceContainerLowest))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "ECONOMIA CLUBE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 9.sp)
                    Text(
                        text = "R$ ${String.format(Locale.GERMAN, "%.2f", totalEconomy)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = SecondaryGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(modifier = Modifier.size(1.dp, 30.dp).background(SurfaceContainerLowest))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "TOTAL GASTO", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 9.sp)
                    Text(
                        text = "R$ ${String.format(Locale.GERMAN, "%.2f", totalSpent)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Histórico de Abastecimentos",
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Nenhum abastecimento registrado ainda.", color = OnSurfaceVariant)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(transactions) { record ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReceiptClick(record) }
                            .testTag("history_item_${record.code}")
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
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceContainerLowest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = TertiaryGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${record.fuelType} • ${String.format(Locale.GERMAN, "%.2f", record.liters)} L",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${record.stationName} (${record.dateFormatted})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Petros: ${record.petrosAuthCode}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrimaryEmerald,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• +${record.pointsEarned} pts",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SecondaryGold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "R$ ${String.format(Locale.GERMAN, "%.2f", record.finalAmount)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = record.paymentMethod,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
