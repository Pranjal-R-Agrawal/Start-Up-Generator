package use_case.generate_idea.application_business_rules;

import api.GenerativeAIAPI;
import data_access.GenerateIdeaDataAccessInterface;
import entity.Idea;

public class GenerateIdeaInteractor implements GenerateIdeaInputBoundary{
    final GenerateIdeaDataAccessInterface generateIdeaDataAccessObject;
    final GenerateIdeaOutputBoundary generateIdeaPresenter;
    final GenerativeAIAPI generativeAIAPI;

    public GenerateIdeaInteractor(GenerateIdeaDataAccessInterface generateIdeaDataAccessObject, GenerateIdeaOutputBoundary generateIdeaPresenter, GenerativeAIAPI generativeAIAPI) {
        this.generateIdeaDataAccessObject = generateIdeaDataAccessObject;
        this.generateIdeaPresenter = generateIdeaPresenter;
        this.generativeAIAPI = generativeAIAPI;
    }

    public void execute() {
        Idea idea = null;

       try {
            idea = generateIdeaDataAccessObject.generateRandomIdea();
           String businessModel = generativeAIAPI.generateBusinessModel(idea);
           idea.setBusinessModel(businessModel);
           GenerateIdeaOutputData ideaOutputData = new GenerateIdeaOutputData(idea.getIdea(),idea.getBusinessModel());
          generateIdeaPresenter.prepareSuccessView(ideaOutputData);
       }catch (Exception e) {
           GenerateIdeaOutputData ideaOutputData = new GenerateIdeaOutputData(idea.getIdea(),"Unable to produce Business Model");
           generateIdeaPresenter.prepareSuccessView(ideaOutputData);
       }

        }
    }
