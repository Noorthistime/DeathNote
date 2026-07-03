package com.example.deathnote.ui.security

import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.deathnote.ui.theme.NoirPrimary
import com.example.deathnote.ui.theme.NoirSurface
import com.example.deathnote.ui.theme.NoirTextSecondary
import java.util.concurrent.Executors

object SecuritySession {
    val unlockedIds = mutableSetOf<String>()
}

@Composable
fun SecurityScreen(
    onAuthenticated: (String?) -> Unit = {},
    onBack: () -> Unit = {}
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    var passwordInput by remember { mutableStateOf("") }
    var showPasswordField by remember { mutableStateOf(false) }

    fun showBiometricPrompt() {
        val activity = context as? FragmentActivity ?: return
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    activity.runOnUiThread { onAuthenticated(null) } // null means biometric success
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("DeathNote Authentication")
            .setSubtitle("Access restricted archives")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    LaunchedEffect(Unit) {
        showBiometricPrompt()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "DEATHNOTE",
                style = MaterialTheme.typography.titleLarge,
                color = NoirPrimary,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                "Access Restricted",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "this notebook is password protected.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = NoirTextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (showPasswordField) {
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    placeholder = { Text("Enter Password", color = NoirTextSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    visualTransformation = VisualTransformation.None,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedBorderColor = NoirTextSecondary.copy(alpha = 0.3f),
                        focusedBorderColor = NoirPrimary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onAuthenticated(passwordInput) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("UNLOCK", fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = { showPasswordField = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    border = BorderStroke(1.dp, NoirTextSecondary.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Key, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Use Password")
                }
            }
        }
    }
}
