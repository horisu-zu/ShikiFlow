package com.example.shikiflow.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.Button
import com.example.shikiflow.presentation.common.FeatureItem
import com.example.shikiflow.presentation.common.FeaturesGroup
import com.example.shikiflow.utils.IconResource

@Composable
fun AuthMain(onStartAuth: () -> Unit) {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (logoSection, featuresItem, signInButton) = createRefs()

        Text(
            text = "ShikiFlow",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.constrainAs(logoSection) {
                top.linkTo(parent.top)
                bottom.linkTo(featuresItem.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        FeaturesGroup(
            items = listOf(
                {
                    FeatureItem(
                        icon = IconResource.Drawable(resId = R.drawable.shiki_logo),
                        title = context.getString(R.string.auth_features_shiki_integration_label),
                        subtitle = context.getString(R.string.auth_features_shiki_integration_desc)
                    )
                },
                {
                    FeatureItem(
                        icon = IconResource.Vector(imageVector = Icons.Default.Favorite),
                        title = context.getString(R.string.auth_features_personalize_label),
                        subtitle = context.getString(R.string.auth_features_personalize_desc)
                    )
                },
                {
                    FeatureItem(
                        icon = IconResource.Drawable(resId = R.drawable.ic_manga),
                        title = context.getString(R.string.auth_features_mangadex_integration_label),
                        subtitle = context.getString(R.string.auth_features_mangadex_integration_desc)
                    )
                }
            ),
            modifier = Modifier
                .constrainAs(featuresItem) {
                    top.linkTo(logoSection.bottom)
                    start.linkTo(parent.start)
                    bottom.linkTo(signInButton.top)
                }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        )

        Box(
            Modifier
                .constrainAs(signInButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
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