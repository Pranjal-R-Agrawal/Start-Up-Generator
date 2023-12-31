package data_access;
import entity.CollabRequest;
import entity.ConcreteCollabRequest;
import entity.Post;
import entity.User;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Represents a Data Access Object to access data from the database for the View Profile Use Case
 * @author Anbuselvan Ragunathan
 */
public interface ViewProfileDataAccessInterface {
    /**
     * Returns user from the database.
     * @return the user that is currently logged in
     */
    User getLoggedInUser();
    /**
     * Returns post from database
     * @return the post that corresponds to the id
     */
    Post getPostByPostID(ObjectId id);
    /**
     * Returns collab request from database
     * @return the collab request that corresponds to the id
     */
    CollabRequest getCollabRequestById(ObjectId id);
    /**
     * Add collab request to database
     */
    void addCollabRequest(ConcreteCollabRequest collabRequest);
    /**
     * Returns user from database
     * @return the post that corresponds to the username
     */
    User getUserByUsername(String username);

    List<Post> getPostByAuthorId(ObjectId id);
    List<ConcreteCollabRequest> getCollabRequestByUsername(String username);

    List<Post> getPostByCollaboratorId(ObjectId id);

}
