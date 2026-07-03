package com.example.deathnote.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deathnote.ui.theme.NoirPrimary
import com.example.deathnote.ui.theme.NoirSurface
import com.example.deathnote.ui.theme.NoirTextSecondary

@Composable
fun OnboardingScreen(
    onContinueAsGuest: () -> Unit = {},
    onSignInWithGoogle: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "DeathNote",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                ),
                color = NoirPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "WHAT DO YOU THINK OF THAT L",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = NoirTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = onSignInWithGoogle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text("Sign in with Google", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onContinueAsGuest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text("Continue as Guest", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
