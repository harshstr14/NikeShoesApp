package com.example.nike

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.nike.screens.fonts
import com.example.nike.ui.theme.NikeTheme
import com.example.nike.googleAuthentication.GoogleSignInManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

private lateinit var auth: FirebaseAuth
private lateinit var database: DatabaseReference

class SignUpScreen : ComponentActivity() {
    private lateinit var googleSignInManager: GoogleSignInManager

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            googleSignInManager.handleSignInResult(
                it,
                onSuccess = { auth ->
                    Toast.makeText(this,"Welcome ${auth.currentUser?.displayName}",Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, MainScreen::class.java).apply {
                            flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                },
                onError = { message ->
                    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                }
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleSignInManager = GoogleSignInManager(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference()

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
                SignUp_Screen(onGoogleSignIn = {
                    googleSignInManager.signIn(launcher)
                })
            }
        }
    }
}

@Composable
private fun SignUp_Screen(onGoogleSignIn: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val (backInteraction, backScale) = pressScale()
    val (signUpInteraction, signUpScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }

    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var nameErrorText by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var emailErrorText by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var passwordErrorText by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

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
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .size(15.dp)
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
                    text = "Create Account",
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
                    text = "Let’s Create Account Together",
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF707B81),
                )

                Spacer(modifier = Modifier.height(55.dp))

                Text(
                    modifier = Modifier.align(Alignment.Start)
                        .padding(horizontal = 30.dp),
                    text = "Name", fontSize = 13.sp,
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
                            color = if (nameError) Color.Red else Color.Transparent,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (inputField, placeholderText) = createRefs()

                        if (name.isEmpty()) {
                            Text(modifier = Modifier.constrainAs(placeholderText) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start, margin = 15.dp)
                                end.linkTo(parent.end, margin = 15.dp)
                                width = Dimension.fillToConstraints },
                                text = "Enter Name",
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
                                value = name,
                                onValueChange = {
                                    name = it

                                    if (it.isNotBlank()) {
                                        nameError = false
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

                if (nameError) {
                    Text(
                        modifier = Modifier.align(Alignment.Start)
                            .padding(horizontal = 30.dp),
                        text = nameErrorText,
                        color = Color.Red,
                        fontSize = 12.sp,
                        lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

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

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    modifier = Modifier.align(Alignment.Start)
                        .padding(horizontal = 30.dp),
                    text = "Password", fontSize = 13.sp,
                    lineHeight = 15.sp, fontFamily = fonts,
                    fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530)
                )

                Box(modifier = Modifier.padding(horizontal = 25.dp, vertical = 8.dp)
                    .height(52.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (passwordError) Color.Red else Color.Transparent,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .background(Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (inputField, placeholderText, toggleIcon) = createRefs()

                        if (password.isEmpty()) {
                            Text(modifier = Modifier.constrainAs(placeholderText) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start, margin = 15.dp)
                                end.linkTo(parent.end, margin = 15.dp)
                                width = Dimension.fillToConstraints },
                                text = "Enter Password",
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
                                value = password,
                                onValueChange = {
                                    password = it

                                    if (it.isNotBlank()) {
                                        passwordError = false
                                    }
                                },
                                modifier = Modifier
                                    .constrainAs(inputField) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(parent.start, margin = 15.dp)
                                        end.linkTo(toggleIcon.start, margin = 15.dp)
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
                                cursorBrush = SolidColor(Color(0xFF1C1C1C)),
                                visualTransformation = if (isPasswordVisible)
                                    VisualTransformation.None
                                else
                                    PasswordVisualTransformation()
                            )
                        }
                        Icon(
                            painter = painterResource(
                                if (isPasswordVisible)
                                    R.drawable.eye_open_icon
                                else
                                    R.drawable.eye_closed_icon
                            ),
                            contentDescription = "Toggle password",
                            tint = Color(0xFF1A2530),
                            modifier = Modifier
                                .size(20.dp)
                                .constrainAs(toggleIcon) {
                                    end.linkTo(parent.end, margin = 15.dp)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                }
                                .clickable {
                                    isPasswordVisible = !isPasswordVisible
                                }
                        )
                    }
                }

                if (passwordError) {
                    Text(
                        modifier = Modifier.align(Alignment.Start)
                            .padding(horizontal = 30.dp),
                        text = passwordErrorText,
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
                            nameError = name.isBlank()
                            emailError = email.isBlank()
                            passwordError = password.isBlank()

                            nameErrorText = if (name.isBlank()) "Please enter name" else ""
                            emailErrorText = if (email.isBlank()) "Please enter email" else ""
                            passwordErrorText = if (password.isBlank()) "Please enter password" else ""

                            if (nameErrorText.isNotEmpty() || emailErrorText.isNotEmpty() || passwordErrorText.isNotEmpty()) return@clickable

                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailErrorText = "Enter a valid email"
                                return@clickable
                            }

                            nameErrorText = ""
                            emailErrorText = ""
                            passwordErrorText = ""

                            auth.createUserWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userID = auth.currentUser?.uid
                                    val userData = mapOf(
                                        "name" to name,
                                        "email" to email,
                                    )
                                    if (userID != null) {
                                        database.child("Users").child(userID).setValue(userData).addOnSuccessListener {
//                                            pref.edit { putBoolean("isLoggedIn", true) }
//                                            val intent = Intent(this, Home::class.java)
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                                            startActivity(intent)
//                                            finish()
                                        }.addOnFailureListener { e ->
                                            Log.e("FirebaseDB", "Failed to save user data: ${e.message}", e)
                                            scope.launch {
                                                snackBarHostState.showSnackbar(
                                                    message = "TCould not save user info. Please try again.",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    } else {
                                        Log.e("Auth", "UID is null after successful registration")
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = "Something went wrong. Please try again.",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                } else {
                                    try {
                                        throw task.exception!!
                                    } catch (e: FirebaseAuthUserCollisionException) {
                                        // Email already in use
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = "This email is already registered",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        Log.e("Auth", "Email : ${e.message}")
                                    } catch (e: FirebaseAuthWeakPasswordException) {
                                        // Weak password
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = "Password is too weak. Use at least 6 characters",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        Log.e("Auth", "Password : ${e.message}")
                                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                                        // Invalid email format
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = "Invalid email format",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        Log.e("Auth", "Email Format : ${e.message}")
                                    } catch (e: Exception) {
                                        // Other errors
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = "Sign-up failed: ${e.message}",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        Log.e("Auth", "Other : ${e.message}")
                                    }
                                }
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 15.sp, lineHeight = 16.sp,
                        fontFamily = fonts, fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal, color = Color(0xFFF8F9FA)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 25.dp)
                        .height(52.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFFFFFFF))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onGoogleSignIn()
                            keyboardController?.hide()
                        }
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row {
                        Icon(painter = painterResource(R.drawable.google_icon), contentDescription = "Google Icon", tint = Color.Unspecified)

                        Spacer(modifier = Modifier.width(7.dp))

                        Text(
                            text = "Sign in with google", fontSize = 14.sp,
                            fontFamily = fonts, fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Normal,
                            color = Color(0xFF1A2530)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(45.dp))

                Row (modifier = Modifier.padding(bottom = 45.dp)) {
                    Text(
                        text = "Already have an account?" , fontSize = 12.sp,
                        fontFamily = fonts, fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF707B81),
                        textAlign = TextAlign.Center, lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Text(
                        text = "Sign in", fontSize = 12.sp,
                        fontFamily = fonts, fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF5B9EE1),
                        textAlign = TextAlign.Center, lineHeight = 15.sp, modifier = Modifier
                            .clickable(
                                interactionSource = signUpInteraction,
                                indication = null
                            ) {
                                val intent = Intent(context, SignInScreen::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }
                                context.startActivity(intent)
                            }
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    NikeTheme {
        SignUp_Screen(onGoogleSignIn = { })
    }
}