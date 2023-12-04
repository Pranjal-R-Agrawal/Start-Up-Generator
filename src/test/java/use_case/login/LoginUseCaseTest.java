package use_case.login;

import data_access.MongoDBDataAccessObject;

import data_access.MongoDBDataAccessObjectBuilder;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import use_case.login.application_business_rules.LoginInteractor;

import use_case.login.application_business_rules.LoginOutputBoundary;
import use_case.login.interface_adapter.LoginController;
import use_case.login.interface_adapter.LoginPresenter;

import view.LoginViewModel;
import view.SignupViewModel;
import view.ViewManagerModel;

public class LoginUseCaseTest {
    LoginViewModel loginViewModel;
    MongoDBDataAccessObject mongoDBDataAccessObject;
    LoginController loginController;

    @Test
    public void testAllFieldsEmpty() {
        loginController.execute(null, null);
        assert loginViewModel.getState().getErrorMessage().equals("Please enter a username.");
    }

    @Test
    public void testUsernameEmpty() {
        loginController.execute(null, "password");
        assert loginViewModel.getState().getErrorMessage().equals("Please enter a username.");
    }

    @Test
    public void testPasswordEmpty() {
        loginController.execute("username", null);
        assert loginViewModel.getState().getErrorMessage().equals("Please enter a password.");
    }

    @Test
    public void testInvalidCredentials() {
        loginController.execute("invalid", "credentials");
        assert loginViewModel.getState().getErrorMessage().equals("Invalid username or password.");
    }

    @Test
    public void testValidCredentials() {
        mongoDBDataAccessObject.addUser(new User(
                "username", "password", "name", "email", "phone", "", "")
        );
        loginController.execute("username", "password");
        assert loginViewModel.getState().getErrorMessage() == null;
    }

    @Before
    public void setUpTest() {
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        loginViewModel = new LoginViewModel();
        try {
            mongoDBDataAccessObject = new MongoDBDataAccessObjectBuilder().setTestParameters().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        LoginOutputBoundary loginPresenter = new LoginPresenter(viewManagerModel, loginViewModel, new SignupViewModel());
        loginController = new LoginController(
                new LoginInteractor(mongoDBDataAccessObject, loginPresenter), loginPresenter
        );

        mongoDBDataAccessObject.resetDatabase();
    }
}
