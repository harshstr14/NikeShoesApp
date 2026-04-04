package com.example.nike

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.nike.screens.fonts
import com.example.nike.ui.theme.NikeTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private lateinit var auth: FirebaseAuth

class ForgetPasswordScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = 0xFFF8F9FA.toInt()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = 0xFFF8F9FA.toInt()
            )
        )

        setContent {
            NikeTheme {
                ForgetPassword_Screen()
            }
        }
    }
}

@Composable
private fun ForgetPassword_Screen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val (backInteraction, backScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var emailErrorText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.background(Color(0xFFF8F9FA)),
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 15.dp)
            ) { data ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Snackbar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(10.dp),
                                ambientColor = Color(0xFFFFFFFF),
                                spotColor = Color(0xFFFFFFFF)
                            ),
                        containerColor = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(9.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painter = painterResource(
                                when {
                                    data.visuals.message.contains("name") -> R.drawable.user_icon
                                    data.visuals.message.contains("email") -> R.drawable.email_icon
                                    data.visuals.message.contains("Email") -> R.drawable.email_icon
                                    data.visuals.message.contains("password") -> R.drawable.password_icon
                                    data.visuals.message.contains("Password") -> R.drawable.password_icon
                                    else -> {
                                        R.drawable.alert_icon
                                    }
                                }
                            ), contentDescription = "Icons",
                                tint = Color(0xFF5B9EE1), modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = data.visuals.message,
                                fontFamily = fonts,
                                fontWeight = FontWeight.SemiBold,
                                fontStyle = FontStyle.Normal,
                                fontSize = 13.sp,
                                color = Color(0xFF1A2530)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.padding(top = 15.dp, start = 20.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable(
                        interactionSource = backInteraction,
                        indication = null
                    ) {
                        activity?.finish()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_icon),
                    contentDescription = "Back Icon",
                    tint = Color(0xFF1A2530),
                    modifier = Modifier.size(20.dp)
                        .graphicsLayer {
                            scaleX = backScale
                            scaleY = backScale
                        }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 85.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Forget Password",
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Please Enter Your Email Address To \nReceive a Reset Password Link",
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF707B81),
                )

                Spacer(modifier = Modifier.height(45.dp))

                Text(
                    modifier = Modifier.align(Alignment.Start)
                        .padding(horizontal = 30.dp),
                    text = "Email Address", fontSize = 13.sp,
                    lineHeight = 15.sp, fontFamily = fonts,
                    fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530)
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 25.dp, vertical = 8.dp)
                        .height(52.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (emailError) Color.Red else Color.Transparent,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (inputField, placeholderText) = createRefs()

                        if (email.isEmpty()) {
                            Text(modifier = Modifier.constrainAs(placeholderText) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start, margin = 15.dp)
                                end.linkTo(parent.end, margin = 15.dp)
                                width = Dimension.fillToConstraints },
                                text = "Enter Email",
                                fontFamily = fonts,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                fontSize = 14.sp, lineHeight = 17.sp,
                                color = Color(0xFF707B81)
                            )
                        }

                        val selectionColors = TextSelectionColors(
                            handleColor = Color(0xFF1C1C1C),
                            backgroundColor = Color(0xFF1C1C1C).copy(alpha = 0.3f)
                        )

                        CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
                            BasicTextField(
                                value = email,
                                onValueChange = {
                                    email = it

                                    if (it.isNotBlank()) {
                                        emailError = false
                                    }
                                },
                                modifier = Modifier
                                    .constrainAs(inputField) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(parent.start, margin = 15.dp)
                                        end.linkTo(parent.end, margin = 15.dp)
                                        width = Dimension.fillToConstraints
                                    },
                                textStyle = TextStyle(
                                    fontFamily = fonts,
                                    fontWeight = FontWeight.SemiBold,
                                    fontStyle = FontStyle.Normal,
                                    fontSize = 14.sp, lineHeight = 17.sp,
                                    color = Color(0xFF707B81)
                                ),
                                singleLine = true,
                                cursorBrush = SolidColor(Color(0xFF1C1C1C))
                            )
                        }
                    }
                }

                if (emailError) {
                    Text(
                        modifier = Modifier.align(Alignment.Start)
                            .padding(horizontal = 30.dp),
                        text = emailErrorText,
                        color = Color.Red,
                        fontSize = 12.sp,
                        lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 25.dp)
                        .height(52.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF5B9EE1))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            emailError = email.isBlank()
                            emailErrorText = if (email.isBlank()) "Please enter email" else ""

                            if (emailErrorText.isNotEmpty()) return@clickable

                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailErrorText = "Enter a valid email"
                                return@clickable
                            }

                            emailErrorText = ""

                            if (!emailError) {
                                auth.sendPasswordResetEmail(email.trim()).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("RecoveryPassword", "Password reset email sent")
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = "Password reset email sent",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else {
                                        Log.e("RecoveryPassword", "Failed", task.exception)
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = task.exception?.localizedMessage ?: "Something went wrong",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 15.sp, lineHeight = 16.sp,
                        fontFamily = fonts, fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal, color = Color(0xFFF8F9FA)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ForgetPasswordScreenPreview() {
    NikeTheme {
        ForgetPassword_Screen()
    }
}