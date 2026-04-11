package com.example.nike.searchScreen

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nike.R
import com.example.nike.detailsScreen.DetailsScreen
import com.example.nike.homeScreen.ShoeCard
import com.example.nike.homeScreen.shoes.ShoesViewModel
import com.example.nike.navigation.BottomNavRoute
import com.example.nike.pressScale
import com.example.nike.screens.fonts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.emptyList

val Context.dataStore by preferencesDataStore(name = "search_prefs")

object DataStoreKeys {
    val SEARCH_HISTORY = stringPreferencesKey("search_history")
}

class SearchHistoryDataStore(private val context: Context) {
    val historyFlow: Flow<List<String>> = context.dataStore.data
        .map { prefs ->
            val saved = prefs[DataStoreKeys.SEARCH_HISTORY] ?: ""
            if (saved.isEmpty()) emptyList()
            else saved.split("||")
        }

    suspend fun saveHistory(history: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[DataStoreKeys.SEARCH_HISTORY] =
                history.joinToString("||")
        }
    }
}

@Composable
fun SearchScreen(navController: NavHostController, shoesViewModel: ShoesViewModel = viewModel()) {
    val viewModel: SearchViewModel = viewModel()
    val (backInteraction, backScale) = pressScale()
    val (clearALlInteraction, clearAllScale) = pressScale()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    
    val shoes by shoesViewModel.shoes.observeAsState(emptyList())
    val context = LocalContext.current

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
            text = "Search",
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal,
            color = Color(0xFF1A2530),
        )

        Text(
            modifier = Modifier
                .padding(top = 27.dp, end = 25.dp)
                .align(Alignment.TopEnd)
                .clickable(
                    interactionSource = clearALlInteraction,
                    indication = null
                ) {
                    navController.navigate(BottomNavRoute.Home.route) {
                        popUpTo(BottomNavRoute.Home.route)
                        launchSingleTop = true
                    }
                }
                .graphicsLayer(
                    scaleX = clearAllScale,
                    scaleY = clearAllScale
                ),
            text = "Cancel",
            fontSize = 13.sp,
            lineHeight = 22.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal,
            color = Color(0xFF5B9EE1),
        )

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 25.dp, top = 80.dp)
                .height(48.dp)
                .focusRequester(focusRequester),
            query = searchText,
            onQueryChange = { newValue ->
                searchText = newValue

                shoesViewModel.updateSearchQuery(newValue.text)

                if (newValue.text.endsWith(" ")) {
                    viewModel.addSearch(newValue.text.trim())
                }
            },
            viewModel = viewModel
        )

        Text(
            modifier = Modifier
                .padding(start = 25.dp, top = 150.dp),
            text = "Shoes",
            fontSize = 18.sp,
            lineHeight = 20.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal,
            color = Color(0xFF1A2530),
        )

        if (searchText.text.isEmpty()) {
            SearchHistoryList(
                history = viewModel.searchHistory,
                onItemClick = { selected ->
                    searchText = TextFieldValue(selected)
                    shoesViewModel.updateSearchQuery(selected)
                },
                onDeleteClick = { item ->
                    viewModel.removeSearch(item)
                },
                modifier = Modifier.padding(top = 180.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 190.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 25.dp, end = 25.dp, bottom = 125.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(shoes) { shoe ->
                        ShoeCard(
                            shoe = shoe,
                            onClick = {
                                val intent = Intent(context, DetailsScreen::class.java).apply {
                                    putExtra("shoe", shoe)
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
}

@Composable
private fun SearchBar(
    modifier: Modifier,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    viewModel: SearchViewModel,
    shoesViewModel: ShoesViewModel = viewModel()

) {
    val selectionColors = TextSelectionColors(
        handleColor = Color(0xFF1C1C1C),
        backgroundColor = Color(0xFF1C1C1C).copy(alpha = 0.3f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF707B81)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 42.dp, end = 32.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.text.isEmpty()) {
                            Text(
                                text = "Search Your Shoes", fontFamily = fonts,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFF707B81)
                            )
                        }

                        innerTextField()
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.addSearch(query.text)
                        shoesViewModel.updateSearchQuery(query.text)
                    }
                )
            )

            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 5.dp)
                    .size(26.dp),
                tint = Color(0xFF707B81)
            )

            if (query.text.isNotEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.clear_all_icon),
                    contentDescription = "Clear",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 5.dp)
                        .size(20.dp)
                        .clickable {
                            onQueryChange(TextFieldValue("", TextRange(0)))
                        },
                    tint = Color(0xFF5B9EE1)
                )
            }
        }
    }
}

@Composable
fun SearchHistoryList(
    history: List<String>,
    onItemClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = history,
            key = { it }
        ) { item ->

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.time_icon),
                        contentDescription = null,
                        tint = Color(0xFF707B81),
                        modifier = Modifier.padding(start = 5.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = item,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        textAlign = TextAlign.Start,
                        color = Color(0xFF1A2530),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        painter = painterResource(R.drawable.cancel_icon),
                        contentDescription = "Delete",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp)
                            .clickable {
                                onDeleteClick(item)
                            },
                        tint = Color(0xFF707B81)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun SearchScreenPreview() {
    val navController = rememberNavController()
    SearchScreen(navController)
}