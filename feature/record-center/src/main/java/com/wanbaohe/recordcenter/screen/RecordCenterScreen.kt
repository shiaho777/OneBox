package com.wanbaohe.recordcenter.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.shifenmiao.common.ui.BaseScreen
import com.t8rin.imagetoolbox.core.resources.icons.line.LineAvatarDefault
import com.t8rin.imagetoolbox.core.resources.icons.line.LineNote
import com.t8rin.imagetoolbox.core.resources.icons.line.LineTrendingUp
import com.t8rin.imagetoolbox.core.ui.widget.navigation.BottomNavItem
import com.t8rin.imagetoolbox.core.ui.widget.navigation.BottomNavigationBar
import com.wanbaohe.recordcenter.R
import com.wanbaohe.recordcenter.component.RecordCenterComponent
import com.wanbaohe.recordcenter.component.RecordCenterTab
import com.wanbaohe.recordcenter.screen.tab.MineTab
import com.wanbaohe.recordcenter.screen.tab.RecordsTab
import com.wanbaohe.recordcenter.screen.tab.TrendsTab

/**
 * 记录中心聚合页 — 底部三 tab(记录 / 趋势 / 我的),tab 结构对齐万年历。
 */
@Composable
fun RecordCenterScreen(component: RecordCenterComponent) {
    val currentTab by component.currentTab.collectAsState()
    val latestByType by component.latestByType.collectAsState()
    val trendSummaries by component.trendSummaries.collectAsState()
    val profile by component.profile.collectAsState()

    BaseScreen(
        title = {
            Text(
                text = stringResource(R.string.record_center_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        onGoBack = component.onGoBack,
        showNavigationBarsPadding = false,
        supportGlassEffect = true,
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                            (fadeIn(animationSpec = tween(250)) +
                                slideInHorizontally(animationSpec = tween(300)) { it / 4 * direction })
                                .togetherWith(
                                    fadeOut(animationSpec = tween(200)) +
                                        slideOutHorizontally(animationSpec = tween(300)) { -it / 4 * direction }
                                )
                        },
                        label = "record_center_tab_switch",
                    ) { tab ->
                        when (tab) {
                            RecordCenterTab.RECORDS -> RecordsTab(
                                recordTypes = component.recordTypes,
                                latestByType = latestByType,
                                onTypeClick = component::navigateToRecordList,
                            )
                            RecordCenterTab.TRENDS -> TrendsTab(
                                summaries = trendSummaries,
                                onTypeClick = component::navigateToRecordList,
                            )
                            RecordCenterTab.MINE -> MineTab(
                                profile = profile,
                                onSave = component::saveProfile,
                            )
                        }
                    }
                }
                RecordCenterBottomBar(
                    currentTab = currentTab,
                    onSwitch = component::switchTab,
                )
            }
        },
    )
}

@Composable
private fun RecordCenterBottomBar(
    currentTab: RecordCenterTab,
    onSwitch: (RecordCenterTab) -> Unit,
) {
    val tabs = listOf(
        TabInfo(
            label = stringResource(R.string.record_center_tab_records),
            icon = com.t8rin.imagetoolbox.core.resources.Icons.Outlined.LineNote,
            tab = RecordCenterTab.RECORDS,
        ),
        TabInfo(
            label = stringResource(R.string.record_center_tab_trends),
            icon = com.t8rin.imagetoolbox.core.resources.Icons.Outlined.LineTrendingUp,
            tab = RecordCenterTab.TRENDS,
        ),
        TabInfo(
            label = stringResource(R.string.record_center_tab_mine),
            icon = com.t8rin.imagetoolbox.core.resources.Icons.Outlined.LineAvatarDefault,
            tab = RecordCenterTab.MINE,
        ),
    )

    val items = tabs.mapIndexed { index, tab ->
        BottomNavItem(
            id = index.toString(),
            label = tab.label,
            icon = tab.icon,
            contentDescription = tab.label,
        )
    }

    BottomNavigationBar(
        items = items,
        selectedItemId = currentTab.ordinal.toString(),
        onItemClick = { clicked ->
            val index = clicked.id.toIntOrNull() ?: return@BottomNavigationBar
            tabs.getOrNull(index)?.let { onSwitch(it.tab) }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

private data class TabInfo(
    val label: String,
    val icon: ImageVector,
    val tab: RecordCenterTab,
)
