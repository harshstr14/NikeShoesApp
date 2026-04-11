package com.example.nike.profileScreen

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.example.nike.R
import com.example.nike.homeScreen.user.UserViewModel
import com.example.nike.navigation.BottomNavRoute
import com.example.nike.pressScale
import com.example.nike.screens.fonts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

object ProfilePrefs {
    val Context.dataStore by preferencesDataStore("profile")
    val PROFILE_URL = stringPreferencesKey("profile_url")
    val USER_NAME = stringPreferencesKey("user_name")

    suspend fun saveProfileUrl(context: Context, url: String) {
        context.dataStore.edit {
            it[PROFILE_URL] = url
        }
    }

    suspend fun saveUserName(context: Context, name: String) {
        context.dataStore.edit {
            it[USER_NAME] = name
        }
    }

    fun getProfileUrl(context: Context) =
        context.dataStore.data.map {
            it[PROFILE_URL]
        }

    fun getUserName(context: Context) =
        context.dataStore.data.map {
            it[USER_NAME]
        }

    suspend fun clear(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
@Composable
fun ProfileScreen(
    navController: NavHostController, snackBarHostState: SnackbarHostState,
    viewModel: UserViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val (backInteraction, backScale) = pressScale()
    val (editInteraction, editScale) = pressScale()
    val (cameraInteraction, cameraScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userProfile by viewModel.userProfileState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val imageUrl by profileViewModel.profileImageUrl.collectAsStateWithLifecycle()
    val isUploading by profileViewModel.isUploading.collectAsState()

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        uid?.let { profileViewModel.silentRefresh(it) }
    }

    LaunchedEffect(uiState) {
        uiState?.let {
            snackBarHostState.showSnackbar(it)
        }
    }

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let { uri ->
                // Upload to Cloudinary and save URL
                val database = FirebaseDatabase.getInstance().getReference("Users")
                uploadToCloudinary(
                    uri, database,
                    profileViewModel,
                    onShowMessage = { message ->
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = message,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { sourceUri ->
            val destinationUri = Uri.fromFile(
                File(context.cacheDir, "crop_${System.currentTimeMillis()}.jpg")
            )
            val options = UCrop.Options().apply {
                setCircleDimmedLayer(true)
                setShowCropFrame(false)
                setShowCropGrid(false)
            }
            val intent = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(512, 512)
                .withOptions(options)
                .getIntent(context)
            cropLauncher.launch(intent)
        }
    }

    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var nameErrorText by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var emailErrorText by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf(false) }
    var phoneNumberErrorText by remember { mutableStateOf("") }

    var showReAuthDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(userProfile) {
        if (name.isEmpty() && email.isEmpty() && phoneNumber.isEmpty()) {
            userProfile?.let {
                name = it.name
                email = it.email
                phoneNumber = it.phone
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {

        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A2530).copy(alpha = 0.4f))
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes (R.raw.nike_logo_animation)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(140.dp)
                        .clip(RectangleShape),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .size(140.dp)
                            .graphicsLayer {
                                scaleX = 1.2f
                                scaleY = 1.2f
                            }
                    )
                }
            }
        }
        Box(
            modifier = Modifier.padding(top = 15.dp, start = 20.dp)
                .size(44.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFFFFFF))
                .clickable(
                    interactionSource = backInteraction,
                    indication = null
                ) {
                    navController.navigate(BottomNavRoute.Home.route) {
                        popUpTo(BottomNavRoute.Home.route)
                        launchSingleTop = true
                    }
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

        Text(
            modifier = Modifier
                .padding(top = 26.dp)
                .align(Alignment.TopCenter),
            text = "Profile",
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal,
            color = Color(0xFF1A2530),
        )

        Icon(
            painter = painterResource(R.drawable.edit_icon),
            contentDescription = "Edit Icon",
            tint = Color(0xFF5B9EE1),
            modifier = Modifier
                .padding(top = 26.dp, end = 30.dp)
                .size(24.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer {
                    scaleX = editScale
                    scaleY = editScale
                }
                .clickable(
                    interactionSource = editInteraction,
                    indication = null
                ) {

                }
        )

        AsyncImage(
            model = imageUrl,
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.logo),
            error = painterResource(R.drawable.logo),
            modifier = Modifier
                .padding(top = 95.dp)
                .align(Alignment.TopCenter)
                .size(90.dp)
                .clip(CircleShape)

        )

        Text(
            modifier = Modifier
                .padding(top = 200.dp)
                .align(Alignment.TopCenter),
            text = name,
            fontSize = 20.sp,
            lineHeight = 22.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal,
            color = Color(0xFF1A2530),
        )

        Box(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 172.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF5B9EE1))
                .clickable(
                    interactionSource = cameraInteraction,
                    indication = null
                ) {
                    imagePickerLauncher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_icon),
                contentDescription = "Camera Icon",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        scaleX = cameraScale
                        scaleY = cameraScale
                    }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 260.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    .background(
                        Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (inputField, placeholderText) = createRefs()

                    if (name.isEmpty()) {
                        Text(
                            modifier = Modifier.constrainAs(placeholderText) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start, margin = 15.dp)
                                end.linkTo(parent.end, margin = 15.dp)
                                width = Dimension.fillToConstraints
                            },
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
                                fontWeight = FontWeight.Normal,
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
                    lineHeight = 14.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal
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
                    .background(
                        Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (inputField, placeholderText) = createRefs()

                    if (email.isEmpty()) {
                        Text(
                            modifier = Modifier.constrainAs(placeholderText) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start, margin = 15.dp)
                                end.linkTo(parent.end, margin = 15.dp)
                                width = Dimension.fillToConstraints
                            },
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
                                fontWeight = FontWeight.Normal,
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
                    lineHeight = 14.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                modifier = Modifier.align(Alignment.Start)
                    .padding(horizontal = 30.dp),
                text = "Phone Number", fontSize = 13.sp,
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
                        color = if (phoneNumberError) Color.Red else Color.Transparent,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .background(
                        Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (inputField, placeholderText) = createRefs()

                    if (phoneNumber.isEmpty()) {
                        Text(
                            modifier = Modifier.constrainAs(placeholderText) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start, margin = 15.dp)
                                end.linkTo(parent.end, margin = 15.dp)
                                width = Dimension.fillToConstraints
                            },
                            text = "Enter Phone Number",
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
                            value = phoneNumber,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    phoneNumber = it
                                    phoneNumberError = false
                                }

                                if (it.isNotBlank()) {
                                    phoneNumberError = false
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
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                fontSize = 14.sp, lineHeight = 17.sp,
                                color = Color(0xFF707B81)
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(Color(0xFF1C1C1C)),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            )
                        )
                    }
                }
            }

            if (phoneNumberError) {
                Text(
                    modifier = Modifier.align(Alignment.Start)
                        .padding(horizontal = 30.dp),
                    text = phoneNumberErrorText,
                    color = Color.Red,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal
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
                        nameError = false
                        emailError = false
                        phoneNumberError = false

                        nameErrorText = ""
                        emailErrorText = ""
                        phoneNumberErrorText = ""

                        if (name.isBlank()) {
                            nameError = true
                            nameErrorText = "Please enter name"
                        }

                        if (email.isBlank()) {
                            emailError = true
                            emailErrorText = "Please enter email"
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = true
                            emailErrorText = "Enter a valid email"
                        }

                        if (phoneNumber.isBlank()) {
                            phoneNumberError = true
                            phoneNumberErrorText = "Please enter phone number"
                        } else if (phoneNumber.length < 10) {
                            phoneNumberError = true
                            phoneNumberErrorText = "Enter valid phone number"
                        }

                        if (nameError || emailError || phoneNumberError) return@clickable

                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@clickable

                        val oldEmail = userProfile?.email?.trim() ?: ""
                        val newEmail = email.trim()

                        val updatedProfile = UserProfile(
                            name = name.trim(),
                            email = newEmail,
                            phone = phoneNumber.trim(),
                            profileImageUrl = userProfile?.profileImageUrl ?: ""
                        )

                        if (newEmail != oldEmail) {
                            profileViewModel.updateEmailInAuth(
                                newEmail = newEmail,
                                onSuccess = {
                                    viewModel.updateProfile(updatedProfile)
                                },
                                onError = { message ->
                                    if (message.contains("requires recent authentication")) {
                                        showReAuthDialog = true
                                    } else {
                                        emailErrorText = message
                                        emailError = true
                                    }
                                }
                            )

                        } else {
                            viewModel.updateProfile(updatedProfile)
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Update Profile",
                    fontSize = 15.sp, lineHeight = 16.sp,
                    fontFamily = fonts, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal, color = Color(0xFFF8F9FA)
                )
            }
        }

        if (showReAuthDialog) {
            IOSStyleBottomDialog(
                title = "Re-authentication Required",
                message = "For security reasons, please enter your password to update your email.",
                password = password,
                onPasswordChange = {
                    password = it
                },
                confirmText = "Continue",
                dismissText = "Cancel",
                onConfirm = {
                    val user = FirebaseAuth.getInstance().currentUser
                    val oldEmail = userProfile?.email ?: ""
                    Log.d("AUTH", "Email: ${user?.email} $oldEmail")
                    FirebaseAuth.getInstance().currentUser?.providerData?.forEach {
                        Log.d("AUTH_PROVIDER", it.providerId)
                    }

                    profileViewModel.reAuthenticateAndUpdateEmail(
                        password = password,
                        newEmail = email.trim(),
                        onSuccess = {
                            showReAuthDialog = false
                            password = ""

                            val updatedProfile = UserProfile(
                                name = name.trim(),
                                email = email.trim(),
                                phone = phoneNumber.trim(),
                                profileImageUrl = userProfile?.profileImageUrl ?: ""
                            )

                            viewModel.updateProfile(updatedProfile)
                        },
                        onError = {
                            emailErrorText = it
                            emailError = true
                        }
                    )
                },
                onDismiss = {
                    showReAuthDialog = false
                }
            )
        }
    }
}

