package com.probasketacademy.presentacion.iniciodesesion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.R
import com.probasketacademy.ui.theme.BorderColor
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LogoHeaderSection()
                    Spacer(modifier = Modifier.height(24.dp))
                    GoogleSignInButton(
                        onClick = { onEvent(AuthEvent.OnGoogleSignInClicked(context)) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    DividerWithText()
                    Spacer(modifier = Modifier.height(20.dp))
                    EmailInputField(
                        email = state.email,
                        onEmailChanged = { onEvent(AuthEvent.OnEmailChanged(it)) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PasswordInputField(
                        password = state.password,
                        isPasswordVisible = state.isPasswordVisible,
                        onPasswordChanged = { onEvent(AuthEvent.OnPasswordChanged(it)) },
                        onToggleVisibility = { onEvent(AuthEvent.OnTogglePasswordVisibility) },
                        onForgotPasswordClicked = { onEvent(AuthEvent.OnForgotPasswordClicked) }
                    )

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    SubmitButton(
                        isLoading = state.isLoading,
                        onClick = { onEvent(AuthEvent.OnLoginClicked) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
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
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
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
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Continuar con Google",
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DividerWithText() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BorderColor
        )
        Text(
            text = "O CON TU CORREO",
            modifier = Modifier.padding(horizontal = 12.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BorderColor
        )
    }
}

@Composable
private fun EmailInputField(
    email: String,
    onEmailChanged: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "CORREO ELECTRÓNICO",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = TextMuted
                )
            },
            placeholder = {
                Text(text = "admin@basketacademy.com", color = TextMuted, fontSize = 14.sp)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderColor,
                focusedBorderColor = PrimaryOrange
            )
        )
    }
}

@Composable
private fun PasswordInputField(
    password: String,
    isPasswordVisible: Boolean,
    onPasswordChanged: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onForgotPasswordClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONTRASEÑA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 12.sp,
                color = PrimaryOrange,
                modifier = Modifier.clickable { onForgotPasswordClicked() }
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = TextMuted
                )
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Mostrar u ocultar contraseña",
                        tint = TextMuted
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderColor,
                focusedBorderColor = PrimaryOrange
            )
        )
    }
}

@Composable
private fun SubmitButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Acceder al Sistema",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
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