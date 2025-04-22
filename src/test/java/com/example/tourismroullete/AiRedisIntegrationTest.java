package com.example.tourismroullete;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.service.ChatCacheService;
import com.example.tourismroullete.repositories.ChatMessageRepository; // Keep if needed for other tests, unused here
import com.example.tourismroullete.repositories.UserRepository;

import org.junit.jupiter.api.AfterEach; // Import for cleanup
import org.junit.jupiter.api.BeforeEach; // Import for setup
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.RedisTemplate; // For potential cleanup

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AiRedisIntegrationTest {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatCacheService chatCacheService;

    // @Autowired // Not directly used in this specific test logic
    // private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired // Inject RedisTemplate for potential cleanup
    private RedisTemplate<String, Object> redisTemplate;

    private User testUser;
    private final String testUsername = "testuser-redis-integration";
    private final String userMessage = "Hello, what can I visit in Italy?";
    private String expectedCacheKey; // To help with cleanup

    @BeforeEach
    void setUp() {
        // Ensure user doesn't exist from previous runs
        userRepository.findByUsername(testUsername).ifPresent(user -> userRepository.delete(user));

        // Create test user
        User user = new User();
        user.setUsername(testUsername);
        user.setEmail(testUsername + "@example.com"); // Add required fields if necessary
        // user.setPassword("dummy"); // Password might not be needed if not using security context here
        testUser = userRepository.save(user);

        // Determine the expected cache key format (adjust if your service uses a different format)
        expectedCacheKey = "chatResponse::" + testUser.getId() + "::" + userMessage;

        // Clean up potential leftover cache entry before the test
        redisTemplate.delete(expectedCacheKey);
    }

    @AfterEach
    void tearDown() {
        // Clean up the created user
        if (testUser != null && testUser.getId() != null) {
            userRepository.deleteById(testUser.getId());
        }
        // Clean up the cache entry created during the test
        if (expectedCacheKey != null) {
            redisTemplate.delete(expectedCacheKey);
        }
    }


    @Test
    void testAiResponseCaching() {
        // 1. User is created in @BeforeEach

        // 2. Message is defined in fields

        // 3. Check if response already cached (using 2 arguments)
        String cached = chatCacheService.getCachedResponse(testUser.getId(), userMessage);
        assertNull(cached, "There should be no cached response yet");

        // 4. Get AI response
        // Note: This makes a real call to the AI model if not mocked/stubbed elsewhere
        String aiResponse = chatClient.prompt().user(userMessage).call().content();
        assertNotNull(aiResponse, "AI response should not be null");
        assertTrue(aiResponse.length() > 0, "AI response should not be empty"); // Added check

        // 5. Cache the response (using 3 arguments)
        chatCacheService.cacheResponse(testUser.getId(), userMessage, aiResponse);

        // 6. Verify cache retrieval (using 2 arguments)
        String cachedAgain = chatCacheService.getCachedResponse(testUser.getId(), userMessage);
        assertNotNull(cachedAgain, "Should retrieve response from cache now"); // Added check
        assertEquals(aiResponse, cachedAgain, "Cached response should match original AI response");
    }
}