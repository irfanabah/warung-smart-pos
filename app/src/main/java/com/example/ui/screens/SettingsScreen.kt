package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.WarungViewModel

@Composable
fun SettingsScreen(
    viewModel: WarungViewModel,
    modifier: Modifier = Modifier
) {
    val storeName by viewModel.storeName.collectAsState()
    val cashierName by viewModel.cashierName.collectAsState()
    val storeAddress by viewModel.storeAddress.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val storeEmoji by viewModel.storeEmoji.collectAsState()

    var nameInput by remember(storeName) { mutableStateOf(storeName) }
    var cashierInput by remember(cashierName) { mutableStateOf(cashierName) }
    var addressInput by remember(storeAddress) { mutableStateOf(storeAddress) }

    val emojis = listOf("🔥", "⭐", "☕", "🍜", "🐱", "🚀", "🎯", "🍀")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "⚙️ PENGATURAN & KUSTOMISASI",
            color = Slate400,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        // Store Profile Form
        Surface(
            color = Slate950,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("👤 Profil Toko & Kasir", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Column {
                    Text("Nama Warung / Toko", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("store_name_input")
                    )
                }

                Column {
                    Text("Nama Kasir Bertugas", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = cashierInput,
                        onValueChange = { cashierInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text("Alamat Toko (Muncul di Struk)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = { viewModel.saveStoreSettings(nameInput, cashierInput, addressInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = selectedTheme.primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_profile_btn")
                ) {
                    Text("Simpan Profil Toko", color = Slate950, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }

        // Theme Presets
        Surface(
            color = Slate950,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🎨 Tema Warna Aplikasi", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    AppThemePreset.entries.forEach { preset ->
                        val isSelected = selectedTheme == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(preset.primaryColor)
                                .border(2.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setThemePreset(preset) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(preset.emoji, fontSize = 16.sp)
                                Text(
                                    preset.title.split(" ").last(),
                                    color = if (preset == AppThemePreset.INDIGO) Color.White else Slate950,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Emoji Ikon Toko
        Surface(
            color = Slate950,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("✨ Ikon Toko / Emoji Logo", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    emojis.take(4).forEach { emoji ->
                        val isSelected = storeEmoji == emoji
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Slate900)
                                .border(2.dp, if (isSelected) selectedTheme.primaryColor else Slate800, RoundedCornerShape(14.dp))
                                .clickable { viewModel.setStoreEmoji(emoji) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    emojis.drop(4).forEach { emoji ->
                        val isSelected = storeEmoji == emoji
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Slate900)
                                .border(2.dp, if (isSelected) selectedTheme.primaryColor else Slate800, RoundedCornerShape(14.dp))
                                .clickable { viewModel.setStoreEmoji(emoji) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}
