Feature: Client Adapter Integration Tests

#  Background: The following is an example test suite intended to execute at build-time.

  Scenario: Exercise the POST /parties/{idType}/{idValue} endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body "this is a bad request"
    Then send POST request with path "/parties/ACCOUNT_ID/11002" to application
    Then verify the HTTP response code is 405

  Scenario: Exercise the GET /parties/{idType}/{idValue} endpoint and verify the results
    Given base application url in system property "application-base-url"
    Then send GET request with path "/parties/ACCOUNT_ID/11002" to application
    Then verify the HTTP response code is 200
    Then verify the body consists the field "idType"
    Then verify the body consists the field "idValue"

  Scenario: Exercise the POST /quoterequests endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body "this is a bad request"
    Given HTTP header "Content-Type" = "text/plain"
    Then send POST request with path "/quoterequests" to application
    Then verify the HTTP response code is 500

  Scenario: Exercise the GET /quoterequests endpoint and verify the results
    Given base application url in system property "application-base-url"
    Then send GET request with path "/quoterequests" to application
    Then verify the HTTP response code is 405

  Scenario: Exercise the POST /quoterequests endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body
    """
    {
      "quoteId": "123",
      "transactionId": "456",
      "amount": 120.00,
      "currency": "USD"
    }
    """
    Given HTTP header "Content-Type" = "application/json"
    Then send POST request with path "/quoterequests" to application
    Then verify the HTTP response code is 200
    Then verify the body consists the field "quoteId"
    Then verify the body consists the field "transactionId"
    Then verify the body consists the field "transferAmount"
    Then verify the body consists the field "transferAmountCurrency"

  Scenario: Exercise the POST /transfers endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body "this is a bad request"
    Given HTTP header "Content-Type" = "text/plain"
    Then send POST request with path "/transfers" to application
    Then verify the HTTP response code is 500

  Scenario: Exercise the GET /transfers endpoint and verify the results
    Given base application url in system property "application-base-url"
    Then send GET request with path "/transfers" to application
    Then verify the HTTP response code is 405

  Scenario: Exercise the POST /transfers endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body
    """
    {
      "transferId": "123",
      "from":
      {
          "idType": "ACCOUNT_ID",
          "idValue": "1000065354113"
      },
      "to":
      {
          "idType": "ACCOUNT_ID",
          "idValue": "1000043574365"
      },
      "currency": "ETB",
      "amount": 100.00
    }
    """
    Given HTTP header "Content-Type" = "application/json"
    Then send POST request with path "/transfers" to application
    Then verify the HTTP response code is 200
    Then verify the body consists the field "homeTransactionId"
