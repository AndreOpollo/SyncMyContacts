package com.example.syncmycontacts.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log.v
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.syncmycontacts.presentation.ContactsViewModel
import com.example.syncmycontacts.ui.theme.background
import com.example.syncmycontacts.ui.theme.primary
import com.example.syncmycontacts.ui.theme.primaryContainer
import com.example.syncmycontacts.ui.theme.secondary
import com.example.syncmycontacts.ui.theme.secondaryContainer
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsScreen(
    contactsViewModel: ContactsViewModel,
){
    val contactUiState = contactsViewModel.contactsUiState.collectAsState()
    val filteredContacts = contactsViewModel.filteredContacts.collectAsState()
    val searchText = contactsViewModel.searchText.collectAsState()
    var hasPermission by remember{mutableStateOf(false)}
    val groupedContacts = filteredContacts.value
        .filter {
            !it.name.isNullOrBlank()
        }
        .groupBy { val first = it.name!!.first()
            if(first.isLetter())   first.uppercaseChar() else "#"
        }
        .toSortedMap(compareBy { if(it=="#")'A'-1 else it })
    RequestContactsPermission {
        hasPermission = true
    }
    LaunchedEffect(hasPermission) {
        contactsViewModel.fetchContacts()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = background)
            .systemBarsPadding()
    ){
        TextField(
            value = searchText.value,
            onValueChange = contactsViewModel::onSearchTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(CircleShape)
            ,
            leadingIcon = {
                Icon(Icons.Default.Search,contentDescription = "Search contacts")
            },
            placeholder = {Text("Search contacts", textAlign = TextAlign.Center)},
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = secondaryContainer,
                focusedContainerColor = secondaryContainer
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            contactsViewModel.backupContactsToJson()
        }) {
            Text("Backup")
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
    ) {
        groupedContacts.forEach { (initial, contactsForLetter) ->
            stickyHeader(key = "header-$initial") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = background)

                ){
                Text(
                    text = initial.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.LightGray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp),
                    )
             }
            }
            
            item(key = "card-${initial}"){
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        contactsForLetter.forEachIndexed { index, contact ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DisplayPhoto(name = contact.name ?: "Unknown")
                                Spacer(modifier = Modifier.width(16.dp))

                                    Column(
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        HighlightedText(
                                            text = contact.name ?: "Unknown",
                                            searchQuery = searchText.value,
                                            style = MaterialTheme.typography.titleMedium,
                                            highlightColor = secondary,
                                            normalColor = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        HighlightedText(
                                            text = contact.phone ?: "Unknown",
                                            searchQuery = searchText.value,
                                            style = MaterialTheme.typography.bodyMedium,
                                            highlightColor = secondary,
                                            normalColor = Color.Gray,
                                            fontSize = 12.sp

                                        )
                                        if(index<contactsForLetter.lastIndex){
                                            Box(modifier = Modifier.padding(top = 4.dp)) {
                                                HorizontalDivider()
                                            }
                                        }
                                    }




                            }

                        }

                    }
                }

            }

        }
    }


    }
}
@Composable
fun ContactItem(){

}

@Composable
fun RequestContactsPermission(
    onPermissionGranted:()->Unit
){
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        isGranted->
        if(isGranted)onPermissionGranted()
    }
    LaunchedEffect(Unit) {
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED
        ){
            onPermissionGranted()
        }else{
            permissionLauncher.launch(
                Manifest.permission.READ_CONTACTS
            )
        }
    }

}

@Composable
fun HighlightedText(
    text:String,
    searchQuery:String,
    style:TextStyle,
    highlightColor: Color = Color.Red,
    normalColor: Color = Color.White,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    fontSize: TextUnit,
    fontWeight: FontWeight? = null

){
    val annotatedString = buildAnnotatedString {
        if(searchQuery.isBlank()||!text.contains(searchQuery,ignoreCase = true)){
            withStyle(style = SpanStyle(color = normalColor)){
                append(text)
            }
            return@buildAnnotatedString
        }

        var startIndex = 0
        val query = searchQuery.lowercase()
        val textLower = text.lowercase()
        while(startIndex<text.length){
            val matchIndex = textLower.indexOf(query,startIndex)
            if(matchIndex==-1){
                withStyle(style = SpanStyle(color = normalColor)){
                    append(text.substring(startIndex))
                }
                break
            }
            if(matchIndex>startIndex){
                withStyle(style= SpanStyle(color = normalColor)){
                    append(text.substring(startIndex,matchIndex))
                }
            }
            withStyle(style= SpanStyle(color = highlightColor)){
                append(text.substring(matchIndex,matchIndex+query.length))
            }
            startIndex = matchIndex + query.length
        }
    }
    Text(
        text = annotatedString,
        style = style,
        modifier = modifier,
        textAlign = textAlign,
        fontSize = fontSize,
        fontWeight = fontWeight
    )

}

@Composable
fun DisplayPhoto(
    name:String,
    modifier: Modifier = Modifier,
    size: Dp = 45.dp,
    fontSize: TextUnit = 20.sp
){
    val colors = listOf<Color>(
        Color(0xFFEF5350),
        Color(0xFFAB47BC),
        Color(0xFF42A5F5),
        Color(0xFF26A69A),
        Color(0xFFFFA726),
        Color(0xFF66BB6A)
    )
    val initial = name.firstOrNull()?.uppercase()
    val backgroundColor = remember(name){
        colors[abs(name.hashCode())%colors.size]
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = initial?:"U",
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold

        )

    }

}