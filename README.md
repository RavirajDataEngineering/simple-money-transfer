# simple-money-transfer
Simple Money Transfer RESTful API . Threadsafe implentation that  supports transfers across multiple accounts  
using inmemory database. Functional tests included that cover all test cases including concurrent transfer across overlapping  
accounts.

## Running Instructions.

1. mvn clean install    
2. Java -jar target/SimpleMoneyTransferjar.jar

## API's Supported.

- GET Account  
   http://localhost:5555/moneytransfer/accounts/{accountId}  ex: http;//localhost:5555/accounts/1
   
- POST request to create account  
   http://localhost:5555/moneytransfer/accounts 
   payload : {"accountId":1,"userId":"raviraj","balance":100}  
- DELETE Account  
   http://localhost:5555/moneytransfer/accounts/{accountId}  
- PUT ( update account)  
   http://localhost:5555/moneytransfer/accounts  
   payload : {"accountId":1,"userId":"rashmi","balance":100}  
- TRANSFER MONEY ACROSS ACCOUNTS  
   http://localhost:5555/moneytransfer/transfer  
   payload : {"debitAccountId":2,"creditAccountId":1,"amount":10}  
- GET transaction by id  
    http://localhost:5555/moneytransfer/transactions/{transactionId}  
- CREATE (POST) a transaction  
    http://localhost:5555/moneytransfer/transactions  
    payload: {"creditAccountId":1,"debitAccountId":2,"transactionId":1,"amount":10}  
- DELETE a transaction  
   http://localhost:5555/moneytransfer/transactions/{tranactionId}

## Http status
- 200 OK
- 400 Bad request
- 404 Not found
- 500 Internal Server Error

##Notes
- Ignoring different currenices across multiple accounts. assuming all accounts of same currency.

