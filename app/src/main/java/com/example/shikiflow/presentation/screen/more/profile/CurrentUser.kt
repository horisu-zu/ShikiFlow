package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType

@Composable
fun CurrentUser(
    userData: User?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseImage(
            model = userData?.avatarUrl,
            contentDescription = "Avatar",
            imageType = ImageType.Square(),
            modifier = Modifier.size(96.dp)
        )
        Text(
            text = userData?.nickname ?: stringResource(R.string.profile_screen_missing_nickname),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}