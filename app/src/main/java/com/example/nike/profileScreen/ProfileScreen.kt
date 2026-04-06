package com.example.nike.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nike.R
import com.example.nike.navigation.BottomNavRoute
import com.example.nike.pressScale
import com.example.nike.screens.fonts

@Composable
fun ProfileScreen(navController: NavHostController) {
    val (backInteraction, backScale) = pressScale()
    val (editInteraction, editScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }

    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var nameErrorText by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var emailErrorText by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf(false) }
    var phoneNumberErrorText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF8F9FA))
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

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
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
            text = "Alisson Becker",
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
                painter = painterResource(R.drawable.camera_icon),
                contentDescription = "Back Icon",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        scaleX = backScale
                        scaleY = backScale
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
                                fontWeight = FontWeight.SemiBold,
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
                        nameError = name.isBlank()
                        emailError = email.isBlank()
                        phoneNumberError = phoneNumber.isBlank()

                        nameErrorText = if (name.isBlank()) "Please enter name" else ""
                        emailErrorText = if (email.isBlank()) "Please enter email" else ""
                        phoneNumberErrorText = if (phoneNumber.isBlank()) "Please phone number" else ""

                        if (nameErrorText.isNotEmpty() || emailErrorText.isNotEmpty() || phoneNumberErrorText.isNotEmpty()) return@clickable

                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailErrorText = "Enter a valid email"
                            return@clickable
                        }

                        nameErrorText = ""
                        emailErrorText = ""
                        phoneNumberErrorText = ""

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
    }
}

@Composable
@Preview(showSystemUi = true)
private fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController)
}