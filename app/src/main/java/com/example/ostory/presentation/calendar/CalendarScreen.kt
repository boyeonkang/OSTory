package com.example.ostory.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Movie
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
import com.example.ostory.domain.model.ReviewRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToSearch: (String) -> Unit,
    onNavigateToReviewDetail: (Int) -> Unit,
    viewModel: CalendarViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now()) }
    val reviews by viewModel.recordsFlow.collectAsState(
        initial = viewModel.getRecords()
    )

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedDateReviews by remember { mutableStateOf<List<ReviewRecord>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // 1. 상단 타이틀 "OSTory"
        Text(
            text = "OSTory",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        )

        // 2. 월 선택기 (이전 달, 현재 년월, 다음 달)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "이전 달",
                    tint = Color(0xFF555555)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.width(120.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "다음 달",
                    tint = Color(0xFF555555)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 요일 헤더 ("일 월 화 수 목 금 토")
        val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEachIndexed { index, day ->
                val color = when (index) {
                    0 -> Color(0xFFFF4D4D) // 일요일: 붉은 계열
                    6 -> Color(0xFF3B82F6) // 토요일: 파란 계열
                    else -> Color(0xFF6B7280) // 평일: 회색 계열
                }
                Text(
                    text = day,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = color,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. 날짜 그리드 계산
        val firstDayOfMonth = currentMonth.withDayOfMonth(1)
        val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7
        val daysInMonth = currentMonth.lengthOfMonth()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 시작 요일 전까지 빈 공간 채우기
            items(firstDayOfWeekIndex) {
                Spacer(modifier = Modifier.aspectRatio(0.7f))
            }

            // 1일부터 마지막 날까지 렌더링
            items(daysInMonth) { index ->
                val day = index + 1
                val date = currentMonth.withDayOfMonth(day)
                val dateString = String.format(Locale.US, "%04d-%02d-%02d", date.year, date.monthValue, date.dayOfMonth)

                // 이 날짜의 감상 기록 필터링
                val reviewsForDay = reviews.filter { it.watchedDate == dateString }
                val reviewCount = reviewsForDay.size
                val firstReview = reviewsForDay.firstOrNull()
                val posterUrl = firstReview?.posterPath

                val today = LocalDate.now()
                val isToday = date.isEqual(today)

                val clickableModifier = if (reviewsForDay.isNotEmpty() && !posterUrl.isNullOrEmpty()) {
                    Modifier.clickable {
                        if (reviewCount == 1) {
                            onNavigateToReviewDetail(firstReview.id)
                        } else {
                            selectedDateReviews = reviewsForDay
                            showBottomSheet = true
                        }
                    }
                } else {
                    Modifier.clickable { onNavigateToSearch(dateString) }
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(0.7f)
                        .fillMaxSize()
                        .then(clickableModifier),
                    contentAlignment = Alignment.Center
                ) {
                    if (firstReview != null && !posterUrl.isNullOrEmpty()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = posterUrl,
                                contentDescription = "감상 기록 포스터",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            if (reviewCount > 1) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+${reviewCount - 1}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        val dayOfWeekIndex = (firstDayOfWeekIndex + index) % 7
                        val textColor = when {
                            isToday -> Color.White
                            dayOfWeekIndex == 0 -> Color(0xFFFF4D4D) // 일요일: 붉은 계열
                            dayOfWeekIndex == 6 -> Color(0xFF3B82F6) // 토요일: 파란 계열
                            else -> Color.Black
                        }

                        val textModifier = if (isToday) {
                            Modifier
                                .size(32.dp)
                                .background(Color(0xFF3B82F6), CircleShape)
                                .wrapContentSize(Alignment.Center)
                        } else {
                            Modifier
                        }

                        Text(
                            text = day.toString(),
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            modifier = textModifier,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "${selectedDateReviews.firstOrNull()?.watchedDate ?: ""} 감상 기록 목록",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(selectedDateReviews) { record ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showBottomSheet = false
                                    onNavigateToReviewDetail(record.id)
                                }
                                .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (record.posterPath != null) {
                                AsyncImage(
                                    model = record.posterPath,
                                    contentDescription = record.titleKo,
                                    modifier = Modifier
                                        .width(50.dp)
                                        .height(70.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .width(50.dp)
                                        .height(70.dp)
                                        .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = record.titleKo ?: "",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 1..5) {
                                        val isSelected = i <= record.rating
                                        Icon(
                                            imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(0xFFFFC107) else Color(0xFFD1D1D6),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${record.rating}점",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = record.comment.ifBlank { "작성된 한줄평이 없습니다." },
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
