# simple-money-transfer
Simple Money Transfer RESTful API

Running Instructions.

1 . mvn clean install

2. Java -jar target/SimpleMoneyTransferjar.jar

API's Supported.

1. GET Account  
   http://localhost:5555/moneytransfer/accounts/{accountId}  ex: http;//localhost:5555/accounts/1
   
2. POST request to create account  
   http://localhost:5555/moneytransfer/accounts 
   payload : {"accountId":1,"userId":"raviraj","balance":100}  
3. DELETE Account  
   http://localhost:5555/moneytransfer/accounts/{accountId}  
4. PUT ( update account)  
   http://localhost:5555/moneytransfer/accounts  
   payload : {"accountId":1,"userId":"rashmi","balance":100}  
5. TRANSFER MONEY ACROSS ACCOUNTS  
   http://localhost:5555/moneytransfer/transfer  
   payload : {"debitAccountId":2,"creditAccountId":1,"amount":10}  
6. GET transaction by id  
    http://localhost:5555/moneytransfer/transactions/{transactionId}  
7. CREATE (POST) a transaction  
    http://localhost:5555/moneytransfer/transactions  
    payload: {"creditAccountId":1,"debitAccountId":2,"transactionId":1,"amount":10}  
8. DELETE a transaction  
   http://localhost:5555/moneytransfer/transactions/{tranactionId}
