package data_access;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import entity.*;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.swing.*;


public class MongoDBDataAccessObject implements SignupUserDataAccessInterface, LoginUserDataAccessInterface, CreateCommentDataAccessInterface, DisplayCommentDataAccessInterface, CreatePostDataAccessInterface, ViewUserInfoDataAccessInterface ,SearchPostsByTitleDataAccessInterface,CollabRequestDataAccessInterface, ViewProfileDataAccessInterface {
    private final MongoDatabase database;
    protected MongoCollection<User> users;
    protected MongoCollection<Post> posts;
    protected MongoCollection<Comment> comments;
    protected MongoCollection<ConcreteCollabRequest> collabRequests ;
    private ObjectId loggedInUserID;
    private final String usersCollectionName;
    private final String postsCollectionName;
    private final String commentsCollectionName;
    private final String collabRequestsCollectionName;

    public MongoDBDataAccessObject(
            String databaseConnectionPath, String databaseName, String usersCollectionName, String postsCollectionName, String commentsCollectionName, String collabRequestsCollectionName
    ) throws FileNotFoundException, NoSuchElementException {
        String uri;
        this.usersCollectionName = usersCollectionName;
        this.postsCollectionName = postsCollectionName;
        this.commentsCollectionName = commentsCollectionName;
        this.collabRequestsCollectionName = collabRequestsCollectionName;
        try {
            File databaseConnection = new File(databaseConnectionPath);
            Scanner scanner = new Scanner(databaseConnection);
            uri = scanner.nextLine();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not find database connection file" + System.lineSeparator() + e.getMessage());
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Database connection file is empty" + System.lineSeparator() + e.getMessage());
        }
        try {
            MongoClient mongoClient = MongoClients.create(uri);

            database = mongoClient.getDatabase(databaseName);

            List<String> collections = database.listCollectionNames().into(new java.util.ArrayList<>());

            if (!collections.contains(usersCollectionName)) database.createCollection(usersCollectionName);
            users = getUsersCollection();

            if (!collections.contains(postsCollectionName)) database.createCollection(postsCollectionName);
            posts = getPostsCollection();

            if (!collections.contains(commentsCollectionName)) database.createCollection(commentsCollectionName);
            comments = getCommentsCollection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not connect to the database\nError: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private MongoCollection<User> getUsersCollection() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        return database.getCollection(usersCollectionName, User.class).withCodecRegistry(pojoCodecRegistry);
    }

    private MongoCollection<Post> getPostsCollection() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        return database.getCollection(postsCollectionName, Post.class).withCodecRegistry(pojoCodecRegistry);
    }

