package com.example.shikiflow.presentation.screen.more.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.AuthTypeMapper.colors
import com.example.shikiflow.presentation.common.mappers.AuthTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.AuthTypeMapper.iconResource
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon
import com.materialkolor.ktx.harmonize

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<SectionItem>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        val itemModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)

        Text(
            text = title,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 4.dp
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        items.forEach { item ->
            when(item) {
                is SectionItem.Default -> {
                    AnimatedVisibility(item.isVisible) {
                        TextItem(
                            title = item.title,
                            subtitle = item.displayValue,
                            modifier = Modifier
                                .clickable { item.onClick() }
                                .then(itemModifier)
                        )
                    }
                }
                is SectionItem.User -> {
                    UserItem(
                        title = item.title,
                        displayValue = item.displayValue,
                        authType = item.authType,
                        imageUrl = item.imageUrl,
                        modifier = Modifier
                            .clickable { item.onClick() }
                            .then(itemModifier)
                    )
                }
                is SectionItem.Switch -> {
                    AnimatedVisibility(item.isVisible) {
                        SwitchItem(
                            title = item.title,
                            displayValue = item.displayValue,
                            isChecked = item.isChecked,
                            onClick = item.onClick,
                            modifier = Modifier
                                .clickable { item.onClick() }
                                .then(itemModifier)
                        )
                    }
                }
                is SectionItem.Mode -> {
                    AnimatedVisibility(item.isVisible) {
                        ModeItem(
                            title = item.title,
                            entries = item.entries,
                            iconResources = item.iconResources,
                            weights = item.weights,
                            mode = item.mode,
                            onClick = item.onClick,
                            modifier = itemModifier
                        )
                    }
                }
                is SectionItem.TrackerServices -> {
                    ServicesItem(
                        title = item.title,
                        currentAuthType = item.currentAuthType,
                        serviceUpdateState = item.serviceUpdateState,
                        connectedServicesMap = item.connectedServicesMap,
                        onServiceClick = item.onServiceClick,
                        onServiceUpdateToggle = item.onServiceUpdateToggle,
                        modifier = itemModifier
                    )
                }
            }
        }
    }
}

@Composable
private fun TextItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            modifier = Modifier,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun UserItem(
    title: String,
    displayValue: String,
    authType: AuthType?,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseImage(
            model = imageUrl,
            imageType = ImageType.Square(
                width = 40.dp,
                clip = RoundedCornerShape(percent = 24)
            )
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        authType?.let {
            val colors = authType.colors()

            authType.iconResource().toIcon(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(percent = 24))
                    .background(colors.first.harmonize(MaterialTheme.colorScheme.onBackground))
                    .padding(4.dp),
                tint = colors.second.harmonize(MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}

@Composable
private fun SwitchItem(
    title: String,
    displayValue: String,
    isChecked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { onClick() }
        )
    }
}

@Composable
private fun <T> ModeItem(
    title: String,
    entries: List<T>,
    mode: T,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    iconResources: List<IconResource> = emptyList(),
    weights: List<Float> = emptyList()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            entries.forEachIndexed { index, entry ->
                val entryWeight = weights.getOrNull(index) ?: 1f

                ModeRowItem(
                    entry = entry.toString(),
                    isCurrentMode = mode == entry,
                    onClick = { onClick(entry) },
                    modifier = Modifier.weight(entryWeight),
                    iconResource = iconResources.getOrNull(entries.indexOf(entry))
                )
            }
        }
    }
}

@Composable
private fun ModeRowItem(
    entry: String,
    isCurrentMode: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    iconResource: IconResource? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(entry) }
            .background(
                color = if (isCurrentMode) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.background
            )
            .then(
                other = if (!isCurrentMode) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        iconResource?.let {
            iconResource.toIcon(
                modifier = Modifier.size(24.dp),
                tint = if(isCurrentMode) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = entry,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if(isCurrentMode) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ServicesItem(
    title: String,
    currentAuthType: AuthType?,
    serviceUpdateState: Boolean,
    connectedServicesMap: Map<AuthType, User>,
    onServiceClick: (AuthType) -> Unit,
    onServiceUpdateToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val servicesMap = remember(connectedServicesMap) {
        connectedServicesMap.filter { it.key != currentAuthType }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        AuthType.entries
            .filter { it != currentAuthType }
            .forEach { authType ->
                TrackerServiceItem(
                    authType = authType,
                    user = servicesMap[authType],
                    onServiceClick = onServiceClick
                )
            }

        AnimatedVisibility(
            visible = servicesMap.isNotEmpty(),
            enter = fadeIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        ) {
            TrackerSwitch(
                serviceUpdateState = serviceUpdateState,
                onServiceUpdateToggle = onServiceUpdateToggle
            )
        }
    }
}

@Composable
private fun TrackerSwitch(
    serviceUpdateState: Boolean,
    onServiceUpdateToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(R.string.tracker_services_update_title),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(stringResource(R.string.common_note))
                    }
                    append(": ")
                    append(stringResource(R.string.tracker_services_update_note))
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
        Switch(
            checked = serviceUpdateState,
            onCheckedChange = { onServiceUpdateToggle() }
        )
    }
}

@Composable
private fun TrackerServiceItem(
    authType: AuthType,
    user: User?,
    onServiceClick: (AuthType) -> Unit,
    modifier: Modifier = Modifier
) {
    val rowHeight = 36.dp
    val rowShape = RoundedCornerShape(percent = 24)
    val colors = authType.colors()

    Row(
        modifier = modifier
            .height(rowHeight)
            .clip(rowShape)
            .clickable { onServiceClick(authType) },
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        authType.iconResource().toIcon(
            modifier = Modifier
                .size(rowHeight)
                .clip(rowShape)
                .background(colors.first.harmonize(MaterialTheme.colorScheme.onBackground))
                .padding(4.dp),
            tint = colors.second.harmonize(MaterialTheme.colorScheme.onBackground)
        )

        Text(
            text = stringResource(authType.displayValue()),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )

        user?.let {
            TrackerUserItem(user)
        }
    }
}

@Composable
private fun TrackerUserItem(
    user: User,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.nickname,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        BaseImage(
            model = user.avatarUrl,
            imageType = ImageType.Square(
                width = 24.dp,
                clip = RoundedCornerShape(percent = 24)
            )
        )
    }
}
