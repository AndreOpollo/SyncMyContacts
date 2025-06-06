package com.example.syncmycontacts.presentation.components



import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SpeedDialItem(
    val icon: ImageVector,
    val label:String,
    val onClick:()->Unit
)
@Composable
fun SpeedDialItem(
    modifier: Modifier = Modifier,
    items:List<SpeedDialItem>,
    fabBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    fabContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    overlayColor: Color = Color.Black.copy(alpha = 0.5f)
){
    var isExpanded by remember{
        mutableStateOf(false)
    }
    val rotation by animateFloatAsState(
        targetValue = if(isExpanded)45f else 0f,
        animationSpec = tween(300)
    )
    val density = LocalDensity.current

    Box(modifier = modifier
        .fillMaxSize()){

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor)
                    .clickable{isExpanded = false}
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ){
            items.forEachIndexed{
                    index,item->
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically(
                        initialOffsetY = {with(density){100.dp.roundToPx()  }},
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index*50
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index*50
                        )
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = {with(density){ 100.dp.roundToPx() }},
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = (items.size-1-index)*30
                        )
                    )+fadeOut(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = (items.size-1-index)*30
                        )
                    ),
                ){
                    SpeedDialItemRow(
                        item = item,
                        onItemClick = {
                            item.onClick()
                            isExpanded = false
                        }
                    )

                }
                if(isExpanded){
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            FloatingActionButton(
                onClick = {
                    isExpanded = !isExpanded
                },
                contentColor = fabContentColor,
                containerColor = fabBackgroundColor,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isExpanded) {"Close"} else {"Open"},
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}

@Composable
private fun SpeedDialItemRow(
    item: SpeedDialItem,
    onItemClick:()->Unit
){
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Row(
        modifier = Modifier.scale(scale),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ){
        Card(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable{onItemClick()},
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ){
            Text(
                text = item.label,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        FloatingActionButton(
            onClick = {onItemClick()},
            modifier = Modifier
                .size(40.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}