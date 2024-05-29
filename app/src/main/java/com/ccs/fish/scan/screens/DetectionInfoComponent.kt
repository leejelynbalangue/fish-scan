package com.ccs.fish.scan.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DetectionInfoComponent(
    isShowValues: Boolean = true,
    ladyfishCount: Int,
    milkfishCount: Int,
    endTime: Long
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "ladyfish: ${if (isShowValues) ladyfishCount else "stopped"}",
                color = Color.White
            )
            Text(
                text = "milkfish: ${if (isShowValues) milkfishCount else "stopped"}",
                color = Color.White
            )
            Text(
                text = "inference: ${if (isShowValues) "$endTime ms" else "stopped"}",
                color = Color.White
            )
        }
        Column {
            Column {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(
                                BorderStroke(2.dp, Color.Red),
                                shape = RoundedCornerShape(0.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ladyfish", color = Color.Red)
                }

                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(
                                BorderStroke(2.dp, Color.Green),
                                shape = RoundedCornerShape(0.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "milkfish", color = Color.Green)
                }

            }
        }
    }

}