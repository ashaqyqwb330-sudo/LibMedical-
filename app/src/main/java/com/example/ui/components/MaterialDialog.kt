// @builder:file app/src/main/java/com/example/ui/components/MaterialDialog.kt
package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextGold
import com.example.ui.theme.Secondary

@Composable
fun MaterialDialog(
    title: String,
    message: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = TextGold,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(text = message, color = Color.White, fontSize = 15.sp)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = Secondary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = Color.White.copy(alpha = 0.6f))
            }
        },
        containerColor = Color(0xFF0F1F33)
    )
}
