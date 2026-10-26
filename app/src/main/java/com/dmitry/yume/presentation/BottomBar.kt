package com.dmitry.yume.presentation

import android.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dmitry.yume.presentation.navigation.TopLevelDestination
import com.dmitry.yume.presentation.navigation.navigateToTab
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType

@Composable
fun BottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val colors = YumeTheme.colors

    NavigationBar(
        containerColor = colors.surfaceCard.copy(alpha = 0.92f),
        tonalElevation = 0.dp
    ) {
        TopLevelDestination.entries.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.graph } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected) {
                        navController.popBackStack(item.start, inclusive = false)
                    } else {
                        navController.navigateToTab(item.graph)
                    } },
                icon = { Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = stringResource(item.labelRes),
                    modifier = Modifier.size(20.dp),
                ) },
                label = { Text(stringResource(item.labelRes), style = YumeType.xs)},
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.accent,
                    selectedTextColor = colors.accent,
                    unselectedIconColor = colors.textMuted,
                    unselectedTextColor = colors.textMuted,
                    indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                )
            )
        }
    }
}
