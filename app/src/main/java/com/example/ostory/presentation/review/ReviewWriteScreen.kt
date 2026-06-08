package com.example.ostory.presentation.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ostory.domain.model.Work
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWriteScreen(
    workId: Int,
    workType: String,
    selectedDate: String? = null,
    onNavigateToReviewSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ReviewWriteViewModel = viewModel()
) {
    val work by viewModel.work.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val reviewText by viewModel.reviewText.collectAsState()
    val isSaveEnabled by viewModel.isSaveEnabled.collectAsState()

    LaunchedEffect(workId, workType) {
        viewModel.loadWorkDetail(workId, workType)
    }

    val date = if (!selectedDate.isNullOrBlank() && selectedDate != "{selectedDate}") {
        try {
            LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            LocalDate.now()
        }
    } else {
        LocalDate.now()
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN)
    val formattedDate = date.format(formatter)

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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (isSaveEnabled) {
                            val safeSelectedDate = if (!selectedDate.isNullOrBlank() && selectedDate != "{selectedDate}") {
                                try {
                                    LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    selectedDate
                                } catch (e: Exception) {
                                    null
                                }
                            } else {
                                null
                            }
                            val success = viewModel.saveReviewRecord(safeSelectedDate)
                            if (success) {
                                onNavigateToReviewSaved()
                            }
                        }
                    },
                    enabled = isSaveEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0), // 활성화 보라색
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE0E0E0), // 비활성화 회색
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = "저장하기",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        },
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
                    CircularProgressIndicator(color = Color(0xFF9C27B0))
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF9C27B0)
                            )
                        ) {
                            Text("뒤로 가기")
                        }
                    }
                }
            }

            work != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // 1. 작품 정보 영역 (포스터 이미지 + 타이틀 + 날짜)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (work!!.posterPath != null) {
                            AsyncImage(
                                model = work!!.posterPath,
                                contentDescription = work!!.titleKo,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = Color(0xFFE0E0E0)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = "포스터 없음",
                                        modifier = Modifier.size(32.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = work!!.titleKo,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = work!!.titleEn,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF7F7F7F),
                                    fontSize = 14.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF7F7F7F),
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. 별점 매겨주세요 영역
                    Text(
                        text = "별점을 매겨주세요",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2C3E50)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            val isSelected = i <= rating
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$i 점",
                                tint = if (isSelected) Color(0xFFFFC107) else Color(0xFFD1D1D6),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable { viewModel.setRating(i) }
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (rating > 0) "${rating}점" else "별점을 선택해주세요",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (rating > 0) Color(0xFF2C3E50) else Color(0xFF7F7F7F),
                            fontSize = 14.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. 한줄 감상평 영역
                    Text(
                        text = "한줄 감상평",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2C3E50)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = reviewText,
                        onValueChange = { viewModel.setReviewText(it) },
                        placeholder = {
                            Text(
                                text = "이 작품에 대한 감상을 한 줄로 남겨보세요",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF9E9E9E)
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF2F2F7),
                            unfocusedContainerColor = Color(0xFFF2F2F7),
                            disabledContainerColor = Color(0xFFF2F2F7),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${reviewText.length} / 100",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF9E9E9E),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
