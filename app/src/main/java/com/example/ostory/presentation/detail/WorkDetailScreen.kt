package com.example.ostory.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.unit.sp
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

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
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
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        item {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .offset(x = (-12).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "뒤로가기",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (work.posterPath != null) {
                    AsyncImage(
                        model = work.posterPath,
                        contentDescription = work.titleKo,
                        modifier = Modifier
                            .width(280.dp)
                            .height(400.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .width(280.dp)
                            .height(400.dp)
                            .clip(RoundedCornerShape(8.dp)),
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = work.titleKo,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = work.titleEn,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val typeText = if (work.type == WorkType.MOVIE) "영화" else "드라마"
                val yearText = if (work.year > 0) "${work.year}" else "연도 정보 없음"

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFF1F3F5),
                    contentColor = Color(0xFF4B5563)
                ) {
                    Text(
                        text = typeText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFF1F3F5),
                    contentColor = Color(0xFF4B5563)
                ) {
                    Text(
                        text = yearText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (work.genres.isNotEmpty()) {
                Text(
                    text = "장르",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(work.genres) { genre ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF0F172A),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = genre.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "줄거리",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = work.plot.ifBlank { "줄거리 정보가 없습니다." },
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    color = Color(0xFF374151)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "평점",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val rating5 = (work.rating / 2.0).coerceIn(0.0, 5.0)
                for (i in 1..5) {
                    val starIcon = when {
                        rating5 >= i - 0.25 -> Icons.Filled.Star
                        rating5 >= i - 0.75 -> Icons.AutoMirrored.Filled.StarHalf
                        else -> Icons.Outlined.Star
                    }
                    val starTint = when {
                        rating5 >= i - 0.75 -> Color(0xFFFFC107) // Yellow for filled and half stars
                        else -> Color(0xFFD1D1D6) // Light gray for empty stars
                    }
                    Icon(
                        imageVector = starIcon,
                        contentDescription = null,
                        tint = starTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${String.format("%.1f", rating5)} / 5.0",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF374151)
                    )
                )
            }

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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C27B0),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "감상 기록 남기기",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
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
        color = Color(0xFFF8F9FA),
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
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = track.artist.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = Color.Gray,
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
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "YouTube에서 보기",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            val hasAlbum = !track.album.isNullOrBlank()
            val hasCompOrLyr = !track.composer.isNullOrBlank() || !track.lyricist.isNullOrBlank()
            val hasOrigArtist = !track.originalArtist.isNullOrBlank()
            
            if (hasAlbum || hasCompOrLyr || hasOrigArtist) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (hasAlbum) {
                    Text(
                        text = "앨범: ${track.album.orEmpty()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (hasCompOrLyr) {
                    val comp = track.composer.orEmpty().ifBlank { "알 수 없음" }
                    val lyr = track.lyricist.orEmpty().ifBlank { "알 수 없음" }
                    Text(
                        text = "작곡: $comp | 작사: $lyr",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
}
