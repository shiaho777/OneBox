package com.shifenmiao.common.manager

/**
 * 新增自定义引擎的结果。
 *
 * 之所以不直接用 Boolean: 失败原因决定用户能不能自己解决 ——
 * "名称重复"要提示换名字, "写库异常"才该说稍后重试。混成一句
 * "添加失败, 请稍后重试"会让用户在一个永远重试不成功的操作上循环。
 */
sealed interface AddEngineResult {

    data object Success : AddEngineResult

    /** 服务标识已被内置预设或另一条自定义引擎占用, 需要换个名字 */
    data object NameTaken : AddEngineResult

    /** 写库成功但回读不到"用户自有"的行, 已回滚 —— 理论上不该出现, 出现即是 bug */
    data object NotVisible : AddEngineResult

    /** 其他异常(数据库、参数等) */
    data object Failed : AddEngineResult
}