@Composable
fun IOSStyleBottomDialog(
    title: String,
    message: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordErrorText by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 22.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF8F9FA),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = title,
                        fontFamily = fonts,
                        fontSize = 17.sp, lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = message,
                        fontFamily = fonts,
                        fontSize = 13.sp, lineHeight = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF707B81)
                    )


                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        modifier = Modifier.align(Alignment.Start),
                        text = "Password", fontSize = 13.sp,
                        lineHeight = 15.sp, fontFamily = fonts,
                        fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Box(modifier = Modifier.padding(vertical = 8.dp)
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
                                        onPasswordChange(it)

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
                            modifier = Modifier.align(Alignment.Start),
                            text = passwordErrorText,
                            color = Color.Red,
                            fontSize = 12.sp,
                            lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF707B81).copy(alpha = 0.2f))
                                .clickable { onDismiss() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dismissText,
                                color = Color(0xFF707B81),
                                fontSize = 15.sp, lineHeight = 15.sp,
                                fontFamily = fonts, fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF5B9EE1))
                                .clickable {
                                    keyboardController?.hide()
                                    passwordError = password.isBlank()
                                    passwordErrorText = if (password.isBlank()) "Please enter password" else ""

                                    if (passwordErrorText.isNotEmpty()) return@clickable

                                    passwordErrorText = ""

                                    onConfirm() }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = confirmText,
                                fontSize = 15.sp, lineHeight = 15.sp,
                                fontFamily = fonts, fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal,
                                color = Color(0xFFFFFFFF)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun uploadToCloudinary(
    imageUri: Uri,
    database: DatabaseReference,
    profileViewModel: ProfileViewModel,
    onShowMessage: (String) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    profileViewModel.setUploading(true)

    MediaManager.get().upload(imageUri)
        .option("folder", "profile_pics")
        .option("public_id", userId)
        .option("overwrite", true)
        .callback(object : UploadCallback {
            override fun onStart(requestId: String?) {
                Log.d("UPLOAD_DEBUG", "Upload started")
                profileViewModel.setUploading(true)
                profileViewModel.updateProgress(0f)
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                val progress = bytes.toFloat() / totalBytes.toFloat()
                Log.d("UPLOAD_DEBUG", "Progress: $progress")
                profileViewModel.updateProgress(progress)
            }

            override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                profileViewModel.setUploading(false)

                val secureUrl = resultData?.get("secure_url").toString()
                val version = resultData?.get("version").toString()

                val finalUrl = "$secureUrl?v=$version"

                // Save URL in Firebase Realtime Database
                database.child(userId).child("Profile ImageUrl").setValue(finalUrl)
                    .addOnSuccessListener {
                        onShowMessage("Profile photo updated")

                        profileViewModel.refreshProfileImage(userId)
                    }
                    .addOnFailureListener { e ->
                        onShowMessage("Failed to update profile: ${e.message}")
                    }
            }

            override fun onError(requestId: String, p1: com.cloudinary.android.callback.ErrorInfo) {
                profileViewModel.setUploading(false)
                profileViewModel.resetProgress()

                onShowMessage("Upload failed: ${p1.description}")
            }

            override fun onReschedule(requestId: String, p1: com.cloudinary.android.callback.ErrorInfo) {
                // You can leave this empty if you don’t need it
            }
        })
        .dispatch()
}

@Composable
@Preview(showSystemUi = true)
private fun ProfileScreenPreview() {
    val snackBarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    ProfileScreen(navController, snackBarHostState)
}