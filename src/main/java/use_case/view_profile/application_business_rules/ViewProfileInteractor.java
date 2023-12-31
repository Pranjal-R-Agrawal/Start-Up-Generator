package use_case.view_profile.application_business_rules;

import data_access.ViewProfileDataAccessInterface;
import java.util.List;

import entity.Post; // To get the needed Posts for collab requests?

/**
 * Concrete implementation of ViweProfileInputBoundary responsible for structuring the things that will be displayed in the view and passing it to the output boundary
 * @author Anbuselvan Raguunathan
 * */
public class ViewProfileInteractor implements ViewProfileInputBoundary {

    final ViewProfileDataAccessInterface viewProfileDataAccessObject;

    final ViewProfileOutputBoundary viewProfilePresenter;

    /**
     * Initializes the ViewProfileInteractor object to perform the view profile logic
     * @param viewProfileDataAccessObject Helps us interact appropriately with the Database
     * @param viewProfilePresenter Receives the output data from ViewProfileInteractor to facilitate with the display of the output data
     */
    public ViewProfileInteractor(ViewProfileDataAccessInterface viewProfileDataAccessObject, ViewProfileOutputBoundary viewProfilePresenter) {
        this.viewProfileDataAccessObject = viewProfileDataAccessObject;
        this.viewProfilePresenter = viewProfilePresenter;
    }
    @Override
    public void execute() {

        // Structures the things that will need to be displayed in the view
        String username = viewProfileDataAccessObject.getLoggedInUser().getUsername();
        String name = viewProfileDataAccessObject.getLoggedInUser().getName();
        String email = viewProfileDataAccessObject.getLoggedInUser().getEmail();

        String projects = "";
        List<Post> projectIds = viewProfileDataAccessObject.getPostByAuthorId(viewProfileDataAccessObject.getLoggedInUser().getId());
        int i = 1;
        for (Post post : projectIds) {
            String postTitle = post.getTitle();
            projects += "<p></p><p>" + (i++) + ". " + postTitle + "</p>";
        }
        String collab = "";
        List<Post> collabProjects = viewProfileDataAccessObject.getPostByCollaboratorId(viewProfileDataAccessObject.getLoggedInUser().getId());
        i = 1;
        for (Post post : collabProjects) {
            String postTitle = post.getTitle();
            collab += "<p></p><p>" + (i++) + ". " + postTitle + "</p>";
        }
//        List<ObjectId> collabRequestIds = viewProfileDataAccessObject.getLoggedInUser().getCollaborationRequestIDs();
//        ArrayList<String> collabRequests = new ArrayList<>();
//        for (ObjectId collabRequestId : collabRequestIds) {
//            String collabRequest = viewProfileDataAccessObject.getCollabRequestById(collabRequestId).getTitle();
//
//            collabRequests.add(collabRequest);
//        }

        ViewProfileOutputData viewProfileOutputData = new ViewProfileOutputData(username, name, email, projects, null, collab);
        viewProfilePresenter.prepareSuccessView(viewProfileOutputData);

        }

}
