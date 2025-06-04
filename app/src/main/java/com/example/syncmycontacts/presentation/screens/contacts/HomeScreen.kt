package com.example.syncmycontacts.presentation.screens.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.syncmycontacts.presentation.FormatOption
import com.example.syncmycontacts.presentation.FormatOptionsBottomSheet
import com.example.syncmycontacts.presentation.SpeedDialItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
){
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true)
    var selectedAction by remember { mutableStateOf<String?>(null) }

    val items = listOf<SpeedDialItem>(
        SpeedDialItem(
            icon = Icons.Default.Call,
            label = "Camera",
            onClick = {}
        ),
        SpeedDialItem(
            icon = Icons.Default.Settings,
            label = "Export Contacts",
            onClick = {
                selectedAction = "Export Contacts"
            }
        ),
        SpeedDialItem(
            icon = Icons.Default.Search,
            label = "Import Contacts",
            onClick = {
                selectedAction = "Import Contacts"
            }
        ),

    )
    val sheetItems = listOf<FormatOption>(
        FormatOption(
            name = "XLS",
            icon = Icons.Default.DateRange,
            description = "Excel SpreadSheet",
            onClick = {

            }
        ),
        FormatOption(
            name = "VCF",
            icon = Icons.Default.DateRange,
            description = "vCard Contact File",
            onClick = {}
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ){
        ContactsScreen()
        SpeedDialItem(
            items = items,
            modifier = Modifier.fillMaxSize()
        )
        if(selectedAction!=null){
            FormatOptionsBottomSheet(
                title = selectedAction!!,
                formats = sheetItems,
                onDismiss = {selectedAction=null},
                sheetState = bottomSheetState
            )
        }
    }
    
}