package com.example.nike.checkoutScreen

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nike.R
import com.example.nike.cartScreen.DashedLine
import com.example.nike.homeScreen.user.UserViewModel
import com.example.nike.pressScale
import com.example.nike.screens.fonts
import com.example.nike.ui.theme.NikeTheme
import java.util.Locale

class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(4) // MMYY only

        val formatted = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i == 1 && i != trimmed.lastIndex) {
                    append("/") // add slash after month
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    else -> offset + 1
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    else -> offset - 1
                }
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

class CheckoutScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = 0xFFF8F9FA.toInt()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = 0xFFF8F9FA.toInt()
            )
        )

        val subtotal = intent.getDoubleExtra("subTotal", 0.0)
        val totalCost = intent.getDoubleExtra("totalCost", 0.0)
        val shipping = intent.getDoubleExtra("shipping", 0.0)

        setContent {
            NikeTheme {
                Checkout_Screen(subtotal, totalCost, shipping)
            }
        }
    }
}

@Composable
private fun Checkout_Screen(
    subtotal: Double, totalCost: Double, shipping: Double,
    viewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val (backInteraction, backScale) = pressScale()
    val (emailInteraction, emailScale) = pressScale()
    val (phoneInteraction, phoneScale) = pressScale()
    val (addressArrowInteraction, addressArrowScale) = pressScale()
    val (paymentArrowInteraction, paymentArrowScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }

    val userProfile by viewModel.userProfileState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isAddressExpanded by remember { mutableStateOf(false) }
    var isPaymentExpanded by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showCardDialog by remember { mutableStateOf(false) }

    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    val addressArrowRotation by animateFloatAsState(if (isAddressExpanded) 90f else -90f)

    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cardCVV by remember { mutableStateOf("") }
    val paymentArrowRotation by animateFloatAsState(if (isPaymentExpanded) 90f else -90f)

    val emailFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }

    var isEmailEditable by remember { mutableStateOf(false) }
    var isPhoneEditable by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("rumenhussen@gmail.com") }
    var phone by remember { mutableStateOf("+88-692-764-269") }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            email = it.email
            phone = it.phone
        }
    }

    LaunchedEffect(isEmailEditable) {
        if (isEmailEditable) {
            emailFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(isPhoneEditable) {
        if (isPhoneEditable) {
            phoneFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    val cardState by viewModel.cardState.collectAsState()

    val displayCardNumber = if (!cardState?.last4Digits.isNullOrEmpty()) {
        "**** **** **** ${cardState?.last4Digits}"
    } else {
        "**** **** Card Number"
    }

    val address by viewModel.addressState.collectAsState()

    val addressText = listOf(
        address?.addressLine,
        address?.city,
        address?.postcode
    ).filter { !it.isNullOrBlank() }
        .joinToString(", ")

    val finalAddress = if (!address?.country.isNullOrBlank()) {
        "$addressText - ${address?.country}"
    } else addressText

    LaunchedEffect(showCardDialog) {
        if (showCardDialog) {
            cardState?.let {
                cardNumber = "************${it.last4Digits}"
                cardHolderName = it.cardHolderName
                expiryDate = it.expiryDate
                cardCVV = "***"
            }
        }
    }

    LaunchedEffect(showAddressDialog) {
        if (showAddressDialog) {
            address?.let {
                addressLine = it.addressLine
                city = it.city
                postcode = it.postcode
                country = it.country
            }
        }
    }

    Scaffold(
        containerColor = colorResource(id = R.color.background_color),
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

            Text(
                modifier = Modifier
                    .padding(top = 26.dp)
                    .align(Alignment.TopCenter),
                text = "Checkout",
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530),
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 25.dp, vertical = 90.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .align(Alignment.TopCenter)
                    .height(435.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        modifier = Modifier
                            .padding(start = 25.dp),
                        text = "Contact Information",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8F9FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.email_icon),
                                contentDescription = "Email Icon",
                                tint = Color(0xFF1A2530),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            val selectionColors = TextSelectionColors(
                                handleColor = Color(0xFF1C1C1C),
                                backgroundColor = Color(0xFF1C1C1C).copy(alpha = 0.3f)
                            )

                            CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
                                BasicTextField(
                                    value = email,
                                    onValueChange = { newValue: String ->
                                        email = newValue
                                    },
                                    readOnly = !isEmailEditable,
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = fonts,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal,
                                        color = Color(0xFF1A2530)
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .focusRequester(emailFocusRequester)
                                )
                            }

                            Text(
                                text = "Email",
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                fontFamily = fonts,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = Color(0xFF707B81)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(R.drawable.edit_icon),
                            contentDescription = "Edit Icon",
                            tint = Color(0xFF707B81),
                            modifier = Modifier.size(20.dp)
                                .clickable(
                                    interactionSource = emailInteraction,
                                    indication = null
                                ) {
                                    isEmailEditable = !isEmailEditable

                                    if (!isEmailEditable) {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                }
                                .graphicsLayer(
                                    scaleX = emailScale,
                                    scaleY = emailScale
                                )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8F9FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.call_icon),
                                contentDescription = "Call Icon",
                                tint = Color(0xFF1A2530),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            val selectionColors = TextSelectionColors(
                                handleColor = Color(0xFF1C1C1C),
                                backgroundColor = Color(0xFF1C1C1C).copy(alpha = 0.3f)
                            )

                            CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
                                BasicTextField(
                                    value = phone,
                                    onValueChange = { newValue: String ->
                                        phone = newValue
                                    },
                                    readOnly = !isPhoneEditable,
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = fonts,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal,
                                        color = Color(0xFF1A2530)
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .focusRequester(phoneFocusRequester)
                                )
                            }

                            Text(
                                text = "Phone",
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                fontFamily = fonts,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = Color(0xFF707B81)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(R.drawable.edit_icon),
                            contentDescription = "Edit Icon",
                            tint = Color(0xFF707B81),
                            modifier = Modifier.size(20.dp)
                                .clickable(
                                    interactionSource = phoneInteraction,
                                    indication = null
                                ) {
                                    isPhoneEditable = !isPhoneEditable

                                    if (!isPhoneEditable) {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                }
                                .graphicsLayer(
                                    scaleX = phoneScale,
                                    scaleY = phoneScale
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        modifier = Modifier
                            .padding(start = 25.dp),
                        text = "Address",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = finalAddress.ifBlank { "No Address Found" },
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            fontFamily = fonts,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = Color(0xFF707B81)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(R.drawable.arrow_icon),
                            contentDescription = "Arrow Icon",
                            tint = Color(0xFF707B81),
                            modifier = Modifier.size(15.dp)
                                .clickable (
                                    interactionSource = addressArrowInteraction,
                                    indication = null
                                ) {
                                    isAddressExpanded = !isAddressExpanded
                                    showAddressDialog = true
                                }
                                .rotate(addressArrowRotation)
                                .graphicsLayer(
                                    scaleX = addressArrowScale,
                                    scaleY = addressArrowScale
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Image(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 25.dp)
                            .size(width = 290.dp, height = 100.dp),
                        painter = painterResource(R.drawable.map),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        modifier = Modifier
                            .padding(start = 25.dp),
                        text = "Payment Method",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8F9FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.card_logo),
                                contentDescription = "Card Icon",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Credit Card",
                                fontSize = 14.sp,
                                lineHeight = 16.sp,
                                fontFamily = fonts,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = Color(0xFF1A2530)
                            )

                            Text(
                                text = displayCardNumber,
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                fontFamily = fonts,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = Color(0xFF707B81)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(R.drawable.arrow_icon),
                            contentDescription = "Arrow Icon",
                            tint = Color(0xFF707B81),
                            modifier = Modifier
                                .size(15.dp)
                                .rotate(paymentArrowRotation)
                                .clickable (
                                    interactionSource = paymentArrowInteraction,
                                    indication = null
                                ) {
                                    isPaymentExpanded = !isPaymentExpanded
                                    showCardDialog = true
                                }
                                .graphicsLayer(
                                    scaleX = paymentArrowScale,
                                    scaleY = paymentArrowScale
                                )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .align(Alignment.BottomCenter)
                    .height(240.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 26.dp, start = 25.dp)
                        .align(Alignment.TopStart),
                    text = "Subtotal",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF707B81)
                )

                Text(
                    modifier = Modifier
                        .padding(top = 26.dp, end = 25.dp)
                        .align(Alignment.TopEnd),
                    text = "$ ${String.format(Locale.US, "%.2f", subtotal)}",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Text(
                    modifier = Modifier
                        .padding(top = 66.dp, start = 25.dp)
                        .align(Alignment.TopStart),
                    text = "Shopping",
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF707B81),
                )

                Text(
                    modifier = Modifier
                        .padding(top = 66.dp, end = 25.dp)
                        .align(Alignment.TopEnd),
                    text = "$ ${String.format(Locale.US, "%.2f", shipping)}",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                DashedLine(
                    modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 105.dp)
                )

                Text(
                    modifier = Modifier
                        .padding(top = 120.dp, start = 25.dp)
                        .align(Alignment.TopStart),
                    text = "Total Cost",
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Text(
                    modifier = Modifier
                        .padding(top = 120.dp, end = 25.dp)
                        .align(Alignment.TopEnd),
                    text = "$ ${String.format(Locale.US, "%.2f", totalCost)}",
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Box(
                    modifier = Modifier
                        .padding(start = 25.dp, end = 25.dp, top = 168.dp)
                        .height(52.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF5B9EE1))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {

                        }
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Payment",
                        fontSize = 15.sp, lineHeight = 16.sp,
                        fontFamily = fonts, fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal, color = Color(0xFFFFFFFF)
                    )
                }
            }

            if (showCardDialog) {
                CardBottomDialog(
                    title = "Update Card Details",
                    message = "Please enter your credit card information to update your payment details.",
                    cardNumber = cardNumber,
                    onCardNumberChange = {
                        cardNumber = it
                    },
                    cardHolderName = cardHolderName,
                    onCardHolderNameChange = {
                        cardHolderName = it
                    },
                    cardCVV = cardCVV,
                    onCardCVVChange = {
                        cardCVV = it
                    },
                    expiryDate = expiryDate,
                    onExpiryDateChange = {
                        expiryDate = it
                    },
                    confirmText = "Continue",
                    dismissText = "Cancel",
                    onConfirm = {
                        viewModel.saveCard(
                            cardNumber,
                            cardHolderName,
                            expiryDate
                        )
                        showCardDialog = false
                    },
                    onDismiss = {
                        showCardDialog = false
                        isPaymentExpanded = !isPaymentExpanded
                    }
                )
            }

            if (showAddressDialog) {
                IOSStyleBottomDialog(
                    title = "Update Address",
                    message = "Please enter your address details to update your delivery information.",
                    addressLine = addressLine,
                    onAddressLineChange = {
                        addressLine = it
                    },
                    city = city,
                    onCityChange = {
                        city = it
                    },
                    postcode = postcode,
                    onPostCodeChange = {
                        postcode = it
                    },
                    country = country,
                    onCountryChange = {
                        country = it
                    },
                    confirmText = "Continue",
                    dismissText = "Cancel",
                    onConfirm = {
                        viewModel.saveAddress(
                            addressLine,
                            city,
                            postcode,
                            country
                        )

                        showAddressDialog = false
                    },
                    onDismiss = {
                        showAddressDialog = false
                        isAddressExpanded = !isAddressExpanded
                    }
                )
            }
        }
    }
}

@Composable
fun CardBottomDialog(
    title: String,
    message: String,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardHolderName: String,
    onCardHolderNameChange: (String) -> Unit,
    cardCVV: String,
    onCardCVVChange: (String) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var cardNumberError by remember { mutableStateOf(false) }
    var cardNumberErrorText by remember { mutableStateOf("") }
    var cardHolderNameError by remember { mutableStateOf(false) }
    var cardHolderNameErrorText by remember { mutableStateOf("") }
    var cardCVVError by remember { mutableStateOf(false) }
    var cardCVVErrorText by remember { mutableStateOf("") }
    var expiryDateError by remember { mutableStateOf(false) }
    var expiryDateErrorText by remember { mutableStateOf("") }

    val formattedExpiry = when {
        expiryDate.length >= 3 -> {
            expiryDate.take(2) + "/" + expiryDate.drop(2)
        }
        expiryDate.isNotEmpty() -> expiryDate
        else -> "MM/YY"
    }

    val formattedCardNumber = when {
        cardNumber.length >= 4 -> cardNumber.chunked(4).joinToString(" ")
        cardNumber.isNotEmpty() -> cardNumber
        else -> "XXXX XXXX XXXX XXXX"
    }

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

                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                            .padding(horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            painter = painterResource(R.drawable.card_matt_black),
                            contentDescription = null
                        )

                        Icon(
                            painter = painterResource(R.drawable.card_logo),
                            contentDescription = "Card Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .padding(25.dp)
                                .padding(top = 5.dp)
                                .size(38.dp)
                                .align(Alignment.TopEnd)
                        )

                        Icon(
                            painter = painterResource(R.drawable.sim_icon),
                            contentDescription = "Sim Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .padding(25.dp)
                                .padding(top = 5.dp)
                                .size(42.dp)
                                .align(Alignment.TopStart)
                        )

                        Text(
                            modifier = Modifier.align(Alignment.TopStart)
                                .padding(top = 100.dp, start = 25.dp),
                            text = formattedCardNumber,
                            fontFamily = fonts,
                            fontSize = 17.sp, lineHeight = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                            color = Color(0xFFFFFFFF),
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 100.dp, start = 25.dp)
                        ) {
                            Column (
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Card Holder Name",
                                    fontFamily = fonts,
                                    fontSize = 10.sp, lineHeight = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = Color(0xFFFFFFFF).copy(alpha = 0.4f)
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = cardHolderName.ifBlank { "CARD HOLDER" },
                                    fontFamily = fonts,
                                    fontSize = 12.sp, lineHeight = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = Color(0xFFFFFFFF)
                                )
                            }

                            Spacer(modifier = Modifier.width(15.dp))

                            Column (
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Expiry Date",
                                    fontFamily = fonts,
                                    fontSize = 10.sp, lineHeight = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = Color(0xFFFFFFFF).copy(alpha = 0.4f)
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = formattedExpiry,
                                    fontFamily = fonts,
                                    fontSize = 12.sp, lineHeight = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = Color(0xFFFFFFFF)
                                )
                            }

                            Spacer(modifier = Modifier.width(15.dp))

                            Column (
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "CVV",
                                    fontFamily = fonts,
                                    fontSize = 10.sp, lineHeight = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = Color(0xFFFFFFFF).copy(alpha = 0.4f)
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = cardCVV.ifBlank { "XXX" },
                                    fontFamily = fonts,
                                    fontSize = 12.sp, lineHeight = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = Color(0xFFFFFFFF)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        modifier = Modifier.align(Alignment.Start).padding(start = 2.dp),
                        text = "Card Number", fontSize = 13.sp,
                        lineHeight = 15.sp, fontFamily = fonts,
                        fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Box(modifier = Modifier.padding(vertical = 8.dp)
                        .height(52.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (cardNumberError) Color.Red else Color.Transparent,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (inputField, placeholderText) = createRefs()

                            if (cardNumber.isEmpty()) {
                                Text(modifier = Modifier.constrainAs(placeholderText) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start, margin = 15.dp)
                                    end.linkTo(parent.end, margin = 15.dp)
                                    width = Dimension.fillToConstraints },
                                    text = "Enter Card Number",
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
                                    value = cardNumber,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }.take(16)
                                        onCardNumberChange(digits)

                                        if (digits.isNotBlank()) {
                                            cardNumberError = false
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                    if (cardNumberError) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = cardNumberErrorText,
                            color = Color.Red,
                            fontSize = 12.sp,
                            lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.align(Alignment.Start).padding(start = 2.dp),
                        text = "Card Holder Name", fontSize = 13.sp,
                        lineHeight = 15.sp, fontFamily = fonts,
                        fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Box(modifier = Modifier.padding(vertical = 8.dp)
                        .height(52.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (cardHolderNameError) Color.Red else Color.Transparent,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (inputField, placeholderText) = createRefs()

                            if (cardHolderName.isEmpty()) {
                                Text(modifier = Modifier.constrainAs(placeholderText) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start, margin = 15.dp)
                                    end.linkTo(parent.end, margin = 15.dp)
                                    width = Dimension.fillToConstraints },
                                    text = "Enter Card Holder Name",
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
                                    value = cardHolderName,
                                    onValueChange = {
                                        onCardHolderNameChange(it)

                                        if (it.isNotBlank()) {
                                            cardHolderNameError = false
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

                    if (cardHolderNameError) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = cardHolderNameErrorText,
                            color = Color.Red,
                            fontSize = 12.sp,
                            lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                modifier = Modifier.align(Alignment.Start).padding(start = 2.dp),
                                text = "Expiry Date", fontSize = 13.sp,
                                lineHeight = 15.sp, fontFamily = fonts,
                                fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                                color = Color(0xFF1A2530)
                            )

                            Box(modifier = Modifier.padding(vertical = 8.dp)
                                .height(52.dp)
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (expiryDateError) Color.Red else Color.Transparent,
                                    shape = RoundedCornerShape(28.dp)
                                )
                                .background(Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(28.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                                    val (inputField, placeholderText) = createRefs()

                                    if (expiryDate.isEmpty()) {
                                        Text(modifier = Modifier.constrainAs(placeholderText) {
                                            top.linkTo(parent.top)
                                            bottom.linkTo(parent.bottom)
                                            start.linkTo(parent.start, margin = 15.dp)
                                            end.linkTo(parent.end, margin = 15.dp)
                                            width = Dimension.fillToConstraints },
                                            text = "Expiry Date",
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
                                            value = expiryDate,
                                            onValueChange = { input ->
                                                val digits = input.filter { it.isDigit() }.take(4)

                                                var corrected = digits

                                                if (digits.length >= 2) {
                                                    var month = digits.take(2)

                                                    val monthInt = month.toIntOrNull() ?: 1
                                                    month = when {
                                                        monthInt == 0 -> "01"
                                                        monthInt > 12 -> "12"
                                                        else -> month.padStart(2, '0')
                                                    }

                                                    corrected = month + digits.drop(2)
                                                }

                                                if (input.isNotBlank()) { expiryDateError = false }

                                                onExpiryDateChange(corrected)
                                            },
                                            visualTransformation = ExpiryDateVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                            if (expiryDateError) {
                                Text(
                                    modifier = Modifier.align(Alignment.Start).padding(start = 2.dp),
                                    text = expiryDateErrorText,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                modifier = Modifier.align(Alignment.Start),
                                text = "CVV", fontSize = 13.sp,
                                lineHeight = 15.sp, fontFamily = fonts,
                                fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                                color = Color(0xFF1A2530)
                            )

                            Box(modifier = Modifier.padding(vertical = 8.dp)
                                .height(52.dp)
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (cardCVVError) Color.Red else Color.Transparent,
                                    shape = RoundedCornerShape(28.dp)
                                )
                                .background(Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(28.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                                    val (inputField, placeholderText) = createRefs()

                                    if (cardCVV.isEmpty()) {
                                        Text(modifier = Modifier.constrainAs(placeholderText) {
                                            top.linkTo(parent.top)
                                            bottom.linkTo(parent.bottom)
                                            start.linkTo(parent.start, margin = 15.dp)
                                            end.linkTo(parent.end, margin = 15.dp)
                                            width = Dimension.fillToConstraints },
                                            text = "Card CVV",
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
                                            value = cardCVV,
                                            onValueChange = { input ->
                                                val digits = input.filter { it.isDigit() }.take(3)
                                                onCardCVVChange(digits)

                                                if (input.isNotBlank()) {
                                                    cardCVVError = false
                                                }
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                            if (cardCVVError) {
                                Text(
                                    modifier = Modifier.align(Alignment.Start),
                                    text = cardCVVErrorText,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                                )
                            }
                        }
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
                                    cardNumberError = cardNumber.isBlank()
                                    cardHolderNameError = cardHolderName.isBlank()
                                    expiryDateError = expiryDate.isBlank()
                                    cardCVVError = cardCVV.isBlank()

                                    cardNumberErrorText = if (cardNumber.isBlank()) "Please enter card number" else ""
                                    cardHolderNameErrorText = if (cardHolderName.isBlank()) "Please enter card holder name" else ""
                                    expiryDateErrorText = if (expiryDate.isBlank()) "Please enter expiry date" else ""
                                    cardCVVErrorText = if (cardCVV.isBlank()) "Please enter card CVV" else ""

                                    if (cardNumberErrorText.isNotEmpty() || cardHolderNameErrorText.isNotEmpty() || expiryDateErrorText.isNotEmpty() || cardCVVErrorText.isNotEmpty()) return@clickable

                                    cardNumberErrorText = ""
                                    cardHolderNameErrorText = ""
                                    expiryDateErrorText = ""
                                    cardCVVErrorText = ""

                                    onConfirm()
                                }
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

@Composable
fun IOSStyleBottomDialog(
    title: String,
    message: String,
    addressLine: String,
    onAddressLineChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    postcode: String,
    onPostCodeChange: (String) -> Unit,
    country: String,
    onCountryChange: (String) -> Unit,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var addressLineError by remember { mutableStateOf(false) }
    var addressLineErrorText by remember { mutableStateOf("") }
    var cityError by remember { mutableStateOf(false) }
    var cityErrorText by remember { mutableStateOf("") }
    var postcodeError by remember { mutableStateOf(false) }
    var postcodeErrorText by remember { mutableStateOf("") }
    var countryError by remember { mutableStateOf(false) }
    var countryErrorText by remember { mutableStateOf("") }

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

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        modifier = Modifier.align(Alignment.Start).padding(start = 2.dp),
                        text = "AddressLine", fontSize = 13.sp,
                        lineHeight = 15.sp, fontFamily = fonts,
                        fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Box(modifier = Modifier.padding(vertical = 8.dp)
                        .height(52.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (addressLineError) Color.Red else Color.Transparent,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (inputField, placeholderText) = createRefs()

                            if (addressLine.isEmpty()) {
                                Text(modifier = Modifier.constrainAs(placeholderText) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start, margin = 15.dp)
                                    end.linkTo(parent.end, margin = 15.dp)
                                    width = Dimension.fillToConstraints },
                                    text = "Enter AddressLine",
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
                                    value = addressLine,
                                    onValueChange = {
                                        onAddressLineChange(it)

                                        if (it.isNotBlank()) {
                                            addressLineError = false
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

                    if (addressLineError) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = addressLineErrorText,
                            color = Color.Red,
                            fontSize = 12.sp,
                            lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.align(Alignment.Start).padding(start = 2.dp),
                        text = "City", fontSize = 13.sp,
                        lineHeight = 15.sp, fontFamily = fonts,
                        fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Box(modifier = Modifier.padding(vertical = 8.dp)
                        .height(52.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (cityError) Color.Red else Color.Transparent,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (inputField, placeholderText) = createRefs()

                            if (city.isEmpty()) {
                                Text(modifier = Modifier.constrainAs(placeholderText) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start, margin = 15.dp)
                                    end.linkTo(parent.end, margin = 15.dp)
                                    width = Dimension.fillToConstraints },
                                    text = "Enter City",
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
                                    value = city,
                                    onValueChange = {
                                        onCityChange(it)

                                        if (it.isNotBlank()) {
                                            cityError = false
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

                    if (cityError) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = cityErrorText,
                            color = Color.Red,
                            fontSize = 12.sp,
                            lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.align(Alignment.Start).padding(start = 2.dp),
                        text = "Postcode", fontSize = 13.sp,
                        lineHeight = 15.sp, fontFamily = fonts,
                        fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Box(modifier = Modifier.padding(vertical = 8.dp)
                        .height(52.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (postcodeError) Color.Red else Color.Transparent,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (inputField, placeholderText) = createRefs()

                            if (postcode.isEmpty()) {
                                Text(modifier = Modifier.constrainAs(placeholderText) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start, margin = 15.dp)
                                    end.linkTo(parent.end, margin = 15.dp)
                                    width = Dimension.fillToConstraints },
                                    text = "Enter Postcode",
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
                                    value = postcode,
                                    onValueChange = {
                                        onPostCodeChange(it)

                                        if (it.isNotBlank()) {
                                            postcodeError = false
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

                    if (postcodeError) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = postcodeErrorText,
                            color = Color.Red,
                            fontSize = 12.sp,
                            lineHeight = 14.sp, fontFamily = fonts, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.align(Alignment.Start).padding(start = 2.dp),
                        text = "Country", fontSize = 13.sp,
                        lineHeight = 15.sp, fontFamily = fonts,
                        fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )

                    Box(modifier = Modifier.padding(vertical = 8.dp)
                        .height(52.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (countryError) Color.Red else Color.Transparent,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .background(Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                            val (inputField, placeholderText) = createRefs()

                            if (country.isEmpty()) {
                                Text(modifier = Modifier.constrainAs(placeholderText) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start, margin = 15.dp)
                                    end.linkTo(parent.end, margin = 15.dp)
                                    width = Dimension.fillToConstraints },
                                    text = "Enter Country",
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
                                    value = country,
                                    onValueChange = {
                                        onCountryChange(it)

                                        if (it.isNotBlank()) {
                                            countryError = false
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

                    if (countryError) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = countryErrorText,
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
                                    addressLineError = addressLine.isBlank()
                                    cityError = city.isBlank()
                                    postcodeError = postcode.isBlank()
                                    countryError = country.isBlank()

                                    addressLineErrorText = if (addressLine.isBlank()) "Please enter addressLine" else ""
                                    cityErrorText = if (city.isBlank()) "Please enter city" else ""
                                    postcodeErrorText = if (postcode.isBlank()) "Please enter postcode" else ""
                                    countryErrorText = if (country.isBlank()) "Please enter country" else ""

                                    if (addressLineErrorText.isNotEmpty() || cityErrorText.isNotEmpty() || postcodeErrorText.isNotEmpty() || countryErrorText.isNotEmpty()) return@clickable

                                    addressLineErrorText = ""
                                    cityErrorText = ""
                                    postcodeErrorText = ""
                                    countryErrorText = ""

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

@Preview(showSystemUi = true)
@Composable
private fun CheckoutScreenPreview() {
    NikeTheme {
        Checkout_Screen(0.0, 0.0, 0.0)
    }
}