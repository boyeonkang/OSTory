package com.example.ostory.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.WorkType

@Composable
fun SearchScreenRoute(
    selectedDate: String? = null,
    onNavigateToDetail: (Int, String, String?) -> Unit,
    onCloseClick: () -> Unit,
    viewModel: SearchViewModel
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val hasSearched by viewModel.hasSearched.collectAsState()

    SearchScreen(
        query = query,
        results = results,
        isLoading = isLoading,
        errorMessage = errorMessage,
        hasSearched = hasSearched,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::search,
        onClear = viewModel::clearSearch,
        onNavigateToDetail = { workId, type ->
            onNavigateToDetail(workId, type, selectedDate)
        },
        onCloseClick = onCloseClick
    )
}

@Composable
fun SearchScreen(
    query: String,
    results: List<Work>,
    isLoading: Boolean,
    errorMessage: String?,
    hasSearched: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onNavigateToDetail: (Int, String) -> Unit,
    onCloseClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        color = Color.White
    ) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Text(
                        text = "OSTory 검색 (테스트 렌더링 확인)",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onCloseClick) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기",
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        OutlinedTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            placeholder = { Text("영화나 드라마 제목을 검색하세요", color = Color.Gray) },
                            trailingIcon = {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = onClear) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "지우기",
                                            tint = Color.Black
                                        )
                                    }
                                } else {
                                    IconButton(onClick = onSearch) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "검색",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { onSearch() }
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            },
            containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else if (hasSearched && results.isEmpty()) {
                Text(
                    text = "검색 결과가 없습니다.",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else if (!hasSearched && results.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "OSTory 검색",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "영화나 드라마 제목을 검색하여 기록해 보세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(results) { work ->
                        SearchResultItem(
                            work = work,
                            onClick = {
                                onNavigateToDetail(work.id, if (work.type == WorkType.MOVIE) "movie" else "drama")
                            }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
fun SearchResultItem(
    work: Work,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (work.posterPath != null) {
            AsyncImage(
                model = work.posterPath,
                contentDescription = work.titleKo,
                modifier = Modifier
                    .size(width = 64.dp, height = 96.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier
                    .size(width = 64.dp, height = 96.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFFE0E0E0),
                contentColor = Color.Gray
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "포스터 없음"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = work.titleKo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = work.titleEn,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkTypeChip(type = work.type)
                Text(
                    text = if (work.year > 0) "${work.year}년" else "연도 정보 없음",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun WorkTypeChip(type: WorkType) {
    val backgroundColor = if (type == WorkType.MOVIE) Color(0xFFEADDFF) else Color(0xFFD6F0FF)
    val contentColor = if (type == WorkType.MOVIE) Color(0xFF21005D) else Color(0xFF004D7A)
    val text = if (type == WorkType.MOVIE) "영화" else "드라마"

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
