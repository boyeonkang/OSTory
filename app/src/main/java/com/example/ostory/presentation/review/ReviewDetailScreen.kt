package com.example.ostory.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import android.content.Intent
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import com.example.ostory.data.repository.ReviewRepository
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.OstTrack
import com.example.ostory.domain.model.ReviewRecord
import com.example.ostory.presentation.detail.OstSection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    recordId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToReviewWrite: (Int, String, Int) -> Unit,
    viewModel: ReviewDetailViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(recordId) {
        viewModel.loadReviewRecord(recordId)
    }

    val recordState by viewModel.record.collectAsState()
    val record = recordState
    val ostList by viewModel.ostList.collectAsState()
    val isOstLoading by viewModel.isOstLoading.collectAsState()
    val isOstLoaded by viewModel.isOstLoaded.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (record == null) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("감상 기록 상세", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "뒤로가기",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color.White
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "감상 기록을 찾을 수 없습니다.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9C27B0))
                    ) {
                        Text("뒤로 가기")
                    }
                }
            }
        }
        return
    }

    val formattedDate = remember(record.watchedDate) {
        try {
            val localDate = LocalDate.parse(record.watchedDate)
            localDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E) 관람", Locale.KOREAN))
        } catch (e: Exception) {
            record.watchedDate
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "감상 기록 삭제",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = "이 감상 기록을 정말로 삭제하시겠습니까?",
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        ReviewRepository.getInstance().deleteRecord(recordId)
                        onNavigateBack()
                    }
                ) {
                    Text("삭제", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "감상 기록",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "뒤로가기",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onNavigateToReviewWrite(record.workId, record.workType.name, record.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "수정",
                            tint = Color.DarkGray
                        )
                    }
                    IconButton(onClick = { shareReviewRecord(context, record) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "공유",
                            tint = Color.DarkGray
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "삭제",
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. 작품 포스터 이미지
            if (record.posterPath != null) {
                AsyncImage(
                    model = record.posterPath,
                    contentDescription = record.titleKo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
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

            // 2. 제목 정보
            Text(
                text = record.titleKo ?: "",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = record.titleEn ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF7F7F7F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7F7F7F)
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            // 3. 내 별점 영역
            Text(
                text = "내 별점",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2C3E50)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    val isSelected = i <= record.rating
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "$i 점",
                        tint = if (isSelected) Color(0xFFFFC107) else Color(0xFFD1D1D6),
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${record.rating}점",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF2C3E50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. 내 감상평 영역
            Text(
                text = "내 감상평",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2C3E50)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF2F2F7),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = record.comment.ifBlank { "작성된 감상평이 없습니다." },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (record.comment.isBlank()) Color(0xFF9E9E9E) else Color.Black
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OstSection(
                workTitle = record.titleKo ?: "",
                ostList = ostList,
                isOstLoading = isOstLoading,
                isOstLoaded = isOstLoaded,
                onFetchOstClick = { viewModel.fetchOst() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun shareReviewRecord(context: android.content.Context, record: ReviewRecord?) {
    if (record == null) return
    try {
        val rating = record.rating.coerceIn(0, 5)
        val stars = "★".repeat(rating) + "☆".repeat(5 - rating)
        val title = record.titleKo ?: ""
        val date = record.watchedDate ?: ""
        val comment = record.comment ?: ""

        val shareText = "[OSTory 감상 기록]\n\n" +
                "작품: $title\n" +
                "감상일: $date\n" +
                "별점: $stars\n" +
                "한줄평: $comment"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "감상 기록 공유하기")
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
