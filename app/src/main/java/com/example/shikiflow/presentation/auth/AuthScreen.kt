package com.example.shikiflow.presentation.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.presentation.common.Button
import com.example.shikiflow.presentation.common.FeatureItem
import com.example.shikiflow.presentation.common.FeaturesGroup
import com.example.shikiflow.utils.IconResource
import kotlinx.coroutines.launch
import com.example.shikiflow.utils.WebIntent

@Composable
fun AuthScreen(
    onAuth: (AuthType) -> String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        FeaturesGroup(
            modifier = Modifier.padding(horizontal = 24.dp),
            items = listOf(
                {
                    FeatureItem(
                        icon = IconResource.Drawable(resId = R.drawable.shiki_logo),
                        title = stringResource(R.string.auth_features_shiki_integration_label),
                        subtitle = stringResource(R.string.auth_features_shiki_integration_desc)
                    )
                },
                {
                    FeatureItem(
                        icon = IconResource.Drawable(resId = R.drawable.ic_manga),
                        title = stringResource(R.string.auth_features_mangadex_integration_label),
                        subtitle = stringResource(R.string.auth_features_mangadex_integration_desc)
                    )
                },
                {
                    FeatureItem(
                        icon = IconResource.Drawable(resId = R.drawable.ic_stars),
                        title = stringResource(R.string.auth_features_customization_label),
                        subtitle = stringResource(R.string.auth_features_customization_desc)
                    )
                }
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuthType.entries.forEach { authType ->
                Button(
                    icon = when(authType) {
                        AuthType.SHIKIMORI -> IconResource.Drawable(R.drawable.shiki_logo)
                        AuthType.ANILIST -> IconResource.Drawable(R.drawable.anilist_logo)
                    },
                    label = stringResource(
                        id = when(authType) {
                            AuthType.SHIKIMORI -> R.string.auth_shiki_sign_in
                            AuthType.ANILIST -> R.string.auth_anilist_sign_in
                        }
                    ),
                    onClick = {
                        scope.launch {
                            val authUrl = onAuth(authType)
                            Log.d("AuthScreen", "Launching Custom Tab with URL: $authUrl")
                            WebIntent.openUrlCustomTab(context, authUrl)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}