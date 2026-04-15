package com.veltrix.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CustomCredential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.veltrix.R
import com.veltrix.ui.components.GlassCard
import com.veltrix.ui.components.GlowBar
import com.veltrix.ui.components.pressScale
import com.veltrix.ui.components.rememberPressInteractionSource
import com.veltrix.ui.theme.springDampingRatio
import com.veltrix.ui.theme.springStiffness
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    email: String,
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onEmailLoginClick: () -> Unit,
    onGoogleTokenReceived: (String) -> Unit
) {
    val introScale = remember { Animatable(0.92f) }
    val introAlpha = remember { Animatable(0f) }
    val primaryButtonInteraction = rememberPressInteractionSource()
    val emailButtonInteraction = rememberPressInteractionSource()
    val context = LocalContext.current
    val googleWebClientId = context.getString(R.string.google_web_client_id)
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val shimmer = rememberInfiniteTransition(label = "login-shimmer")
    val orbAlpha by shimmer.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb-alpha"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.98f else 1f,
        animationSpec = spring(stiffness = springStiffness, dampingRatio = springDampingRatio),
        label = "login-button-scale"
    )

    LaunchedEffect(Unit) {
        introAlpha.animateTo(1f, tween(550))
        introScale.animateTo(1f, spring(stiffness = springStiffness, dampingRatio = springDampingRatio))
    }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 72.dp, start = 8.dp)
                .size(180.dp)
                .alpha(orbAlpha * 0.7f)
                .blur(110.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF6366F1), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 140.dp)
                .size(150.dp)
                .alpha(0.12f)
                .blur(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF10B981), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .alpha(introAlpha.value)
                .scale(introScale.value),
            verticalArrangement = Arrangement.Center
        ) {
            BrandHeader(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 14.dp)
            )
            LoginBadge()
            Text(
                text = "Welcome Back,",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Good to see you again. Sign in to continue to your staff management workspace.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f),
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 32.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Use your Firebase email and password to access the dashboard.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.56f)
                    )
                    LoginInfoStrip()

                    LoginField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = "Work Email",
                        placeholder = "Email address",
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    LoginField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = "Password",
                        placeholder = "Password",
                        isPassword = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    AnimatedVisibility(
                        visible = !errorMessage.isNullOrBlank(),
                        enter = fadeIn() + slideInVertically { -it / 2 },
                        exit = fadeOut() + slideOutVertically { -it / 2 }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFF43F5E).copy(alpha = 0.14f))
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = errorMessage.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFFC4CE)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(buttonScale)
                            .pressScale(primaryButtonInteraction)
                    ) {
                        GlowBar(
                            modifier = Modifier
                                .matchParentSize()
                                .blur(26.dp),
                            color = Color(0xFF6366F1).copy(alpha = 0.7f)
                        )
                        Button(
                            onClick = {
                                if (googleWebClientId.isBlank()) {
                                    if (email.isNotBlank() && password.isNotBlank()) onEmailLoginClick()
                                    else Toast.makeText(context, "Authentication fail", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                coroutineScope.launch {
                                    try {
                                        val request = GetCredentialRequest.Builder()
                                            .addCredentialOption(
                                                GetSignInWithGoogleOption.Builder(googleWebClientId).build()
                                            )
                                            .build()

                                        val result = credentialManager.getCredential(
                                            context = context,
                                            request = request
                                        )

                                        val credential = result.credential
                                        if (credential is CustomCredential &&
                                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                        ) {
                                            val googleIdTokenCredential = GoogleIdTokenCredential
                                                .createFrom(credential.data)
                                            onGoogleTokenReceived(googleIdTokenCredential.idToken)
                                        } else {
                                            if (email.isNotBlank() && password.isNotBlank()) onEmailLoginClick()
                                            else Toast.makeText(context, "Authentication fail", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (_: GoogleIdTokenParsingException) {
                                        if (email.isNotBlank() && password.isNotBlank()) onEmailLoginClick()
                                        else Toast.makeText(context, "Authentication fail", Toast.LENGTH_SHORT).show()
                                    } catch (_: GetCredentialException) {
                                        if (email.isNotBlank() && password.isNotBlank()) onEmailLoginClick()
                                        else Toast.makeText(context, "Authentication fail", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF111827),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF111827).copy(alpha = 0.92f),
                                disabledContentColor = Color.White.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF6366F1),
                                            Color(0xFF312E81)
                                        )
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            interactionSource = primaryButtonInteraction
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.google_logo),
                                        contentDescription = "Google logo",
                                        modifier = Modifier.size(20.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Text("Continue with Google", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = onEmailLoginClick,
                        enabled = !isLoading,
                        modifier = Modifier
                            .align(Alignment.End)
                            .pressScale(emailButtonInteraction),
                        interactionSource = emailButtonInteraction
                    ) {
                        Text(
                            text = "Use email & password",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.76f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF6366F1).copy(alpha = 0.16f))
            .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Bolt,
            contentDescription = null,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "Secure staff workspace",
            color = Color.White.copy(alpha = 0.84f),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun LoginInfoStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF6366F1).copy(alpha = 0.10f),
                        Color(0xFF10B981).copy(alpha = 0.07f),
                        Color.Transparent
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981).copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Admin access",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Protected sign-in flow",
                    color = Color.White.copy(alpha = 0.52f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        Text(
            text = "v1.0",
            color = Color.White.copy(alpha = 0.46f),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun BrandHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 112.dp, height = 74.dp)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.veltrix),
            contentDescription = "Veltrix logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.48f)
            )
        },
        singleLine = true,
        leadingIcon = leadingIcon,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        } else {
            null
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF0B1220).copy(alpha = 0.94f),
            unfocusedContainerColor = Color(0xFF0B1220).copy(alpha = 0.88f),
            focusedBorderColor = Color(0xFF6366F1).copy(alpha = 0.42f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.56f),
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.56f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f),
            focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.48f),
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.48f)
        ),
        modifier = modifier.fillMaxWidth()
    )
}
