package com.example.dating.ui.chat

import com.example.dating.R
import java.net.URLDecoder

data class ConversationDisplayUser(
    val fullName: String,
    val avatar: Any
)

fun getDisplayUser(
    user: ChatUser,
    matchMode: String
): ConversationDisplayUser {

    return if (matchMode == "anonymous") {

        ConversationDisplayUser(
            fullName = "Ẩn danh",
            avatar = R.drawable.anonymous_avatar
        )

    } else {

        ConversationDisplayUser(
            fullName = URLDecoder.decode(user.fullName, "UTF-8"),
            avatar = user.getDisplayAvatarUrl()
        )
    }
}