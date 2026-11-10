package com.vinaykr.netgrip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinaykr.netgrip.ui.screens.*
import com.vinaykr.netgrip.ui.theme.*
import com.vinaykr.netgrip.ui.viewmodel.RouterViewModel

enum class NavigationTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    CLIENTS("Clients", Icons.Default.Devices),
    WIFI("Wi-Fi 6", Icons.Default.Wifi),
    SERVICES("Services", Icons.Default.Layers),
    DIAGNOSTICS("Diagnostics", Icons.Default.Terminal)
}

class MainActivity : ComponentActivity() {
    private val viewModel: RouterViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetGripTheme {
                val uiState by viewModel.uiState.collectAsState()
                var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

                if (!uiState.isLoggedIn) {
                    LoginScreen(viewModel = viewModel)
                } else {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            text = "NETGRIP",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            letterSpacing = 1.sp,
                                            color = CyanAccent
                                        )
                                        Text(
                                            text = "Jidu AX6000 · ${uiState.routerUrl}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { viewModel.refreshAll() }) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Refresh",
                                            tint = TextPrimary
                                        )
                                    }
                                    IconButton(onClick = { viewModel.logout() }) {
                                        Icon(
                                            imageVector = Icons.Default.ExitToApp,
                                            contentDescription = "Logout",
                                            tint = RoseError
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = NetGripDarkBg,
                                    titleContentColor = TextPrimary
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = NetGripCardBg,
                                tonalElevation = 0.dp
                            ) {
                                NavigationTab.values().forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentTab = tab },
                                        icon = {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.label,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = NetGripDarkBg,
                                            selectedTextColor = CyanAccent,
                                            indicatorColor = CyanAccent,
                                            unselectedIconColor = TextSecondary,
                                            unselectedTextColor = TextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(NetGripDarkBg)
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                NavigationTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                                NavigationTab.CLIENTS -> ClientsScreen(viewModel = viewModel)
                                NavigationTab.WIFI -> WifiScreen(viewModel = viewModel)
                                NavigationTab.SERVICES -> ServicesScreen(viewModel = viewModel)
                                NavigationTab.DIAGNOSTICS -> DiagnosticsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
