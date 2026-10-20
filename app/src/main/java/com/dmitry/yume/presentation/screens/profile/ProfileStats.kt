package com.dmitry.yume.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dmitry.yume.domain.models.ProfileStatistics
import com.dmitry.yume.presentation.components.SkeletonBlock
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import com.dmitry.yume.presentation.ui.theme.YumeType
import java.text.NumberFormat

private data class StatusSlice(val title: String, val count: Int, val color: Color)
private data class Metric(val title: String, val value: String)

@Composable
fun ProfileStats(
    statistics: ProfileStatistics?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = YumeTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Статистика", style = YumeType.h2, color = colors.textPrimary)
        if (isLoading && statistics == null) {
            ProfileStatsSkeleton()
        } else if (statistics != null) {
            LibraryCard(statistics)
            WatchTimeCard(statistics)
        }
    }
}

@Composable
private fun ProfileStatsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = "Загрузка статистики" },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SkeletonStatsSurface {
            SkeletonBlock(Modifier.width(112.dp).height(22.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SkeletonBlock(Modifier.size(132.dp), cornerRadius = 66.dp)
            }
            SkeletonGrid2x2()
        }
        SkeletonStatsSurface {
            SkeletonBlock(Modifier.width(150.dp).height(18.dp), subdued = true)
            SkeletonBlock(Modifier.width(200.dp).height(32.dp))
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(3) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(18.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(0.8f).height(11.dp), subdued = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonGrid2x2() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(2) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SkeletonBlock(Modifier.size(8.dp), cornerRadius = 4.dp)
                        SkeletonBlock(Modifier.weight(1f).height(14.dp), subdued = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonStatsSurface(content: @Composable ColumnScope.() -> Unit) {
    val colors = YumeTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceCard.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, colors.lineStrong),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
private fun LibraryCard(statistics: ProfileStatistics) {
    val colors = YumeTheme.colors
    val library = statistics.library
    val slices = listOf(
        StatusSlice("В планах", library.planned, colors.statusPlanned),
        StatusSlice("Просмотрено", library.completed, colors.statusCompleted),
        StatusSlice("Смотрю", library.watching, colors.statusWatching),
        StatusSlice("Брошено", library.dropped, colors.statusDropped),
    )

    StatsSurface {
        Text("Мои списки", style = YumeType.h3, color = colors.textPrimary)
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            StaticDonut(slices, library.total, Modifier.size(132.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            slices.chunked(2).forEach { rowSlices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowSlices.forEach { StatusRow(it, Modifier.weight(1f)) }
                    if (rowSlices.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun WatchTimeCard(statistics: ProfileStatistics) {
    val colors = YumeTheme.colors
    val actual = statistics.actual
    StatsSurface {
        Text("Время просмотра", style = YumeType.sm, color = colors.textMuted)
        Text(formatWatchTime(actual.watchedMinutesEstimate), style = YumeType.h1, color = colors.textPrimary)
        Divider()
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCell(Metric("эпизодов", formatNumber(actual.episodesCompleted)), Modifier.weight(1f))
            MetricCell(
                Metric("средняя оценка", statistics.averageRating?.let(::formatDecimal) ?: "—"),
                Modifier.weight(1f),
            )
            MetricCell(Metric("в избранном", formatNumber(statistics.favorites)), Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatsSurface(content: @Composable ColumnScope.() -> Unit) {
    val colors = YumeTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceCard,
        border = BorderStroke(1.dp, colors.lineStrong),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
private fun StaticDonut(slices: List<StatusSlice>, total: Int, modifier: Modifier = Modifier) {
    val colors = YumeTheme.colors
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val stroke = Stroke(width = 16.dp.toPx())
            drawArc(colors.lineStrong, -90f, 360f, false, style = stroke)
            if (total > 0) {
                var startAngle = -90f
                slices.forEach { slice ->
                    val sweep = 360f * slice.count / total.toFloat()
                    if (sweep > 0f) drawArc(slice.color, startAngle, sweep, false, style = stroke)
                    startAngle += sweep
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(formatNumber(total), style = YumeType.h1, color = colors.textPrimary)
            Text(
                "ТАЙТЛОВ\nВ СПИСКАХ",
                style = YumeType.xs,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StatusRow(slice: StatusSlice, modifier: Modifier = Modifier) {
    val colors = YumeTheme.colors
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(slice.color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(
            slice.title,
            style = YumeType.xs,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(formatNumber(slice.count), style = YumeType.sm, color = colors.textPrimary)
    }
}

@Composable
private fun MetricCell(metric: Metric, modifier: Modifier = Modifier) {
    val colors = YumeTheme.colors
    Column(modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(metric.value, style = YumeType.h3, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(metric.title, style = YumeType.xs, color = colors.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(YumeTheme.colors.line))
}

private fun formatNumber(value: Int) = NumberFormat.getIntegerInstance().format(value)
private fun formatDecimal(value: Double) =
    NumberFormat.getNumberInstance().apply { maximumFractionDigits = 1 }.format(value)

private fun formatWatchTime(minutes: Double): String {
    val roundedMinutes = minutes.toLong().coerceAtLeast(0)
    val hours = roundedMinutes / 60
    val remainder = roundedMinutes % 60
    return when {
        hours > 0 && remainder > 0 -> "$hours ч $remainder мин"
        hours > 0 -> "$hours ч"
        else -> "$remainder мин"
    }
}
