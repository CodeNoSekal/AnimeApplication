package com.dmitry.yume.presentation.screens.catalog.filters.components

sealed class TripleCheckState {
    object Unchecked : TripleCheckState()
    object Checked : TripleCheckState()
    object Rejected : TripleCheckState()
}

sealed class DoubleCheckState {
    object Unchecked : DoubleCheckState()
    object Checked : DoubleCheckState()
}

sealed class CheckType {
    object Double : CheckType()
    object Triple : CheckType()
}