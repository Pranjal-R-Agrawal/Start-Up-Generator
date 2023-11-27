package data_access;
import entity.Comment;
import entity.User;
import entity.Post;
import org.bson.types.ObjectId;
public interface CollabRequestDataAccessInterface {
    Comment getCommentByCommentId(ObjectId id);
    User getUserById(ObjectId id);
    Post getPostByPostId(ObjectId id);
    void setCollaborationRequestId(ObjectId id);
}
