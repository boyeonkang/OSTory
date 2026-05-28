package com.example.ostory.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalendarScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToReviewDetail: (Int) -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "캘린더 홈 화면 (Placeholder)",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToSearch) {
                Text("작품 검색하러 가기")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onNavigateToReviewDetail(1) }) {
                Text("임시 감상 기록 상세 보기 (ID: 1)")
            }
        }
    }
}
