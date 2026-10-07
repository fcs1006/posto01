package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.theme.OnPrimaryContainerEmerald
import com.example.ui.theme.OnPrimaryEmerald
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryContainerEmerald
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceDark

private const val CPF_MASK = "###.###.###-##"
private const val PHONE_MASK = "(##) #####-####"
private const val DATE_MASK = "##/##/####"

@Composable
fun CompleteProfileScreen(
    userProfile: UserProfile,
    onComplete: (name: String, phone: String, cpf: String, birthDate: String, password: String) -> Unit,
    onExit: () -> Unit
) {
    var name by remember { mutableStateOf(userProfile.name) }
    var phone by remember { mutableStateOf(userProfile.phone.filter { it.isDigit() }) }
    var cpf by remember { mutableStateOf(userProfile.cpf.filter { it.isDigit() }) }
    var birthDate by remember { mutableStateOf(userProfile.birthDate.filter { it.isDigit() }) }
    var vehiclePlate by remember { mutableStateOf(userProfile.vehiclePlate) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isNameValid = name.isNotBlank()
    val isPhoneValid = phone.length >= 10
    val isCpfValid = isValidCpf(cpf)
    val isBirthDateValid = isValidBirthDate(birthDate)
    val isPasswordValid = password.length >= 6
    val isConfirmValid = confirmPassword.isNotBlank() && confirmPassword == password
    val isFormValid = isNameValid && isPhoneValid && isCpfValid && isBirthDateValid && isPasswordValid && isConfirmValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // 1. Header Banner
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerHigh,
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainerEmerald.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CLUBE 01",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryGold,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Complete Seu Cadastro",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SecondaryGold.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ETAPA FINAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "O CPF é obrigatório para identificação na bomba física e liberação imediata dos seus descontos do Clube 01.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progresso do Cadastro",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "80%",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    LinearProgressIndicator(
                        progress = { 0.8f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimaryEmerald,
                        trackColor = SurfaceContainerLowest
                    )
                }
            }
        }

        // 2. Form Card Container
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Dados do Membro",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )

                // E-mail (Read Only)
                OutlinedTextField(
                    value = userProfile.email,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("E-mail Google Vinculado", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = OnSurfaceVariant) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = OnSurfaceVariant,
                        disabledBorderColor = SurfaceContainerHigh,
                        disabledLabelColor = OnSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Nome Completo
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Nome Completo", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryEmerald) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complete_name_input")
                )

                // CPF
                OutlinedTextField(
                    value = cpf,
                    onValueChange = { input -> cpf = input.filter { it.isDigit() }.take(11); errorMessage = null },
                    label = { Text("CPF (para desconto no bico)", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = SecondaryGold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = MaskVisualTransformation(CPF_MASK),
                    singleLine = true,
                    placeholder = { Text("000.000.000-00") },
                    isError = cpf.isNotBlank() && !isCpfValid,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complete_cpf_input")
                )

                // Telefone / WhatsApp
                OutlinedTextField(
                    value = phone,
                    onValueChange = { input -> phone = input.filter { it.isDigit() }.take(11); errorMessage = null },
                    label = { Text("Telefone / WhatsApp", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryEmerald) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = MaskVisualTransformation(PHONE_MASK),
                    singleLine = true,
                    placeholder = { Text("(00) 00000-0000") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complete_phone_input")
                )

                // Data de Nascimento
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { input -> birthDate = input.filter { it.isDigit() }.take(8); errorMessage = null },
                    label = { Text("Data de Nascimento", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null, tint = PrimaryEmerald) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = MaskVisualTransformation(DATE_MASK),
                    singleLine = true,
                    placeholder = { Text("dd/mm/aaaa") },
                    isError = birthDate.isNotBlank() && !isBirthDateValid,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complete_birthdate_input")
                )

                // Senha
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Crie uma Senha de Acesso (mínimo 6 caracteres)", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryEmerald) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = OnSurfaceVariant
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complete_password_input")
                )

                // Confirmar Senha
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = null },
                    label = { Text("Confirme a Senha", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = PrimaryEmerald) },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = OnSurfaceVariant
                            )
                        }
                    },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = confirmPassword.isNotBlank() && confirmPassword != password,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complete_confirmpassword_input")
                )

                if (errorMessage != null) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        when {
                            !isNameValid -> errorMessage = "Informe seu nome completo."
                            !isPhoneValid -> errorMessage = "Informe um telefone válido com DDD."
                            !isCpfValid -> errorMessage = "Informe um CPF válido para liberar o desconto na bomba."
                            !isBirthDateValid -> errorMessage = "Informe uma data de nascimento válida (dd/mm/aaaa)."
                            !isPasswordValid -> errorMessage = "A senha deve ter pelo menos 6 caracteres."
                            !isConfirmValid -> errorMessage = "As senhas não coincidem."
                            else -> {
                                errorMessage = null
                                onComplete(
                                    name.trim(),
                                    applyMask(phone, PHONE_MASK),
                                    applyMask(cpf, CPF_MASK),
                                    applyMask(birthDate, DATE_MASK),
                                    password
                                )
                            }
                        }
                    },
                    enabled = isFormValid,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainerEmerald,
                        disabledContainerColor = PrimaryContainerEmerald.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("complete_submit_button")
                ) {
                    Text(
                        text = "Concluir Cadastro & Ativar Clube 01",
                        color = OnPrimaryEmerald,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = onExit) {
                        Text(text = "Sair e cancelar", color = OnSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ---------- Helpers ----------

private fun applyMask(digits: String, mask: String): String {
    var digitIndex = 0
    val out = StringBuilder()
    for (ch in mask) {
        if (digitIndex >= digits.length) break
        if (ch == '#') {
            out.append(digits[digitIndex])
            digitIndex++
        } else {
            out.append(ch)
        }
    }
    return out.toString()
}

private class MaskVisualTransformation(private val mask: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val formatted = applyMask(digits, mask)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var count = 0
                for (i in formatted.indices) {
                    if (formatted[i].isDigit()) {
                        if (count == offset) return i
                        count++
                    }
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return formatted.take(offset).count { it.isDigit() }
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

private fun isValidCpf(cpf: String): Boolean {
    if (cpf.length != 11) return false
    if (cpf.all { it == cpf[0] }) return false

    fun checkDigit(start: Int): Int {
        var sum = 0
        var weight = start
        for (i in 0 until (start - 1)) {
            sum += (cpf[i] - '0') * weight
            weight--
        }
        val rest = (sum * 10) % 11
        return if (rest == 10) 0 else rest
    }

    val d1 = checkDigit(10)
    val d2 = checkDigit(11)
    return d1 == (cpf[9] - '0') && d2 == (cpf[10] - '0')
}

private fun isValidBirthDate(digits: String): Boolean {
    if (digits.length != 8) return false
    val day = digits.substring(0, 2).toIntOrNull() ?: return false
    val month = digits.substring(2, 4).toIntOrNull() ?: return false
    val year = digits.substring(4, 8).toIntOrNull() ?: return false
    if (month !in 1..12) return false
    if (year !in 1900..2026) return false
    if (day !in 1..31) return false
    val maxDay = when (month) {
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    return day <= maxDay
}
