package com.probasketacademy.presentacion.iniciodesesion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.R
import com.probasketacademy.ui.theme.CardBackground
import com.probasketacademy.ui.theme.GoogleBlue
import com.probasketacademy.ui.theme.LightBackgroundAuth
import com.probasketacademy.ui.theme.PrimaryOrange
import com.probasketacademy.ui.theme.TextDark
import com.probasketacademy.ui.theme.TextMuted

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onLoginSuccess()
        }
    }

    AuthContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
fun AuthContent(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightBackgroundAuth)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LogoHeaderSection()

                    Spacer(modifier = Modifier.height(48.dp))

                    if (state.isLoading) {
                        CircularProgressIndicator(color = PrimaryOrange)
                    } else {
                        GoogleSignInButton(
                            onClick = { onEvent(AuthEvent.OnGoogleSignInClicked(context)) }
                        )
                    }

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            FooterSection()
        }
    }
}

@Composable
private fun LogoHeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.logo_probasket),
            contentDescription = "Logo ProBasketAcademy",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = PrimaryOrange, fontWeight = FontWeight.Bold)) {
                    append("Pro ")
                }
                withStyle(style = SpanStyle(color = TextDark, fontWeight = FontWeight.Bold)) {
                    append("BasketAcademy")
                }
            },
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Bienvenido a ProBasketAcademy",
            fontSize = 13.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "G",
                fontWeight = FontWeight.Bold,
                color = GoogleBlue,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Continuar con Google",
                color = TextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FooterSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Términos y Condiciones",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.clickable { }
            )
            Text(
                text = "  •  ",
                fontSize = 12.sp,
                color = TextMuted
            )
            Text(
                text = "Política de Privacidad",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.clickable { }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "© 2026 BasketAcademy Pro System",
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    AuthContent(
        state = AuthState(),
        onEvent = {}
    )
}