package com.example.shikiflow.presentation.screen.more.profile

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.Converter
import kotlinx.datetime.toInstant

@Composable
fun CurrentUser(
    userData: CurrentUserQuery.Data?,
    context: Context,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseImage(
            model = userData?.currentUser?.avatarUrl,
            contentDescription = "Avatar",
            imageType = ImageType.Square(),
            modifier = Modifier.size(96.dp)
        )
        Column {
            Text(
                text = userData?.currentUser?.nickname ?: stringResource(R.string.profile_screen_missing_nickname),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = Converter.convertInstantToString(
                    context = context,
                    lastSeenInstant = userData?.currentUser?.lastOnlineAt.toString().toInstant()
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}