package com.example.ostory.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WorkDetailScreen(
    workId: Int,
    workType: String,
    onNavigateToReviewWrite: (Int, String) -> Unit,
    onNavigateBack: () -> Unit
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
                text = "작품 상세 정보 화면 (Placeholder)",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "전달받은 작품 ID: $workId")
            Text(text = "전달받은 작품 유형: $workType")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onNavigateToReviewWrite(workId, workType) }) {
                Text("감상 기록 남기기")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onNavigateBack) {
                Text("뒤로 가기")
            }
        }
    }
}
