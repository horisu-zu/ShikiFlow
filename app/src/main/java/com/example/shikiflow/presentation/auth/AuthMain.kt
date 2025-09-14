package com.example.shikiflow.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.Button
import com.example.shikiflow.presentation.common.FeatureItem
import com.example.shikiflow.presentation.common.FeaturesGroup
import com.example.shikiflow.utils.IconResource

@Composable
fun AuthMain(onStartAuth: () -> Unit) {
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
                        icon = IconResource.Vector(imageVector = Icons.Default.Favorite),
                        title = stringResource(R.string.auth_features_personalize_label),
                        subtitle = stringResource(R.string.auth_features_personalize_desc)
                    )
                },
                {
                    FeatureItem(
                        icon = IconResource.Drawable(resId = R.drawable.ic_manga),
                        title = stringResource(R.string.auth_features_mangadex_integration_label),
                        subtitle = stringResource(R.string.auth_features_mangadex_integration_desc)
                    )
                }
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Button(
                label = stringResource(R.string.auth_sign_in),
                onClick = onStartAuth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp, vertical = 18.dp)
            )
        }
    }
}