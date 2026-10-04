package com.wanbaohe.profile.settingItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.system.OneBoxDesignSystem

/**
 * 「我的」页设置分组卡片。
 *
 * 背景与页面底部「备份 / 恢复」按钮保持同一实心 surfaceContainer，
 * 不走 GlassCard 玻璃管线，避免卡片与按钮出现两套灰度。
 */
@Composable
fun ProfileGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = OneBoxDesignSystem.sectionCardShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(PaddingValues(horizontal = 0.dp, vertical = 16.dp))
        ) {
            content()
        }
    }
}
