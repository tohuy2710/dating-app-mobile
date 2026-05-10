package com.example.dating.ui.chat

/**
 * Mock data generator for testing the chat module.
 * This file contains all mock data based on the dating app database schema.
 * 
 * Database References:
 * - users table: user_id, full_name, email, bio, birth_date, gender
 * - messages table: message_id, match_id, sender_id, content, sent_at, is_read
 * - matches table: match_id, user1_id, user2_id, match_mode, matched_at, is_active
 */

object MockChatData {
    
    // Main user ID from the database
    const val MAIN_USER_ID = 1
    
    // ============================================================================
    // MOCK USERS (from users table)
    // ============================================================================
    
    val alice = ChatUser(
        userId = 2,
        fullName = "Alice Johnson",
        email = "alice@gmail.com",
        bio = "Travel lover ✈️",
        gender = "female",
        avatarUrl = "https://picsum.photos/400/400?random=2",
        isOnline = true,
        birthDate = "2003-06-12"
    )

    val emma = ChatUser(
        userId = 3,
        fullName = "Emma Wilson",
        email = "emma@gmail.com",
        bio = "Dog mom 🐶",
        gender = "female",
        avatarUrl = "https://picsum.photos/400/400?random=3",
        isOnline = true,
        birthDate = "2001-09-21"
    )

    val olivia = ChatUser(
        userId = 4,
        fullName = "Olivia Brown",
        email = "olivia@gmail.com",
        bio = "Photographer",
        gender = "female",
        avatarUrl = "https://picsum.photos/400/400?random=4",
        isOnline = false,
        birthDate = "1999-11-30"
    )

    val mia = ChatUser(
        userId = 6,
        fullName = "Mia Davis",
        email = "mia@gmail.com",
        bio = "Netflix addict",
        gender = "female",
        avatarUrl = "https://picsum.photos/400/400?random=6",
        isOnline = false,
        birthDate = "2004-01-18"
    )

    val sophia = ChatUser(
        userId = 7,
        fullName = "Sophia Miller",
        email = "sophia@gmail.com",
        bio = "Coffee first ☕",
        gender = "female",
        avatarUrl = "https://picsum.photos/400/400?random=7",
        isOnline = true,
        birthDate = "2002-08-05"
    )

    val ava = ChatUser(
        userId = 9,
        fullName = "Ava Martinez",
        email = "ava@gmail.com",
        bio = "Foodie 🍜",
        gender = "female",
        avatarUrl = "https://picsum.photos/400/400?random=9",
        isOnline = true,
        birthDate = "2003-04-09"
    )

    // ============================================================================
    // MOCK MESSAGES (from messages table)
    // ============================================================================
    
    fun generateMessages(): Map<Int, List<Message>> {
        val currentTime = System.currentTimeMillis()
        
        return mapOf(
            // Match 1 messages (main_user + alice)
            1 to listOf(
                Message(
                    messageId = 1,
                    matchId = 1,
                    senderId = MAIN_USER_ID,
                    content = "Hey Alice 👋",
                    sentAt = currentTime - 3600000, // 1 hour ago
                    isRead = true
                ),
                Message(
                    messageId = 2,
                    matchId = 1,
                    senderId = 2,
                    content = "Hi! Nice to meet you 😊",
                    sentAt = currentTime - 3540000, // ~59 minutes ago
                    isRead = true
                ),
                Message(
                    messageId = 3,
                    matchId = 1,
                    senderId = MAIN_USER_ID,
                    content = "How was your day?",
                    sentAt = currentTime - 300000, // 5 minutes ago
                    isRead = false
                )
            ),
            
            // Match 2 messages (main_user + emma)
            2 to listOf(
                Message(
                    messageId = 4,
                    matchId = 2,
                    senderId = 3,
                    content = "Hello Main User!",
                    sentAt = currentTime - 7200000, // 2 hours ago
                    isRead = true
                ),
                Message(
                    messageId = 5,
                    matchId = 2,
                    senderId = MAIN_USER_ID,
                    content = "Hey Emma 😄",
                    sentAt = currentTime - 7140000, // ~1h 59m ago
                    isRead = true
                ),
                Message(
                    messageId = 6,
                    matchId = 2,
                    senderId = 3,
                    content = "Do you like dogs?",
                    sentAt = currentTime - 120000, // 2 minutes ago
                    isRead = false
                )
            )
        )
    }
    
    // ============================================================================
    // MOCK CONVERSATIONS (combined match + messages)
    // ============================================================================
    
    fun generateConversations(): List<Conversation> {
        val currentTime = System.currentTimeMillis()
        val messages = generateMessages()
        
        return listOf(
            Conversation(
                matchId = 1,
                user = alice,
                lastMessage = "How was your day?",
                lastMessageTime = currentTime - 300000,
                unreadCount = 1,
                isTyping = false,
                messages = messages[1] ?: emptyList(),
                matchMode = "traditional"
            ),
            Conversation(
                matchId = 2,
                user = emma,
                lastMessage = "Do you like dogs?",
                lastMessageTime = currentTime - 120000,
                unreadCount = 1,
                isTyping = false,
                messages = messages[2] ?: emptyList(),
                matchMode = "traditional"
            )
        )
    }
    
    // ============================================================================
    // MOCK SUGGESTED CONNECTIONS (non-matched users)
    // ============================================================================
    
    fun generateSuggestions(): List<SuggestedConnection> {
        return listOf(
            SuggestedConnection(
                userId = 4,
                user = olivia,
                mutualFriendsCount = 5
            ),
            SuggestedConnection(
                userId = 7,
                user = sophia,
                mutualFriendsCount = 8
            ),
            SuggestedConnection(
                userId = 9,
                user = ava,
                mutualFriendsCount = 3
            )
        )
    }
    
    // ============================================================================
    // SUMMARY OF MOCK DATA
    // ============================================================================
    
    /**
     * Data Summary:
     * 
     * MAIN USER: ID 1 (mainuser@gmail.com)
     * 
     * ACTIVE MATCHES:
     * 1. Alice Johnson (ID 2) - Match ID 1
     *    - Last message: "How was your day?" (from main user, UNREAD)
     *    - Unread count: 1
     *    - Online: YES
     *    - 3 messages in history
     * 
     * 2. Emma Wilson (ID 3) - Match ID 2
     *    - Last message: "Do you like dogs?" (from Emma, UNREAD)
     *    - Unread count: 1
     *    - Online: YES
     *    - 3 messages in history
     * 
     * SUGGESTED CONNECTIONS:
     * 1. Olivia Brown (ID 4) - Photographer, Offline
     *    - Mutual connections: 5
     * 
     * 2. Sophia Miller (ID 7) - Coffee first, Online
     *    - Mutual connections: 8
     * 
     * 3. Ava Martinez (ID 9) - Foodie, Online
     *    - Mutual connections: 3
     * 
     * TOTAL UNREAD MESSAGES: 2 (1 from Alice, 1 from Emma)
     */
}
