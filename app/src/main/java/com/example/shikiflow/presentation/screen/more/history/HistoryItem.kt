package com.example.shikiflow.presentation.screen.more.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.user.UserHistoryResponse
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.utils.Converter
import kotlinx.datetime.toInstant

@Composable
fun HistoryItem(
    historyItem: UserHistoryResponse?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {
        BaseImage(
            model = "${BuildConfig.BASE_URL}${historyItem?.target?.image?.original}",
            contentDescription = "Poster",
            modifier = Modifier.width(96.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = historyItem?.target?.name ?: "Huh!",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = historyItem?.description?.replace("<b>", "")
                    ?.replace("</b>", "") ?: "Huh!",
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = Converter.formatInstant(historyItem?.created_at?.toInstant()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}
