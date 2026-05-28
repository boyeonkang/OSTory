package com.example.ostory.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun WorkDetailScreen(
    workId: Int,
    workType: String,
    onNavigateToReviewWrite: (Int, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: WorkDetailViewModel = viewModel()
) {
    val work by viewModel.work.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(workId, workType) {
        viewModel.loadWorkDetail(workId, workType)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                errorMessage != null -> {
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

                work != null -> {
                    WorkDetailContent(
                        work = work!!,
                        workType = workType,
                        onNavigateToReviewWrite = onNavigateToReviewWrite,
                        onNavigateBack = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
fun WorkDetailContent(
    work: Work,
    workType: String,
    onNavigateToReviewWrite: (Int, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
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
                    .height(420.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
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

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onNavigateToReviewWrite(work.id, workType) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("감상 기록 남기기")
        }
    }
}