package com.example.ostory.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.WorkType
import com.example.ostory.domain.model.OstTrack
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign

@Composable
fun WorkDetailScreen(
    workId: Int,
    workType: String,
    selectedDate: String? = null,
    onNavigateToReviewWrite: (Int, String, String?) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: WorkDetailViewModel = viewModel()
) {
    val work by viewModel.work.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val ostList by viewModel.ostList.collectAsState()
    val isOstLoading by viewModel.isOstLoading.collectAsState()
    val isOstLoaded by viewModel.isOstLoaded.collectAsState()

    LaunchedEffect(workId, workType) {
        viewModel.loadWorkDetail(workId, workType)
    }

    Scaffold { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = onNavigateBack) {
                            Text("뒤로 가기")
                        }
                    }
                }
            }

            work != null -> {
                WorkDetailContent(
                    work = work!!,
                    workType = workType,
                    selectedDate = selectedDate,
                    ostList = ostList,
                    isOstLoading = isOstLoading,
                    isOstLoaded = isOstLoaded,
                    onFetchOstClick = { viewModel.fetchOst() },
                    onNavigateToReviewWrite = onNavigateToReviewWrite,
                    onNavigateBack = onNavigateBack,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun WorkDetailContent(
    work: Work,
    workType: String,
    selectedDate: String? = null,
    ostList: List<OstTrack> = emptyList(),
    isOstLoading: Boolean = false,
    isOstLoaded: Boolean = false,
    onFetchOstClick: () -> Unit = {},
    onNavigateToReviewWrite: (Int, String, String?) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            OutlinedButton(onClick = onNavigateBack) {
                Text("뒤로 가기")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (work.posterPath != null) {
                AsyncImage(
                    model = work.posterPath,
                    contentDescription = work.titleKo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    color = Color(0xFFE0E0E0)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "포스터 없음",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = work.titleKo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = work.titleEn,
                style = MaterialTheme.typography.titleMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (work.type == WorkType.MOVIE) "영화" else "드라마",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (work.year > 0) "${work.year}년" else "연도 정보 없음",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (work.genres.isNotEmpty()) {
                Text(
                    text = "장르",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(work.genres) { genre ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = genre.name,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "평점 ${String.format("%.1f", work.rating)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "줄거리",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = work.plot.ifBlank { "줄거리 정보가 없습니다." },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            WorkDetailOstSection(
                workTitle = work.titleKo,
                ostList = ostList,
                isOstLoading = isOstLoading,
                isOstLoaded = isOstLoaded,
                onFetchOstClick = onFetchOstClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onNavigateToReviewWrite(work.id, workType, selectedDate) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("감상 기록 남기기")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun WorkDetailOstSection(
    workTitle: String,
    ostList: List<OstTrack>,
    isOstLoading: Boolean,
    isOstLoaded: Boolean,
    onFetchOstClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredList = remember(ostList) {
        ostList.filter { track ->
            val title = track.title?.trim() ?: ""
            val artist = track.artist?.trim() ?: ""
            title.isNotEmpty() && title != "알 수 없음" &&
            artist.isNotEmpty() && artist != "알 수 없음"
        }.take(3)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "OST",
                tint = Color(0xFF9C27B0),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "OST",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isOstLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OST 정보를 불러오는 중입니다...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else if (!isOstLoaded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OST 정보를 불러올 수 있습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = onFetchOstClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("OST 정보 불러오기")
                }
            }
        } else if (filteredList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "등록된 OST 정보가 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onFetchOstClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("OST 정보 다시 불러오기")
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredList.forEach { track ->
                    WorkDetailOstTrackItem(track = track, workTitle = workTitle)
                }
            }
        }
    }
}

@Composable
fun WorkDetailOstTrackItem(
    track: OstTrack,
    workTitle: String
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF2F2F7),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = track.title.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Button(
                    onClick = {
                        val query = "${track.title.orEmpty()} ${track.artist.orEmpty()} $workTitle OST"
                        val encodedQuery = Uri.encode(query)
                        val url = "https://www.youtube.com/results?search_query=$encodedQuery"
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0000),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "YouTube에서 보기",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 0.5.dp)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "앨범: ${track.album.orEmpty()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val comp = track.composer.orEmpty().ifBlank { "알 수 없음" }
            val lyr = track.lyricist.orEmpty().ifBlank { "알 수 없음" }
            Text(
                text = "작곡: $comp | 작사: $lyr",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!track.originalArtist.isNullOrBlank()) {
                Text(
                    text = "원곡: ${track.originalArtist.orEmpty()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
