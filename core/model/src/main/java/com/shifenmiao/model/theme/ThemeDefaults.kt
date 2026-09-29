package com.shifenmiao.model.theme

/**
 * 主题外观的共享默认值。
 *
 * 放在 core:model 是因为 :core:storage、:core:settings、:core:database、:core:ui 都依赖它，
 * 而这些模块各自持有同一份默认值（DataStore、主题预设、冷启动快照、Room 建表/迁移），
 * 单一常量可以避免"改了一处、漏了另一处"导致新装与升级表现不一致。
 */
object ThemeDefaults {

    /**
     * 玻璃描边可见度默认值 (0f..1f)，0 隐藏描边，1 为完整强度。
     *
     * 提到 20% 是"看得见边界、又不喧宾夺主"的下限: 17% 时描边基本糊进底色,
     * 半透明玻璃层读不出边界, 整页就发闷。
     *
     * 这是**唯一**的默认值来源: SettingsState / AppThemePreset / DataStore 兜底 /
     * MMKV 冷启动快照 / 主题设置页草稿都读它。
     * 注意: ThemePresetEntity 的 @ColumnInfo(defaultValue = "0.17") 是建表语句里的
     * 字面量, 有意保持不动 —— 改建表语句会改 Room schema/identityHash 从而整库重建,
     * 而它运行时根本不会被读到, 所以两处不需要同步。
     */
    const val DEFAULT_GLASS_BORDER_ALPHA = 0.20f
}
