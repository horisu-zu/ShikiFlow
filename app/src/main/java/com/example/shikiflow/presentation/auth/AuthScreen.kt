package com.example.shikiflow.presentation.auth

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.presentation.common.Button
import com.example.shikiflow.presentation.common.FeatureItem
import com.example.shikiflow.presentation.common.FeaturesGroup
import com.example.shikiflow.presentation.common.mappers.AuthTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.AuthTypeMapper.iconResource
import com.example.shikiflow.presentation.viewmodel.auth.AuthViewModel
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.WebIntent.openActionView

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuthType.entries.forEach { authType ->
                Button(
                    icon = authType.iconResource(),
                    label = buildString {
                        append(stringResource(R.string.auth_sign_in))
                        append(" ")
                        append(stringResource(id = authType.displayValue()))
                    },
                    onClick = {
                        val authUrl = authViewModel.getAuthorizationUrl(authType)

                        context.openActionView(authUrl)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}