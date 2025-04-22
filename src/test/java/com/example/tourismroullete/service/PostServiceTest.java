package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.exception.ResourceNotFoundException;
import com.example.tourismroullete.exception.UnauthorizedException;
import com.example.tourismroullete.model.Comment;
import com.example.tourismroullete.model.Like;
import com.example.tourismroullete.model.Post;
import com.example.tourismroullete.repository.CommentRepository;
import com.example.tourismroullete.repository.LikeRepository;
import com.example.tourismroullete.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private MultipartFile imageFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_withImage_shouldSavePostWithImage() throws IOException {
        // Arrange
        Post post = new Post();
        User user = new User();
        byte[] imageData = "image".getBytes();

        when(imageFile.isEmpty()).thenReturn(false);
        when(imageFile.getInputStream()).thenReturn(new ByteArrayInputStream(imageData));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.createPost(post, user, imageFile);

        // Assert
        assertEquals(user, result.getAuthor());
        assertArrayEquals(imageData, result.getImage());
        verify(postRepository).save(result);
    }

    @Test
    void updatePost_shouldUpdatePostFields() {
        // Arrange
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setId(postId);
        User user = new User();
        user.setUsername("danie");
        existingPost.setAuthor(user);

        Post updatedPost = new Post();
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");
        updatedPost.setLocation("Updated Location");
        updatedPost.setPublic(true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Post result = postService.updatePost(postId, updatedPost, "danie");

        // Assert
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        assertEquals("Updated Location", result.getLocation());
        assertTrue(result.isPublic());
    }

    @Test
    void updatePost_shouldThrowUnauthorizedException_ifUserDoesNotMatch() {
        Post post = new Post();
        User user = new User();
        user.setUsername("danie");
        post.setAuthor(user);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(UnauthorizedException.class, () ->
                postService.updatePost(1L, new Post(), "arian")
        );
    }

    @Test
    void deletePost_shouldDeletePostIfUserAuthorized() {
        Post post = new Post();
        post.setId(1L);
        User user = new User();
        post.setAuthor(user);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, user);

        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_shouldThrowExceptionIfNotAuthorized() {
        User owner = new User();
        owner.setUsername("owner");

        User other = new User();
        other.setUsername("other");

        Post post = new Post();
        post.setAuthor(owner);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(UnauthorizedException.class, () -> postService.deletePost(1L, other));
    }

    @Test
    void getPostsByUsername_shouldReturnPostsList() {
        List<Post> posts = List.of(new Post(), new Post());

        when(postRepository.findByAuthor_UsernameOrderByCreatedAtDesc("danie")).thenReturn(posts);

        List<Post> result = postService.getPostsByUsername("danie");

        assertEquals(2, result.size());
        verify(postRepository).findByAuthor_UsernameOrderByCreatedAtDesc("danie");
    }



    @Test
    void addComment_shouldSaveAndReturnComment() {
        Post post = new Post();
        User user = new User();
        String content = "Nice view!";

        Comment savedComment = new Comment();
        savedComment.setPost(post);
        savedComment.setAuthor(user);
        savedComment.setContent(content);

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Comment result = postService.addComment(post, content, user);

        assertEquals(content, result.getContent());
        assertEquals(post, result.getPost());
        assertEquals(user, result.getAuthor());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testGetAllPosts_ReturnsListOfPosts() {
        // Arrange
        Post post1 = new Post();
        post1.setId(1L);
        Post post2 = new Post();
        post2.setId(2L);
        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        // Act
        List<Post> result = postService.getAllPosts();

        // Assert
        assertEquals(2, result.size());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void testGetPostById_ExistingId_ReturnsPost() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        Optional<Post> result = postService.getPostById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPostById_NonExistingId_ReturnsEmptyOptional() {
        // Arrange
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Post> result = postService.getPostById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(postRepository, times(1)).findById(99L);
    }
}


