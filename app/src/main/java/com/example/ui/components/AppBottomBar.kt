package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalAppStrings

enum class AppDestination {
    HOME,
    TASKS,
    NOTES,
    EXPENSES,
    SETTINGS,
    CALENDAR
}

@Composable
fun AppBottomBar(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit
) {
    val strings = LocalAppStrings.current

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val navItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        NavigationBarItem(
            selected = currentDestination == AppDestination.HOME,
            onClick = { onNavigate(AppDestination.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == AppDestination.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = strings.navHome,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navHome,
                    fontSize = 10.5.sp,
                    fontWeight = if (currentDestination == AppDestination.HOME) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = currentDestination == AppDestination.TASKS,
            onClick = { onNavigate(AppDestination.TASKS) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == AppDestination.TASKS) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = strings.navTasks,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navTasks,
                    fontSize = 10.5.sp,
                    fontWeight = if (currentDestination == AppDestination.TASKS) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_tasks")
        )

        NavigationBarItem(
            selected = currentDestination == AppDestination.NOTES,
            onClick = { onNavigate(AppDestination.NOTES) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == AppDestination.NOTES) Icons.Filled.Description else Icons.Outlined.Description,
                    contentDescription = strings.navNotes,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navNotes,
                    fontSize = 10.5.sp,
                    fontWeight = if (currentDestination == AppDestination.NOTES) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_notes")
        )

        NavigationBarItem(
            selected = currentDestination == AppDestination.EXPENSES,
            onClick = { onNavigate(AppDestination.EXPENSES) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == AppDestination.EXPENSES) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                    contentDescription = strings.navExpenses,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navExpenses,
                    fontSize = 10.5.sp,
                    fontWeight = if (currentDestination == AppDestination.EXPENSES) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_expenses")
        )

        NavigationBarItem(
            selected = currentDestination == AppDestination.SETTINGS,
            onClick = { onNavigate(AppDestination.SETTINGS) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == AppDestination.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = strings.navSettings,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navSettings,
                    fontSize = 10.5.sp,
                    fontWeight = if (currentDestination == AppDestination.SETTINGS) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_settings")
        )
    }
}