    private MongoCollection<Comment> getCommentsCollection() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        return database.getCollection(commentsCollectionName, Comment.class).withCodecRegistry(pojoCodecRegistry);
    }
    private MongoCollection<ConcreteCollabRequest> getCollabRequests() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        return database.getCollection(collabRequestsCollectionName, ConcreteCollabRequest.class).withCodecRegistry(pojoCodecRegistry);

    }

    public void resetDatabase() {
        users.deleteMany(new Document());
        posts.deleteMany(new Document());
        comments.deleteMany(new Document());
    }

    public boolean usernameUsed(String username) {
        users = getUsersCollection();
        users.createIndex(Indexes.text("username"));
        for(User user : users.find(Filters.text(username))) return true;
        return false;
    }

    public void addUser(UserInterface user) {
        users = getUsersCollection();
        users.insertOne((User)user);
    }

    public boolean isValid(String username, String password) {
        users = getUsersCollection();
        users.createIndex(Indexes.text("username"));
        for (User user : users.find(Filters.text(username))) {
            if (user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        users = getUsersCollection();
        users.createIndex(Indexes.text("username"));
        return users.find(Filters.text(username)).first();
    }

    @Override
    public List<Post> getPostByAuthorId(ObjectId id) {
        posts = getPostsCollection();
        // posts.createIndex(Indexes.text("authorID"));
        return posts.find(Filters.eq("authorID", id)).into(new ArrayList<Post>());
    }

    @Override
    public List<ConcreteCollabRequest> getCollabRequestByUsername(String username) {
        collabRequests = getCollabRequests();
        collabRequests.createIndex(Indexes.text("username"));
        return collabRequests.find(Filters.eq("username", username)).into(new ArrayList<ConcreteCollabRequest>());
    }

    @Override
    public Post getPostByPostId(ObjectId id) {
        posts = getPostsCollection();
        return posts.find(Filters.eq("_id", id)).first();
    }

    public User getUserById(ObjectId id) {
        users = getUsersCollection();
        return users.find(Filters.eq("_id", id)).first();
    }
    @Override
    public void addCollabRequest(CollabRequest collabRequest) {
        posts = getPostsCollection();
        collabRequests = getCollabRequests();
        collabRequests.insertOne((ConcreteCollabRequest) collabRequest);
        ObjectId postId = collabRequest.getPostId();
        ObjectId collaboratorId = getUserByUsername(collabRequest.getCommenter()).getId();
        Bson filter = Filters.eq("_id", postId);
        Bson update = Updates.push("collaboratorIDs", collaboratorId);
        posts.updateOne(filter, update);
    }

    @Override
    public void addCollabRequest(ConcreteCollabRequest collabRequest) {
        collabRequests = getCollabRequests();
        collabRequests.insertOne(collabRequest);

    }

    /**
     * Void Implementation of the drop function from the interface
     */
    public void drop(){

    }

    public void setLoggedInUserID(ObjectId id) {
        loggedInUserID = id;
    }

    public User getLoggedInUser() {
        users = getUsersCollection();
        return users.find(Filters.eq("_id", loggedInUserID)).first();
    }

    public List<Comment> getCommentsByParentPostID(ObjectId id) {
        comments = getCommentsCollection();
        comments.createIndex(Indexes.text("parentPostId"));
        return comments.find(Filters.eq("parentPostId", id)).into(new ArrayList<Comment>());
    }

    public Comment getCommentByCommentID(ObjectId id) {
        comments = getCommentsCollection();
        return comments.find(Filters.eq("_id", id)).first();
    }

    public Post getPostByPostID(ObjectId id) {
        posts = getPostsCollection();
        return posts.find(Filters.eq("_id", id)).first();
    }

    @Override
    public CollabRequest getCollabRequestById(ObjectId id) {
        collabRequests = getCollabRequests();
        return collabRequests.find(Filters.eq("_id", id)).first();
    }

    @Override
    public void addComment(Comment comment) {
        comments = getCommentsCollection();
        comments.insertOne(comment);
    }

    public void addPost(Post post) {
        posts = getPostsCollection();
        posts.insertOne(post);
    }

    @Override
    public ObjectId getLoggedInUserId() {
        return loggedInUserID;
    }

    @Override
    public List<PostSearchResultsInterface> getPostsByTitle(String query) {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        MongoCollection<PostSearchResults> postSearchResultsMongoCollection = database.getCollection(postsCollectionName, PostSearchResults.class).withCodecRegistry(pojoCodecRegistry);

        AggregateIterable<PostSearchResults> searchResults = postSearchResultsMongoCollection.aggregate(Arrays.asList(new Document("$search",
                        new Document("index", "posts")
                                .append("text",
                                        new Document("query", query)
                                                .append("path", "title")
                                                .append("fuzzy",
                                                        new Document()))),
                new Document("$project",
                        new Document("_id", 1L)
                                .append("title", 1L)
                                .append("score",
                                        new Document("$meta", "searchScore"))))
        );

        return searchResults.into(new ArrayList<PostSearchResultsInterface>());
    }

    public List<Post> getPostByCollaboratorId(ObjectId id) {
        posts = getPostsCollection();
        // posts.createIndex(Indexes.text("collaboratorIDs"));
        return posts.find(Filters.in("collaboratorIDs", id)).into(new ArrayList<Post>());
    }
}
