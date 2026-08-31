package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab

@Composable
fun BottomNavBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Slate950,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate850),
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                NavTabItem(MainTab.DASHBOARD, "Home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
                NavTabItem(MainTab.POS, "Kasir", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
                NavTabItem(MainTab.ATM, "Mini ATM", Icons.Filled.CreditCard, Icons.Outlined.CreditCard),
                NavTabItem(MainTab.KASBON, "Kasbon", Icons.Filled.Book, Icons.Outlined.Book),
                NavTabItem(MainTab.INVENTORY, "Stok", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
                NavTabItem(MainTab.REPORTS, "Laporan", Icons.Filled.Assessment, Icons.Outlined.Assessment),
                NavTabItem(MainTab.SETTINGS, "Setelan", Icons.Filled.Settings, Icons.Outlined.Settings)
            )

            tabs.forEach { item ->
                val isSelected = currentTab == item.tab
                val targetColor = if (isSelected) themeColor else Slate400

                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) themeColor.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onTabSelected(item.tab) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("nav_btn_${item.tab.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = targetColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            color = targetColor,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private data class NavTabItem(
    val tab: MainTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
