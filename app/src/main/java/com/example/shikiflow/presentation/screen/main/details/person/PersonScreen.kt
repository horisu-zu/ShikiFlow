package com.example.shikiflow.presentation.screen.main.details.person

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.toBrowseAnime
import com.example.shikiflow.domain.model.anime.toBrowseManga
import com.example.shikiflow.domain.model.person.GroupedRole
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.character.CharacterMediaSection
import com.example.shikiflow.presentation.screen.main.details.common.CommentSection
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode
import com.example.shikiflow.presentation.viewmodel.person.PersonViewModel
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
    personId: String,
    navOptions: MediaNavOptions,
    personViewModel: PersonViewModel = hiltViewModel()
) {
    val personDetails by personViewModel.personDetails.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isAtTop by remember {
        derivedStateOf {
            scrollState.value <= 0
        }
    }

    LaunchedEffect(personId) {
        personViewModel.getPersonDetails(personId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if(isAtTop) stringResource(R.string.person_title)
                            else personDetails.data?.name ?: stringResource(R.string.person_title),
                        style = MaterialTheme.typography.titleLarge
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
        }
    ) { paddingValues ->
        when(personDetails) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                personDetails.data?.let { details ->
                    val horizontalPadding = 12.dp

                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(
                                top = paddingValues.calculateTopPadding(),
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            ).padding(horizontal = horizontalPadding)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                    ) {
                        PersonTitleSection(
                            avatarUrl = "${BuildConfig.BASE_URL}${details.image.original}",
                            name = details.name,
                            japaneseName = details.japanese,
                            groupedRoles = details.groupedRoles,
                            modifier = Modifier.height(148.dp)
                        )
                        details.roles?.let {
                            LazyRow(
                                modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = horizontalPadding),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(details.roles) { role ->
                                    role.characters.forEach { character ->
                                        CharacterCard(
                                            characterPoster = "${BuildConfig.BASE_URL}${character.image.original}",
                                            characterName = character.name,
                                            onClick = { navOptions.navigateToCharacterDetails(
                                                characterId = character.id.toString()
                                            ) },
                                            modifier = Modifier.width(96.dp)
                                        )
                                    }
                                }
                            }
                        }
                        if(!details.works.isNullOrEmpty()) {
                            val browseItems = details.works.mapNotNull { work ->
                                when {
                                    work.anime != null -> work.anime.toBrowseAnime()
                                    work.manga != null -> work.manga.toBrowseManga()
                                    else -> null
                                }
                            }
                            CharacterMediaSection(
                                sectionTitle = stringResource(R.string.works_title),
                                items = browseItems,
                                onItemClick = { id, mediaType ->
                                    when(mediaType) {
                                        MediaType.ANIME -> navOptions.navigateToAnimeDetails(id)
                                        MediaType.MANGA -> navOptions.navigateToMangaDetails(id)
                                    }
                                }
                            )
                        }
                        details.topicId?.let { topicId ->
                            CommentSection(
                                topicId = topicId.toString(),
                                isRefreshing = false,
                                onEntityClick = { entityType, id ->
                                    navOptions.navigateByEntity(entityType, id)
                                },
                                onLinkClick = { WebIntent.openUrlCustomTab(context, it) },
                                onTopicNavigate = { topicId ->
                                    navOptions.navigateToComments(
                                        screenMode = CommentsScreenMode.TOPIC,
                                        id = topicId
                                    )
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
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
                        message = personDetails.message ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { personViewModel.getPersonDetails(personId, true) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PersonTitleSection(
    avatarUrl: String,
    name: String?,
    japaneseName: String?,
    groupedRoles: List<GroupedRole>?,
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

        groupedRoles?.let {
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
                    groupedRoles.forEach { role ->
                        Text(
                            text = "${role.role}: ${role.count}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}