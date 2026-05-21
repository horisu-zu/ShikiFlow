package com.example.shikiflow.presentation.screen.main.details.staff

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.presentation.screen.main.details.MediaRolesType
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.presentation.screen.main.details.RoleType
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.staff.StaffAttributes
import com.example.shikiflow.domain.model.staff.StaffName
import com.example.shikiflow.domain.model.staff.StaffName.Companion.preferred
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.ToggleFavoriteButton
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.character.CharacterMediaSection
import com.example.shikiflow.presentation.screen.main.details.character.PaginatedListNavigateIcon
import com.example.shikiflow.presentation.screen.main.details.common.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentSection
import com.example.shikiflow.presentation.viewmodel.staff.staff_details.StaffViewModel
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.DateMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.GenderMapper.displayValue
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.utils.Converter.format
import com.example.shikiflow.utils.Converter.isHTMLStringBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(
    staffId: Int,
    navOptions: MediaNavOptions,
    staffViewModel: StaffViewModel = hiltViewModel()
) {
    val staffUiState by staffViewModel.uiState.collectAsStateWithLifecycle()

    val titleType = LocalTitleTypeController.current
    val lazyGridState = rememberLazyGridState()
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
            lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }
    val scrolledFirst by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex != 0
        }
    }

    LaunchedEffect(staffId) {
        staffViewModel.setStaffId(staffId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = if(!scrolledFirst) {
                                stringResource(R.string.staff_title)
                            } else staffUiState.staffDetails?.fullName?.preferred(titleType)
                                ?: stringResource(R.string.staff_title),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navOptions.navigateBack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Main"
                            )
                        }
                    },
                    actions = {
                        staffUiState.staffDetails?.let { details ->
                            if(details.favorites != null && details.isFavorite != null) {
                                ToggleFavoriteButton(
                                    favoritesCount = details.favorites,
                                    isFavorite = details.isFavorite,
                                    onToggle = { staffViewModel.toggleFavorite(details.id) }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surfaceContainer
                    )
                )
                if(!isAtTop) { HorizontalDivider() }
            }
        }
    ) { paddingValues ->
        if(staffUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(staffUiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = staffUiState.errorMessage ?: stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { staffViewModel.onRefresh() }
                )
            }
        } else {
            staffUiState.staffDetails?.let { details ->
                val horizontalPadding = 12.dp

                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Adaptive(300.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding()),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 8.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        StaffTitleSection(
                            avatarUrl = details.imageUrl,
                            staffName = details.fullName,
                            preferredType = titleType,
                            staffRoles = details.shortRoles
                        )
                    }
                    details.attributes?.let { attributes ->
                        StaffAttributes(attributes)
                    }
                    if(!details.description.isHTMLStringBlank()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ExpandableText(
                                htmlText = details.description ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                collapsedMaxLines = 4,
                                onEntityClick = { entityType, id ->
                                    navOptions.navigateByEntity(entityType, id)
                                }
                            )
                        }
                    }
                    if(details.staffCharacterRoles.entries.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            VoiceActorRolesSection(
                                characterRoles = details.staffCharacterRoles,
                                titleType = titleType,
                                horizontalPadding = horizontalPadding,
                                onCharacterClick = { characterId ->
                                    navOptions.navigateToCharacterDetails(characterId)
                                },
                                onPaginatedNavigate = {
                                    navOptions.navigateToMediaRoles(
                                        id = staffId,
                                        mediaRolesType = MediaRolesType.STAFF,
                                        roleTypes = buildList {
                                            add(RoleType.VA)
                                            if(details.staffAnimeRoles.entries.isNotEmpty()) {
                                                add(RoleType.ANIME)
                                            }
                                            if(details.staffMangaRoles.entries.isNotEmpty()) {
                                                add(RoleType.MANGA)
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    }
                    if(details.staffAnimeRoles.entries.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CharacterMediaSection(
                                sectionTitle = stringResource(R.string.media_type_anime),
                                items = details.staffAnimeRoles,
                                onItemClick = { id ->
                                    navOptions.navigateToAnimeDetails(id)
                                },
                                onPaginatedNavigate = {
                                    navOptions.navigateToMediaRoles(
                                        id = staffId,
                                        mediaRolesType = MediaRolesType.STAFF,
                                        roleTypes = buildList {
                                            add(RoleType.ANIME)
                                            if(details.staffCharacterRoles.entries.isNotEmpty()) {
                                                add(RoleType.VA)
                                            }
                                            if(details.staffMangaRoles.entries.isNotEmpty()) {
                                                add(RoleType.MANGA)
                                            }
                                        }
                                    )
                                },
                                horizontalPadding = horizontalPadding
                            )
                        }
                    }
                    if(details.staffMangaRoles.entries.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CharacterMediaSection(
                                sectionTitle = stringResource(R.string.media_type_manga),
                                items = details.staffMangaRoles,
                                onItemClick = { id ->
                                    navOptions.navigateToMangaDetails(id)
                                },
                                onPaginatedNavigate = {
                                    navOptions.navigateToMediaRoles(
                                        id = staffId,
                                        mediaRolesType = MediaRolesType.STAFF,
                                        roleTypes = buildList {
                                            add(RoleType.MANGA)
                                            if(details.staffCharacterRoles.entries.isNotEmpty()) {
                                                add(RoleType.VA)
                                            }
                                            if(details.staffAnimeRoles.entries.isNotEmpty()) {
                                                add(RoleType.ANIME)
                                            }
                                        }
                                    )
                                },
                                horizontalPadding = horizontalPadding
                            )
                        }
                    }
                    details.topicId?.let { topicId ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CommentSection(
                                topicId = topicId,
                                onEntityClick = { entityType, id ->
                                    navOptions.navigateByEntity(entityType, id)
                                },
                                onTopicNavigate = { topicId ->
                                    navOptions.navigateToComments(
                                        screenMode = CommentsScreenMode.TOPIC,
                                        id = topicId
                                    )
                                },
                                onUserClick = { user ->
                                    navOptions.navigateToUserProfile(user)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StaffTitleSection(
    avatarUrl: String?,
    staffName: StaffName,
    preferredType: PreferredTitleType,
    staffRoles: Map<String, Int?>,
    modifier: Modifier = Modifier
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    val imageType = ImageType.Poster()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseImage(
            model = avatarUrl,
            imageType = imageType
        )

        Spacer(Modifier.width(16.dp))

        AnimatedVisibility(
            visible = !isExpanded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = staffName.preferred(preferredType),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if(preferredType != PreferredTitleType.NATIVE) {
                    staffName.native?.let { nativeName ->
                        Text(
                            text = nativeName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        if(staffRoles.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 12.dp,
                            topEnd = if (isExpanded) 0.dp else 4.dp,
                            bottomEnd = if (isExpanded) 0.dp else 4.dp,
                        )
                    )
                    .clickable(
                        interactionSource = mutableInteractionSource,
                        indication = null,
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Expand Button",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .rotate(rotationState)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium
                )
            ),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium
                )
            ),
            modifier = Modifier
                .heightIn(max = imageType.width / imageType.aspectRatio)
                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                staffRoles.forEach { role ->
                    Text(
                        text = buildString {
                            append(role.key)
                            role.value?.let { rolesCount ->
                                append(": $rolesCount")
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VoiceActorRolesSection(
    characterRoles: PaginatedList<MediaPersonShort>,
    titleType: PreferredTitleType,
    horizontalPadding: Dp,
    onCharacterClick: (Int) -> Unit,
    onPaginatedNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardWidth = 96.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.staff_va_roles_label),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = { onPaginatedNavigate() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Navigate to Page"
                )
            }
        }
        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(characterRoles.entries) { character ->
                CharacterCard(
                    characterPoster = character.imageUrl,
                    characterName = character.fullName.preferred(titleType),
                    onClick = {
                        onCharacterClick(character.id)
                    },
                    modifier = Modifier.width(cardWidth)
                )
            }
            if(characterRoles.hasNextPage) {
                item {
                    PaginatedListNavigateIcon(
                        onNavigate = { onPaginatedNavigate() },
                        modifier = Modifier
                            .width(cardWidth)
                            .aspectRatio(2f / 2.85f)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }
}

fun LazyGridScope.StaffAttributes(
    staffAttributes: StaffAttributes
) {
    staffAttributes.dateOfBirth?.let { date ->
        date.format()?.let { value ->
            item {
                StaffAttributesItem(
                    label = stringResource(R.string.staff_attribute_date_of_birth),
                    value = value
                )
            }
        }
    }
    staffAttributes.dateOfDeath?.let { date ->
        date.format()?.let { value ->
            item {
                StaffAttributesItem(
                    label = stringResource(R.string.staff_attribute_date_of_death),
                    value = value
                )
            }
        }
    }
    staffAttributes.age?.let { age ->
        item {
            StaffAttributesItem(
                label = stringResource(R.string.staff_attribute_age),
                value = age
            )
        }
    }
    staffAttributes.gender?.let { gender ->
        item {
            StaffAttributesItem(
                label = stringResource(R.string.staff_attribute_gender),
                value = stringResource(gender.displayValue())
            )
        }
    }
    staffAttributes.yearsActive?.let { yearsActive ->
        item {
            StaffAttributesItem(
                label = stringResource(R.string.staff_attribute_years_active),
                value = yearsActive.displayValue()
            )
        }
    }
    staffAttributes.hometown?.let { hometown ->
        item {
            StaffAttributesItem(
                label = stringResource(R.string.staff_attribute_hometown),
                value = hometown
            )
        }
    }
}

@Composable
private fun StaffAttributesItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}