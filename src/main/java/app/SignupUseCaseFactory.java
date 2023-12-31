package app;

import data_access.SignupUserDataAccessInterface;
import use_case.signup.application_business_rules.*;
import use_case.signup.interface_adapter.SignupController;
import use_case.signup.interface_adapter.SignupPresenter;
import use_case.login.interface_adapter.LoginViewModel;
import view.SignupView;
import use_case.signup.interface_adapter.SignupViewModel;
import view.ViewManagerModel;

public class SignupUseCaseFactory {
    private SignupUseCaseFactory() {}

    /**
     * Creates and returns a SignupView object
     * @param viewManagerModel the view manager model
     * @param signupViewModel the signup view model
     * @param loginViewModel the login view model
     * @param signupUserDataAccessObject the signup user data access object
     * @return a SignupView object
     */
    public static SignupView create(ViewManagerModel viewManagerModel, SignupViewModel signupViewModel, LoginViewModel loginViewModel, SignupUserDataAccessInterface signupUserDataAccessObject) {
        SignupController signupController = createUserSignupUseCase(viewManagerModel, signupViewModel, loginViewModel, signupUserDataAccessObject);
        return new SignupView(signupViewModel, signupController);
    }

    private static SignupController createUserSignupUseCase(ViewManagerModel viewManagerModel, SignupViewModel signupViewModel, LoginViewModel loginViewModel, SignupUserDataAccessInterface signupUserDataAccessObject) {
        SignupOutputBoundary signupPresenter = new SignupPresenter(viewManagerModel, signupViewModel, loginViewModel);
        SignupInputBoundary signupInteractor = new SignupInteractor(signupUserDataAccessObject, signupPresenter);
        return new SignupController(signupInteractor, signupPresenter);
    }
}