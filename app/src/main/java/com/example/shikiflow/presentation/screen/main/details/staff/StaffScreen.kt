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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.character.CharacterMediaSection
import com.example.shikiflow.presentation.screen.main.details.character.PaginatedListNavigateIcon
import com.example.shikiflow.presentation.screen.main.details.common.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentSection
import com.example.shikiflow.presentation.viewmodel.person.StaffViewModel
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(
    personId: Int,
    navOptions: MediaNavOptions,
    staffViewModel: StaffViewModel = hiltViewModel()
) {
    val staffDetails by staffViewModel.personDetails.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val density = LocalDensity.current
    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex != 0
        }
    }

    LaunchedEffect(personId) {
        staffViewModel.getPersonDetails(personId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = if(isAtTop) stringResource(R.string.staff_title)
                                else staffDetails.data?.fullName ?: stringResource(R.string.staff_title),
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surface
                    )
                )
                if(!isAtTop) { HorizontalDivider() }
            }
        }
    ) { paddingValues ->
        when(staffDetails) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                staffDetails.data?.let { details ->
                    val horizontalPadding = 12.dp

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize()
                            .padding(
                                top = paddingValues.calculateTopPadding(),
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            ),
                        contentPadding = PaddingValues(
                            horizontal = horizontalPadding,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                    ) {
                        item {
                            StaffTitleSection(
                                avatarUrl = details.imageUrl,
                                name = details.fullName,
                                japaneseName = details.nativeName,
                                staffRoles = details.shortRoles,
                                modifier = Modifier.height(148.dp)
                            )
                        }
                        details.staffCharacterRoles?.let { characterRoles ->
                            item {
                                var roleCardHeight by remember { mutableIntStateOf(0) }
                                val cardWidth = 96.dp

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
                                            characterName = character.fullName,
                                            onClick = { navOptions.navigateToCharacterDetails(
                                                characterId = character.id
                                            ) },
                                            modifier = Modifier.width(cardWidth)
                                                .onSizeChanged { size ->
                                                    roleCardHeight = size.height
                                                }
                                        )
                                    }
                                    if(characterRoles.hasNextPage) {
                                        item {
                                            PaginatedListNavigateIcon(
                                                onNavigate = { /**/ },
                                                modifier = Modifier
                                                    .height(
                                                        height = with(density) {
                                                            roleCardHeight.toDp()
                                                        }
                                                    )
                                                    .width(cardWidth)
                                                    .clip(CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        details.staffAnimeRoles?.let { animeRoles ->
                            item {
                                CharacterMediaSection(
                                    sectionTitle = stringResource(R.string.main_track_mode_anime),
                                    items = animeRoles,
                                    onItemClick = { id ->
                                        navOptions.navigateToAnimeDetails(id)
                                    },
                                    onPaginatedNavigate = { /**/ },
                                    horizontalPadding = horizontalPadding
                                )
                            }
                        }
                        details.staffMangaRoles?.let { mangaRoles ->
                            item {
                                CharacterMediaSection(
                                    sectionTitle = stringResource(R.string.settings_manga_section_title),
                                    items = mangaRoles,
                                    onItemClick = { id ->
                                        navOptions.navigateToMangaDetails(id)
                                    },
                                    onPaginatedNavigate = { /**/ },
                                    horizontalPadding = horizontalPadding
                                )
                            }
                        }
                        details.topicId?.let { topicId ->
                            item {
                                CommentSection(
                                    topicId = topicId,
                                    onEntityClick = { entityType, id ->
                                        navOptions.navigateByEntity(entityType, id)
                                    },
                                    onLinkClick = { WebIntent.openUrlCustomTab(context, it) },
                                    onTopicNavigate = { topicId ->
                                        navOptions.navigateToComments(
                                            screenMode = CommentsScreenMode.TOPIC,
                                            id = topicId
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = staffDetails.message ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { staffViewModel.getPersonDetails(personId, true) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StaffTitleSection(
    avatarUrl: String?,
    name: String?,
    japaneseName: String?,
    staffRoles: Map<String, Int?>,
    modifier: Modifier = Modifier
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        //horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {
        RoundedImage(
            model = avatarUrl,
            modifier = Modifier.fillMaxHeight(0.8f)
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
                name?.let { englishName ->
                    Text(
                        text = englishName,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                japaneseName?.let { japaneseName ->
                    Text(
                        text = japaneseName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(
                    topStart = 12.dp,
                    bottomStart = 12.dp,
                    topEnd = if(isExpanded) 0.dp else 4.dp,
                    bottomEnd = if(isExpanded) 0.dp else 4.dp,
                ))
                .clickable(
                    interactionSource = mutableInteractionSource,
                    indication = null,
                    onClick = {
                        isExpanded = !isExpanded
                    }
                )
                .background(MaterialTheme.colorScheme.surface),
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
            modifier = Modifier.fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
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