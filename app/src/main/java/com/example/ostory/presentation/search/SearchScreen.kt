package com.example.ostory.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen(
    onNavigateToDetail: (Int, String) -> Unit
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
                text = "작품 검색 화면 (Placeholder)",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onNavigateToDetail(550, "MOVIE") }) {
                Text("임시 영화 상세 보기 (ID: 550, Fight Club)")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onNavigateToDetail(1399, "DRAMA") }) {
                Text("임시 드라마 상세 보기 (ID: 1399, Game of Thrones)")
            }
        }
    }
}
