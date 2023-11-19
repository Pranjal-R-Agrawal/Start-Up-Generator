package use_case.comment.interface_adapter;

import org.bson.types.ObjectId;
import use_case.comment.application_business_rules.CommentInputBoundary;
import use_case.comment.application_business_rules.CommentInputData;

import java.util.List;

public class CommentController {
    final CommentInputBoundary commentInteractor;

    public CommentController(CommentInputBoundary commentInteractor){
        this.commentInteractor = commentInteractor;
    }

    public void execute(ObjectId parentId, ObjectId parentPostId, ObjectId authorId, String body, String qualifications){
        CommentInputData inputData = new CommentInputData(parentId, parentPostId, authorId, body, qualifications);
        commentInteractor.execute(inputData);
    }
}
