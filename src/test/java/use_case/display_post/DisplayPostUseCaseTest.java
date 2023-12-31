package use_case.display_post;

import entity.Comment;
import entity.Post;
import entity.User;
import org.bson.types.ObjectId;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import use_case.display_post.application_business_rules.DisplayPostInteractor;
import use_case.display_post.application_business_rules.DisplayPostOutputBoundary;
import use_case.display_post.interface_adapter.DisplayPostController;
import use_case.display_post.interface_adapter.DisplayPostPresenter;
import use_case.display_post.interface_adapter.PostAndCommentsViewModel;
import data_access.MongoDBDataAccessObject;
import data_access.MongoDBDataAccessObjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class DisplayPostUseCaseTest {
    PostAndCommentsViewModel postAndCommentsViewModel;
    MongoDBDataAccessObject mongoDBDataAccessObject;
    DisplayPostController displayPostController;

    @Test
    public void testInvalidCommentID() {
        ObjectId commentID = new ObjectId();
        displayPostController.execute(commentID, "comment");
        assertEquals(postAndCommentsViewModel.getState().getErrorMessage(), "Comment not found");
    }

    @Test
    public void testInvalidPostID() {
        ObjectId postID = new ObjectId();
        displayPostController.execute(postID, "post");
        assertEquals(postAndCommentsViewModel.getState().getErrorMessage(), "Post not found");
    }

    @Test
    public void testValidCommentID() {
        User user = new User("username", "password", "name", "email", "phone", "city", "field");
        mongoDBDataAccessObject.addUser(user);

        Post post = new Post(user.getId(), "title", "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addPost(post);

        Comment comment = new Comment(post.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment);

        ObjectId commentID = comment.getId();

        displayPostController.execute(commentID, "comment");

        assertEquals(postAndCommentsViewModel.getState().getComments().size(), 1);
    }

    @Test
    public void testValidPostID_1_comment() {
        User user = new User("username", "password", "name", "email", "phone", "city", "field");
        mongoDBDataAccessObject.addUser(user);

        Post post = new Post(user.getId(), "title", "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addPost(post);

        Comment comment = new Comment(post.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment);

        ObjectId postID = post.getId();

        displayPostController.execute(postID, "post");

        assertEquals(postAndCommentsViewModel.getState().getComments().size(), 1);
    }

    @Test
    public void testValidPostID_3_comment() {
        User user = new User("username", "password", "name", "email", "phone", "city", "field");
        mongoDBDataAccessObject.addUser(user);

        Post post = new Post(user.getId(), "title", "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addPost(post);

        Comment comment = new Comment(post.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment);

        Comment comment2 = new Comment(post.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment2);

        Comment comment3 = new Comment(post.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment3);

        ObjectId postID = post.getId();

        displayPostController.execute(postID, "post");

        assertEquals(postAndCommentsViewModel.getState().getComments().size(), 3);
    }

    @Test
    public void testValidPostID_nested() {
        User user = new User("username", "password", "name", "email", "phone", "city", "field");
        mongoDBDataAccessObject.addUser(user);

        Post post = new Post(user.getId(), "title", "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addPost(post);

        Comment comment = new Comment(post.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment);

        Comment comment2 = new Comment(comment.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment2);

        Comment comment3 = new Comment(comment2.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment3);

        ObjectId postID = post.getId();

        displayPostController.execute(postID, "post");

        assertEquals(postAndCommentsViewModel.getState().getComments().size(), 3);
    }

    @Test
    public void testProcessedCommentData() {
        User user = new User("username", "password", "name", "email", "phone", "city", "field");
        mongoDBDataAccessObject.addUser(user);

        Post post = new Post(user.getId(), "title", "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addPost(post);

        Comment comment = new Comment(post.getId(), post.getId(), user.getId(), "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addComment(comment);

        mongoDBDataAccessObject.setLoggedInUserID(user.getId());

        ObjectId postId = post.getId();
        ObjectId commentID = comment.getId();

        displayPostController.execute(commentID, "comment");

        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("id"), commentID);
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("parentPostId"), postId);
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("parentId"), postId);
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("authorId"), user.getId());
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("username"), user.getUsername());
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("body"), "body");
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("qualifications"), new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("comment_author_is_post_author"), true);
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("logged_in_user_is_comment_author"), true);
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("logged_in_user_is_post_author"), true);
        assertEquals(postAndCommentsViewModel.getState().getComments().get(commentID).get("show_more_info_button"), true);
    }

    @Test
    public void testProcessedPostData() {
        User user = new User("username", "password", "name", "email", "phone", "city", "field");
        mongoDBDataAccessObject.addUser(user);

        Post post = new Post(user.getId(), "title", "body", new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        mongoDBDataAccessObject.addPost(post);

        mongoDBDataAccessObject.setLoggedInUserID(user.getId());

        ObjectId postId = post.getId();

        displayPostController.execute(postId, "post");

        assertEquals(postAndCommentsViewModel.getState().getPost().get("id"), postId);
        assertEquals(postAndCommentsViewModel.getState().getPost().get("authorId"), user.getId());
        assertEquals(postAndCommentsViewModel.getState().getPost().get("username"), user.getUsername());
        assertEquals(postAndCommentsViewModel.getState().getPost().get("title"), "title");
        assertEquals(postAndCommentsViewModel.getState().getPost().get("body"), "body");
        assertEquals(postAndCommentsViewModel.getState().getPost().get("suggested_collaborator_qualifications"), new ArrayList<String>(Arrays.asList("qual1", "qual2")));
        assertEquals(postAndCommentsViewModel.getState().getPost().get("logged_in_user_is_post_author"), true);
    }

    @Before
    public void setUpTest() {
        try {
            mongoDBDataAccessObject = new MongoDBDataAccessObjectBuilder().setTestParameters().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        postAndCommentsViewModel = new PostAndCommentsViewModel();
        DisplayPostOutputBoundary displayCommentPresenter = new DisplayPostPresenter(postAndCommentsViewModel);
        DisplayPostInteractor displayPostInteractor = new DisplayPostInteractor(mongoDBDataAccessObject, displayCommentPresenter);
        displayPostController = new DisplayPostController(displayPostInteractor);
        mongoDBDataAccessObject.resetDatabase();
    }
}
