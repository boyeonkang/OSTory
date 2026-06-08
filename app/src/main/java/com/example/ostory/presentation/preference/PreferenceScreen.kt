package com.example.ostory.presentation.preference

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

@Composable
fun PreferenceScreen(
    viewModel: PreferenceAnalysisViewModel = viewModel()
) {
    val totalCount by viewModel.totalCount.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAFAFA)),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 64.dp),
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

            // 기본 통계 카드 (가로 배치 Row)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 총 관람작 카드
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F5FB)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = "총 관람작 수",
                                tint = Color(0xFF9C27B0),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "총 관람작",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF757575)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${totalCount}개",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "작품 기록 완료",
                                fontSize = 11.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }

                    // 평균 별점 카드
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F5FB)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "평균 별점",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "평균 별점",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF757575)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${String.format(Locale.US, "%.1f", averageRating)}점",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "남긴 별점 평균",
                                fontSize = 11.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                }
            }

            // AI 취향 분석 버튼 (감상 기록이 있을 때만 노출)
            if (totalCount > 0) {
                item {
                    Button(
                        onClick = { viewModel.analyzePreferences() },
                        enabled = !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9C27B0), // 프리미엄 퍼플
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
            }

            // AI 취향 요약 보고서 카드 (분석 결과가 있고 요약이 있을 때만 노출)
            if (analysisResult != null && analysisResult!!.summary.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)), // 연보라색
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE1BEE7))
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
                                color = Color(0xFF4A148C),
                                lineHeight = 22.sp,
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
                        EmptyStateCard(message = "AI 취향 분석을 시작하면 결과를 확인할 수 있습니다.")
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
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    genres.forEachIndexed { index, genre ->
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFF3E5F5), RoundedCornerShape(20.dp))
                                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = genre.name,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF9C27B0)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = genre.reason,
                                                fontSize = 13.sp,
                                                color = Color(0xFF555555),
                                                lineHeight = 18.sp
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
                        EmptyStateCard(message = "AI 취향 분석을 시작하면 결과를 확인할 수 있습니다.")
                    } else {
                        val keywords = analysisResult!!.musicKeywords.take(3)
                        if (keywords.isEmpty()) {
                            EmptyStateCard(message = "음악 취향 분석 결과가 없습니다.")
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    keywords.forEachIndexed { index, music ->
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFFFF3E0), RoundedCornerShape(20.dp))
                                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "#${music.keyword}",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFE65100)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = music.reason,
                                                fontSize = 13.sp,
                                                color = Color(0xFF555555),
                                                lineHeight = 18.sp
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
                        EmptyStateCard(message = "AI 취향 분석을 시작하면 결과를 확인할 수 있습니다.")
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
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    recommendations.forEachIndexed { index, rec ->
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Movie,
                                                    contentDescription = "추천 작품",
                                                    tint = Color(0xFF4CAF50),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = rec.title,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = rec.reason,
                                                fontSize = 13.sp,
                                                color = Color(0xFF666666),
                                                lineHeight = 18.sp,
                                                modifier = Modifier.padding(start = 22.dp)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
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
