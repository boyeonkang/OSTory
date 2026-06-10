package com.example.ostory.presentation.preference

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.util.Locale

@Composable
fun PreferenceScreen(
    viewModel: PreferenceAnalysisViewModel = viewModel()
) {
    val totalCount by viewModel.totalCount.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val sortedRecords by viewModel.sortedRecords.collectAsState()
    val currentMonthCount by viewModel.currentMonthCount.collectAsState()
    val monthlyCounts by viewModel.monthlyCounts.collectAsState()
    val ratingDistribution by viewModel.ratingDistribution.collectAsState()
    val dayOfWeekDistribution by viewModel.dayOfWeekDistribution.collectAsState()

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        if (totalCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFAFAFA)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = Color(0xFFBDBDBD),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "아직 감상 기록이 없습니다.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "검색 탭에서 작품을 찾아 감상 기록을 남겨보세요.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFAFAFA)),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 상단 제목 및 부제
                item {
                    Column {
                        Text(
                            text = "AI 취향 분석",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "당신의 영화 취향을 분석했어요",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }

                // 요약 카드 영역
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryCard(
                            title = "총 감상작",
                            value = "${totalCount}개",
                            description = "작품 기록 완료",
                            icon = Icons.Default.Movie,
                            iconTint = Color(0xFF9C27B0),
                            backgroundColor = Color(0xFFF9F5FB),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "평균 별점",
                            value = "${String.format(Locale.US, "%.1f", averageRating)}점",
                            description = "남긴 별점 평균",
                            icon = Icons.Default.Star,
                            iconTint = Color(0xFFFFC107),
                            backgroundColor = Color(0xFFFEFDF0),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "이번 달 감상",
                            value = "${currentMonthCount}개",
                            description = "이번 달 감상 수",
                            icon = Icons.Default.Favorite,
                            iconTint = Color(0xFFE91E63),
                            backgroundColor = Color(0xFFFFF0F5),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 통계 섹션 타이틀
                item {
                    SectionHeader(title = "감상 통계")
                }

                // 월별 감상 수
                item {
                    MonthlyStatisticsCard(monthlyCounts = monthlyCounts)
                }

                // 별점 분포
                item {
                    RatingStatisticsCard(ratingDistribution = ratingDistribution)
                }

                // 요일별 감상 수
                item {
                    DayOfWeekStatisticsCard(dayOfWeekDistribution = dayOfWeekDistribution)
                }

                // 전체 감상 기록 목록 타이틀
                item {
                    SectionHeader(title = "전체 감상 기록")
                }

                // 전체 감상 기록 목록 아이템들
                items(sortedRecords) { record ->
                    ReviewRecordItem(record = record)
                }

                // AI 취향 분석 섹션 타이틀
                item {
                    SectionHeader(title = "AI 취향 분석 결과")
                }

                // AI 취향 분석 버튼
                item {
                    Button(
                        onClick = { viewModel.analyzePreferences() },
                        enabled = !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9C27B0),
                            disabledContainerColor = Color(0xFFE1BEE7)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "취향을 분석하는 중입니다...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "분석 시작",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI 취향 분석하기",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // 에러 메시지 카드
                if (errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFFDA4AF).copy(alpha = 0.8f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp, horizontal = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    fontSize = 14.sp,
                                    color = Color(0xFFB91C1C),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // AI 취향 요약 보고서 카드
                if (analysisResult != null && analysisResult!!.summary.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F5FB)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE1BEE7).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "AI 요약",
                                        tint = Color(0xFF9C27B0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI가 분석한 나의 취향 요약",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9C27B0)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = analysisResult!!.summary,
                                    fontSize = 14.sp,
                                    color = Color(0xFF3B0764),
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // 선호 장르 섹션
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = "선호 장르")
                        if (totalCount == 0) {
                            EmptyStateCard(message = "감상 기록을 추가하면 AI 취향 분석을 확인할 수 있습니다.")
                        } else if (isAnalyzing) {
                            EmptyStateCard(message = "취향을 분석하는 중입니다.")
                        } else if (analysisResult == null) {
                            val message = if (errorMessage != null) "분석 결과를 불러오지 못했습니다." else "AI 취향 분석을 시작하면 결과를 확인할 수 있습니다."
                            EmptyStateCard(message = message)
                        } else {
                            val genres = analysisResult!!.preferredGenres.take(3)
                            if (genres.isEmpty()) {
                                EmptyStateCard(message = "선호 장르 분석 결과가 없습니다.")
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        genres.forEachIndexed { index, genre ->
                                            if (index > 0) {
                                                HorizontalDivider(
                                                    color = Color(0xFFF3F4F6),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(vertical = 16.dp)
                                                )
                                            }
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(50),
                                                        color = Color(0xFFF3E5F5),
                                                        contentColor = Color(0xFF9C27B0)
                                                    ) {
                                                        Text(
                                                            text = genre.name,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = genre.reason,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF6B7280),
                                                    lineHeight = 20.sp,
                                                    maxLines = 3,
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

                // 음악 취향 섹션
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = "음악 취향")
                        if (totalCount == 0) {
                            EmptyStateCard(message = "감상 기록을 추가하면 AI 취향 분석을 확인할 수 있습니다.")
                        } else if (isAnalyzing) {
                            EmptyStateCard(message = "취향을 분석하는 중입니다.")
                        } else if (analysisResult == null) {
                            val message = if (errorMessage != null) "분석 결과를 불러오지 못했습니다." else "AI 취향 분석을 시작하면 결과를 확인할 수 있습니다."
                            EmptyStateCard(message = message)
                        } else {
                            val keywords = analysisResult!!.musicKeywords.take(3)
                            if (keywords.isEmpty()) {
                                EmptyStateCard(message = "음악 취향 분석 결과가 없습니다.")
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFE0B2).copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        keywords.forEachIndexed { index, music ->
                                            if (index > 0) {
                                                HorizontalDivider(
                                                    color = Color(0xFFFFF7ED),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(vertical = 16.dp)
                                                )
                                            }
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(50),
                                                        color = Color(0xFFFFF3E0),
                                                        contentColor = Color(0xFFE65100)
                                                    ) {
                                                        val cleanKeyword = music.keyword.trim()
                                                        val displayKeyword = if (cleanKeyword.startsWith("#")) cleanKeyword else "#$cleanKeyword"
                                                        Text(
                                                            text = displayKeyword,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = music.reason,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF6B7280),
                                                    lineHeight = 20.sp,
                                                    maxLines = 3,
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

                // AI 추천 작품 섹션
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = "AI 추천 작품")
                        if (totalCount == 0) {
                            EmptyStateCard(message = "감상 기록을 추가하면 AI 취향 분석을 확인할 수 있습니다.")
                        } else if (isAnalyzing) {
                            EmptyStateCard(message = "취향을 분석하는 중입니다.")
                        } else if (analysisResult == null) {
                            val message = if (errorMessage != null) "분석 결과를 불러오지 못했습니다." else "AI 취향 분석을 시작하면 결과를 확인할 수 있습니다."
                            EmptyStateCard(message = message)
                        } else {
                            val recommendations = analysisResult!!.recommendations.take(3)
                            if (recommendations.isEmpty()) {
                                EmptyStateCard(message = "추천 작품 분석 결과가 없습니다.")
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        recommendations.forEachIndexed { index, rec ->
                                            if (index > 0) {
                                                HorizontalDivider(
                                                    color = Color(0xFFF3F4F6),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(vertical = 16.dp)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color(0xFFE8F5E9),
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.Movie,
                                                            contentDescription = "추천 작품",
                                                            tint = Color(0xFF4CAF50),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = rec.title,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.Black
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = rec.reason,
                                                        fontSize = 13.sp,
                                                        color = Color(0xFF6B7280),
                                                        lineHeight = 20.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 9.sp,
                color = Color(0xFF9E9E9E),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MonthlyStatisticsCard(monthlyCounts: List<Pair<String, Int>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "월별 감상 수",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (monthlyCounts.isEmpty()) {
                Text(
                    text = "월별 통계 데이터가 없습니다.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                val maxCount = monthlyCounts.maxOf { it.second }.coerceAtLeast(1)
                val displayList = monthlyCounts.takeLast(6)
                displayList.forEach { (month, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = month,
                            fontSize = 12.sp,
                            color = Color(0xFF616161),
                            modifier = Modifier.width(60.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(6.dp))
                        ) {
                            val fraction = count.toFloat() / maxCount
                            if (fraction > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction)
                                        .fillMaxHeight()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(Color(0xFFE1BEE7), Color(0xFF9C27B0))
                                            ),
                                            RoundedCornerShape(6.dp)
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${count}개",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingStatisticsCard(ratingDistribution: Map<Int, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "별점 분포",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            val maxCount = ratingDistribution.values.maxOfOrNull { it }?.coerceAtLeast(1) ?: 1
            
            for (rating in 5 downTo 1) {
                val count = ratingDistribution[rating] ?: 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.width(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${rating}점",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF616161)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(6.dp))
                    ) {
                        val fraction = count.toFloat() / maxCount
                        if (fraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFFFE082), Color(0xFFFFB300))
                                        ),
                                        RoundedCornerShape(6.dp)
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${count}개",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun DayOfWeekStatisticsCard(dayOfWeekDistribution: Map<java.time.DayOfWeek, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "요일별 감상 수",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val days = listOf(
                java.time.DayOfWeek.MONDAY,
                java.time.DayOfWeek.TUESDAY,
                java.time.DayOfWeek.WEDNESDAY,
                java.time.DayOfWeek.THURSDAY,
                java.time.DayOfWeek.FRIDAY,
                java.time.DayOfWeek.SATURDAY,
                java.time.DayOfWeek.SUNDAY
            )
            val maxCount = dayOfWeekDistribution.values.maxOfOrNull { it }?.coerceAtLeast(1) ?: 1
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    val count = dayOfWeekDistribution[day] ?: 0
                    val fraction = count.toFloat() / maxCount
                    val displayDay = when (day) {
                        java.time.DayOfWeek.MONDAY -> "월"
                        java.time.DayOfWeek.TUESDAY -> "화"
                        java.time.DayOfWeek.WEDNESDAY -> "수"
                        java.time.DayOfWeek.THURSDAY -> "목"
                        java.time.DayOfWeek.FRIDAY -> "금"
                        java.time.DayOfWeek.SATURDAY -> "토"
                        java.time.DayOfWeek.SUNDAY -> "일"
                    }
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (count > 0) {
                            Text(
                                text = "${count}개",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .weight(1f)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(fraction)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0xFF80DEEA), Color(0xFF00ACC1))
                                        ),
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = displayDay,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF616161)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewRecordItem(record: com.example.ostory.domain.model.ReviewRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (record.posterPath != null) {
                AsyncImage(
                    model = record.posterPath,
                    contentDescription = record.titleKo,
                    modifier = Modifier
                        .width(60.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .width(60.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFFE0E0E0)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = record.titleKo ?: "알 수 없음",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = record.watchedDate,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        val isFilled = index < record.rating
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isFilled) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${record.rating}.0",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF616161)
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = record.comment,
                    fontSize = 13.sp,
                    color = Color(0xFF424242),
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
