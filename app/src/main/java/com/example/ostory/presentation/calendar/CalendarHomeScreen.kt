package com.example.ostory.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ostory.data.repository.ReviewRepository
import com.example.ostory.domain.model.ReviewRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarHomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToReviewDetail: (Int) -> Unit,
    reviewRepository: ReviewRepository = ReviewRepository.getInstance()
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now()) }
    val reviews by reviewRepository.recordsFlow.collectAsState(
        initial = reviewRepository.getRecords()
    )

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

                // 이 날짜의 감상 기록 찾기
                val reviewForDay = reviews.find { it.watchedDate == dateString }
                val posterUrl = reviewForDay?.posterPath

                val today = LocalDate.now()
                val isToday = date.isEqual(today)

                Box(
                    modifier = Modifier
                        .aspectRatio(0.7f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (reviewForDay != null && !posterUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = posterUrl,
                            contentDescription = "감상 기록 포스터",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigateToReviewDetail(reviewForDay.id) },
                            contentScale = ContentScale.Crop
                        )
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
}
