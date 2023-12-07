package entity;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * An interface for a factory to create Comment objects
 * @author Yathusan Koneswararajah
 */
public interface CommentFactoryInterface {
    /**
     * Creates and returns a Comment object
     * @param parentId ID of parent comment
     * @param parentPostId of parent post
     * @param authorId ID of the author of the post
     * @param body Body of the post
     * @param qualifications Qualifications listed by commenter
     * @return a Comment object
     */
    public CommentInterface create(ObjectId parentId, ObjectId parentPostId, ObjectId authorId, String body, List<String> qualifications);
}
