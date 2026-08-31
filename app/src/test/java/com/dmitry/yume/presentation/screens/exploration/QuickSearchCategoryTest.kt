package com.dmitry.yume.presentation.screens.exploration

import org.junit.Assert.assertEquals
import org.junit.Test

class QuickSearchCategoryTest {

    @Test fun `type categories use updated descending sorting`() {
        val category = QuickSearchCategory.Tv

        assertEquals(listOf("tv"), category.options.kinds)
        assertEquals("updated", category.options.sort)
        assertEquals("desc", category.options.order)
    }

    @Test fun `announcements filter announced titles by updates`() {
        val category = QuickSearchCategory.Announcements

        assertEquals(listOf("anons"), category.options.statuses)
        assertEquals("updated", category.options.sort)
        assertEquals("desc", category.options.order)
    }

    @Test fun `recent releases filter released titles by release year`() {
        val category = QuickSearchCategory.RecentReleases

        assertEquals(listOf("released"), category.options.statuses)
        assertEquals("year", category.options.sort)
        assertEquals("desc", category.options.order)
    }
}
