package github.daisukikaffuchino.momoqr.logic.model

import com.kyant.m3color.dynamiccolor.ColorSpec

enum class ColorSpecVersion(val id: Int,val label: String) {
    Spec2021(1,"SPEC 2021"),
    Spec2025(2,"SPEC 2025"),
    Spec2026(3,"SPEC 2026");

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: Spec2021
    }
}

fun ColorSpecVersion.toSpecVersion(): ColorSpec.SpecVersion {
    return when (this) {
        ColorSpecVersion.Spec2021 -> ColorSpec.SpecVersion.SPEC_2021
        ColorSpecVersion.Spec2025 -> ColorSpec.SpecVersion.SPEC_2025
        ColorSpecVersion.Spec2026 -> ColorSpec.SpecVersion.SPEC_2026
    }
}