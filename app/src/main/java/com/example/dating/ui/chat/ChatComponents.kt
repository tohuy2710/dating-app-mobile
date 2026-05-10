package com.example.dating.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.BorderGray
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkSecondaryText
import com.example.dating.ui.theme.DarkSurface
import com.example.dating.ui.theme.DarkText
import com.example.dating.ui.theme.Gray300
import com.example.dating.ui.theme.Gray500
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Search bar component for searching conversations.
 */
@Composable
fun ChatSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = {
            Text(
                text = "Tìm kiếm",
                color = Gray500,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = Gray500,
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = DarkText,
            unfocusedTextColor = DarkText,

            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            cursorColor = BrandPink
        ),
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

/**
 * Component showing suggested connection card.
 */
@Composable
fun SuggestedConnectionCard(
    suggestion: SuggestedConnection,
    onConnect: (ChatUser) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .clickable { onConnect(suggestion.user) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Gray300),
            contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = suggestion.user.avatarUrl,
                contentDescription = suggestion.user.fullName,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            if (suggestion.user.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(BrandPink)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, DarkBackground, CircleShape)
                )
            }
        }

        Text(
            text = suggestion.user.fullName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = DarkText
        )
    }
}

/**
 * Component showing a list of suggested connections.
 */
@Composable
fun SuggestedConnectionsList(
    suggestions: List<SuggestedConnection>,
    onConnect: (ChatUser) -> Unit,
    modifier: Modifier = Modifier
) {

    if (suggestions.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Có ${suggestions.size} người muốn kết nối với bạn",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = DarkSecondaryText
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(suggestions) { suggestion ->

                SuggestedConnectionCard(
                    suggestion = suggestion,
                    onConnect = onConnect
                )
            }
        }
    }
}

/**
 * Component showing a single conversation item in the messages list.
 */
@Composable
fun MessageItem(
    conversation: Conversation,
    onSelectConversation: (Conversation) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelectConversation(conversation) }
            .background(DarkBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),

        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Profile image
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Gray300),

            contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = conversation.user.avatarUrl,
                contentDescription = conversation.user.fullName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            if (conversation.user.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(BrandPink)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, DarkBackground, CircleShape)
                )
            }
        }

        // Message content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),

            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = conversation.user.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatMessageTime(conversation.lastMessageTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }

            Text(
                text = conversation.lastMessage,
                style = MaterialTheme.typography.bodySmall,
                color = DarkSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Unread indicator
        if (conversation.unreadCount > 0) {

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(BrandPinkDark),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (conversation.unreadCount > 9)
                        "9+"
                    else
                        conversation.unreadCount.toString(),

                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Formats message time for display.
 */
private fun formatMessageTime(timestamp: Long): String {

    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {

        diff < 60000 ->
            "Now"

        diff < 3600000 ->
            "${diff / 60000}m"

        diff < 86400000 ->
            "${diff / 3600000}h"

        diff < 604800000 -> {

            val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
            sdf.format(Date(timestamp))
        }

        else -> {

            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}