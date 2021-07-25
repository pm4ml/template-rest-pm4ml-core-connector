Feature: Core Connector Integration Tests

#  Background: The following is an example test suite intended to execute at build-time.

  Scenario: Exercise the POST /parties/{idType}/{idValue} endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body "this is a bad request"
    Then send POST request with path "/parties/ACCOUNT_ID/001421431" to application
    Then verify the HTTP response code is 405

  Scenario: Exercise the GET /parties/{idType}/{idValue} endpoint and verify the results
    Given base application url in system property "application-base-url"
    Then send GET request with path "/parties/ACCOUNT_ID/001421431" to application
    Then verify the HTTP response code is 200
    Then verify the body consists the field "idType"
    Then verify the body consists the field "idValue"

  Scenario: Exercise the POST /quoterequests endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body "this is a bad request"
    Given HTTP header "Content-Type" = "text/plain"
    Then send POST request with path "/quoterequests" to application
    Then verify the HTTP response code is 415

  Scenario: Exercise the GET /quoterequests endpoint and verify the results
    Given base application url in system property "application-base-url"
    Then send GET request with path "/quoterequests" to application
    Then verify the HTTP response code is 405

  Scenario: Exercise the POST /quoterequests endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body
    """
    {
        "quoteId": "a4c92ea3-1716-4320-99e7-8e0bb965ce71",
        "transactionId": "d5cf47b1-4928-1920-83b2-2b1cc827ca92",
        "from":
        {
            "idType": "ACCOUNT_ID",
            "idValue": "001421431"
        },
        "to":
        {
            "idType": "ACCOUNT_ID",
            "idValue": "001421431"
        },
        "amountType": "SEND",
        "amount": 100.01,
        "currency": "MMK",
        "transactionType": "TRANSFER",
        "initiator": "PAYER",
        "initiatorType": "CONSUMER"
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
    Then verify the HTTP response code is 415

  Scenario: Exercise the GET /transfers endpoint and verify the results
    Given base application url in system property "application-base-url"
    Then send GET request with path "/transfers" to application
    Then verify the HTTP response code is 405

  Scenario: Exercise the POST /transfers endpoint and verify the results
    Given base application url in system property "application-base-url"
    Given POST body
    """
    {
      "transferId": "d5cf47b1-4928-1920-83b2-2b1cc827ca34",
      "from":
      {
          "idType": "ACCOUNT_ID",
          "idValue": "001421431"
      },
      "to":
      {
          "idType": "ACCOUNT_ID",
          "idValue": "001421431"
      },
      "currency": "MMK",
      "amount": 10
    }
    """
    Given HTTP header "Content-Type" = "application/json"
    Then send POST request with path "/transfers" to application
    Then verify the HTTP response code is 201
    Then verify the body consists the field "homeTransactionId"
