Feature: As a Amazon user I should be able to login and logout with valid credentials

	@login1
  Scenario: Login into the application with valid credentials
  	Given I am on the home page URL "https://www.google.com/"
    And I should see search button
    
  @login2
  Scenario: Login into the application with valid credentials
  	Given I am on the home page URL "https://www.google.co.in/"
    And I should see search button
    
    
    
   