package com.ccs.fish.scan.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ccs.fish.scan.R
import com.ccs.fish.scan.utils.Navigation

@Composable
fun MainMenuScreen(mainMenuNavController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        MainMenuScreenImage()
        Spacer(modifier = Modifier.size(32.dp))
        MainMenuScreenButtons(mainMenuNavController)
    }
}

@Composable
fun MainMenuScreenImage() {
    Image(
        painter = painterResource(id = R.drawable.fishscan_text),
        contentDescription = "Logo",
    )
}

@Composable
fun MainMenuScreenButton(title: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .width(200.dp)
            .padding(bottom = 8.dp)
            .shadow(
                elevation = 5.dp,
                shape = MaterialTheme.shapes.extraLarge
            ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Text(
            text = title.uppercase(),
            color = Color.hsv(226f, 0.67f, 0.43f, 1f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MainMenuScreenButtons(mainMenuNavController: NavController) {
    Column() {
        MainMenuScreenButton(
            "Scan",
            onClick = { mainMenuNavController.navigate(Navigation.Scan.route) })
        MainMenuScreenButton(
            "Instruction",
            onClick = { mainMenuNavController.navigate(Navigation.Instruction.route) })
        MainMenuScreenButton(
            "Gallery",
            onClick = { mainMenuNavController.navigate(Navigation.Gallery.route) })
        MainMenuScreenButton(
            "History",
            onClick = { mainMenuNavController.navigate(Navigation.History.route) })
    }
}