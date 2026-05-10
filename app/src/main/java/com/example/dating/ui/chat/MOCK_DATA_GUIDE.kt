/**
 * MOCK DATA TESTING GUIDE
 * 
 * This document explains the mock data structure for testing the chat module.
 * 
 * =============================================================================
 * MAIN USER
 * =============================================================================
 * 
 * Database ID: 1
 * Email: mainuser@gmail.com
 * Name: Main User
 * 
 * =============================================================================
 * ACTIVE CONVERSATIONS (Matches)
 * =============================================================================
 * 
 * CONVERSATION 1: Alice Johnson
 * ├─ Match ID: 1
 * ├─ User ID: 2
 * ├─ Email: alice@gmail.com
 * ├─ Bio: Travel lover ✈️
 * ├─ Online: YES
 * ├─ Birth Date: 2003-06-12
 * ├─ Avatar: https://picsum.photos/400/400?random=2
 * ├─ Last Message: "How was your day?" (from main user)
 * ├─ Timestamp: 5 minutes ago
 * ├─ Unread Count: 1 ✨
 * └─ Messages:
 *    1. "Hey Alice 👋" - from main user - 1h ago - READ
 *    2. "Hi! Nice to meet you 😊" - from Alice - 59m ago - READ
 *    3. "How was your day?" - from main user - 5m ago - UNREAD ❌
 * 
 * CONVERSATION 2: Emma Wilson
 * ├─ Match ID: 2
 * ├─ User ID: 3
 * ├─ Email: emma@gmail.com
 * ├─ Bio: Dog mom 🐶
 * ├─ Online: YES
 * ├─ Birth Date: 2001-09-21
 * ├─ Avatar: https://picsum.photos/400/400?random=3
 * ├─ Last Message: "Do you like dogs?" (from Emma)
 * ├─ Timestamp: 2 minutes ago
 * ├─ Unread Count: 1 ✨
 * └─ Messages:
 *    1. "Hello Main User!" - from Emma - 2h ago - READ
 *    2. "Hey Emma 😄" - from main user - 2h ago - READ
 *    3. "Do you like dogs?" - from Emma - 2m ago - UNREAD ❌
 * 
 * =============================================================================
 * SUGGESTED CONNECTIONS (Non-matched users)
 * =============================================================================
 * 
 * SUGGESTION 1: Olivia Brown
 * ├─ User ID: 4
 * ├─ Email: olivia@gmail.com
 * ├─ Bio: Photographer
 * ├─ Online: NO (Offline)
 * ├─ Birth Date: 1999-11-30
 * ├─ Avatar: https://picsum.photos/400/400?random=4
 * └─ Mutual Connections: 5
 * 
 * SUGGESTION 2: Sophia Miller
 * ├─ User ID: 7
 * ├─ Email: sophia@gmail.com
 * ├─ Bio: Coffee first ☕
 * ├─ Online: YES
 * ├─ Birth Date: 2002-08-05
 * ├─ Avatar: https://picsum.photos/400/400?random=7
 * └─ Mutual Connections: 8
 * 
 * SUGGESTION 3: Ava Martinez
 * ├─ User ID: 9
 * ├─ Email: ava@gmail.com
 * ├─ Bio: Foodie 🍜
 * ├─ Online: YES
 * ├─ Birth Date: 2003-04-09
 * ├─ Avatar: https://picsum.photos/400/400?random=9
 * └─ Mutual Connections: 3
 * 
 * =============================================================================
 * TEST SCENARIOS
 * =============================================================================
 * 
 * 1. CHAT LIST VIEW
 *    ✓ Shows 2 active conversations
 *    ✓ Shows 3 suggested connections
 *    ✓ Shows unread badge (1) for both conversations
 *    ✓ Shows online indicators for Alice and Emma
 *    ✓ Shows last message preview
 *    ✓ Shows timestamp
 * 
 * 2. CONVERSATION DETAIL - Alice
 *    ✓ Shows Alice's profile header
 *    ✓ Shows "Active now" status
 *    ✓ Shows 3 messages in conversation
 *    ✓ Message 1 & 2: Colored differently (read)
 *    ✓ Message 3: Different color, indicates unread
 *    ✓ Messages show correct sender (Alice vs Main User)
 * 
 * 3. CONVERSATION DETAIL - Emma
 *    ✓ Shows Emma's profile header
 *    ✓ Shows "Active now" status
 *    ✓ Shows 3 messages in conversation
 *    ✓ Message 1 & 2: Colored differently (read)
 *    ✓ Message 3: Different color, indicates unread
 *    ✓ Messages show correct sender (Emma vs Main User)
 * 
 * 4. MESSAGE INPUT
 *    ✓ Can type in message input field
 *    ✓ Send button is enabled when text is present
 *    ✓ Send button is disabled when text is empty
 * 
 * 5. SUGGESTED CONNECTIONS
 *    ✓ Shows 3 suggestions in carousel
 *    ✓ Olivia shows as offline (no online indicator)
 *    ✓ Sophia and Ava show as online
 *    ✓ Click suggestion to connect
 * 
 * =============================================================================
 * TOTAL STATISTICS
 * =============================================================================
 * 
 * Active Conversations: 2
 * Total Messages: 6
 * Unread Messages: 2 (1 from Alice, 1 from Emma)
 * Suggested Connections: 3
 * Online Users in Chats: 2/2 (100%)
 * Online Users in Suggestions: 2/3 (66%)
 * 
 * =============================================================================
 * HOW TO USE MOCK DATA IN YOUR APP
 * =============================================================================
 * 
 * The mock data is automatically loaded when ChatViewModel is created.
 * It's located in: MockChatData.kt
 * 
 * To modify mock data:
 * 1. Open MockChatData.kt
 * 2. Edit the user objects (alice, emma, etc.)
 * 3. Edit the generateMessages() function to add/remove messages
 * 4. Edit the generateConversations() function to add/remove conversations
 * 5. Edit the generateSuggestions() function to add/remove suggestions
 * 
 * To switch to real data:
 * 1. In ChatViewModel.kt, replace MockChatData calls with ChatService calls
 * 2. Implement ChatService methods with actual API endpoints
 * 3. Connect ChatService to Retrofit client
 * 
 * =============================================================================
 * DATABASE SCHEMA MAPPING
 * =============================================================================
 * 
 * ChatUser → users table
 * ├─ userId → user_id
 * ├─ fullName → full_name
 * ├─ email → email
 * ├─ bio → bio
 * ├─ birthDate → birth_date
 * └─ gender → gender
 * 
 * Message → messages table
 * ├─ messageId → message_id
 * ├─ matchId → match_id
 * ├─ senderId → sender_id
 * ├─ content → content
 * ├─ sentAt → sent_at
 * └─ isRead → is_read
 * 
 * Conversation → matches + messages
 * ├─ matchId → matches.match_id
 * ├─ user → matches.user1_id or user2_id (joined with users)
 * ├─ messages → messages where match_id = ?
 * └─ matchMode → matches.match_mode
 * 
 * SuggestedConnection → users (non-matched)
 * ├─ userId → user_id
 * └─ user → joined users table
 * 
 * =============================================================================
 */
