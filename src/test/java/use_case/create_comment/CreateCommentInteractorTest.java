package use_case.create_comment;

import data_access.CreateCommentDataAccessInterface;
import data_access.MockCreatePostDataAccessObject;
import entity.CommentFactory;
import org.bson.types.ObjectId;
import org.junit.Test;
import use_case.create_comment.application_business_rules.CreateCommentInputBoundary;
import use_case.create_comment.application_business_rules.CreateCommentInputData;
import use_case.create_comment.application_business_rules.CreateCommentInteractor;
import use_case.create_comment.application_business_rules.CreateCommentOutputBoundary;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CreateCommentInteractorTest {
    @Test
    public void testBodyEmpty(){
        CreateCommentInputData inputData = new CreateCommentInputData(new ObjectId(), new ObjectId(), "", "testqualifications");
        CreateCommentDataAccessInterface dataAccessObject = new MockCreatePostDataAccessObject();

        CreateCommentOutputBoundary createPostPresenter = new CreateCommentOutputBoundary() {
            @Override
            public void prepareSuccessView(ObjectId commentId) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailureView(String error) {
                assertEquals(error, "Please enter text in body.");
            }
        };
        CreateCommentInputBoundary interactor = new CreateCommentInteractor(createPostPresenter, dataAccessObject, new CommentFactory());
        interactor.execute(inputData);
    }
    @Test
    public void testQualificationsEmpty(){
        CreateCommentInputData inputData = new CreateCommentInputData(new ObjectId(), new ObjectId(), "testbody", "");
        CreateCommentDataAccessInterface dataAccessObject = new MockCreatePostDataAccessObject();

        CreateCommentOutputBoundary createPostPresenter = new CreateCommentOutputBoundary() {
            @Override
            public void prepareSuccessView(ObjectId commentId) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailureView(String error) {
                assertEquals(error, "Please enter at least 1 qualification.");
            }
        };
        CreateCommentInputBoundary interactor = new CreateCommentInteractor(createPostPresenter, dataAccessObject, new CommentFactory());
        interactor.execute(inputData);
    }
    @Test
    public void testValidInput(){
        CreateCommentInputData inputData = new CreateCommentInputData(new ObjectId(), new ObjectId(), "testbody", "test;qualifications");
        CreateCommentDataAccessInterface dataAccessObject = new MockCreatePostDataAccessObject();

        CreateCommentOutputBoundary createPostPresenter = new CreateCommentOutputBoundary() {
            @Override
            public void prepareSuccessView(ObjectId commentId) {
                assertNotEquals(null, dataAccessObject.getCommentByCommentID(commentId));
                assertEquals("testbody", dataAccessObject.getCommentByCommentID(commentId).getBody());
                assertEquals(Arrays.asList("test", "qualifications"), dataAccessObject.getCommentByCommentID(commentId).getQualifications());

            }

            @Override
            public void prepareFailureView(String error) {
                fail("Use case failure is unexpected.");
            }
        };
        CreateCommentInputBoundary interactor = new CreateCommentInteractor(createPostPresenter, dataAccessObject, new CommentFactory());
        interactor.execute(inputData);
    }
}
