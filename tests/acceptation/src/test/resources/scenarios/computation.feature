Feature: Tax Computation

  Background:
    Given The TCS service deployed on localhost:9090

  Scenario: Paying taxes using the "simple" method

    Given a taxpayer identified as 111-555-111
      And an income of 12000 kroner
    When the simple computation method is selected
      And the service is called
    Then the computed tax amount is 2400.0
      And the answer is associated to 111-555-111
      And the computation date is set


  Scenario: Paying taxes using the "complex" method in urban area

    Given a taxpayer identified as 111-555-222
      And living in the following area: 55543
      And an income of 12000 kroner
      And an assets value of 42000 kroner
    When the complex computation method is selected
      And the service is called
    Then the computed tax amount is 7440.0
      And the answer is associated to 111-555-222
      And the computation date is set

  Scenario: Paying taxes using the "complex" method in rural area

    Given a taxpayer identified as 111-555-333
      And living in the following area: 15555
      And an income of 12000 kroner
      And an assets value of 42000 kroner
    When the complex computation method is selected
      And the service is called
    Then the computed tax amount is 6360.0
      And the answer is associated to 111-555-333
      And the computation date is set