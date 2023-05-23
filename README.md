# E-Wallet Application
This is an E-Wallet application designed to provide digital wallet functionality to customers. It allows users to create accounts, manage wallets, perform transactions, and request unblocking of their accounts if blocked.

## Functionality
The E-Wallet application provides the following functionality:

Account Management: Users can create new accounts by registering with their personal information, including name, surname, email, and password.

Wallet Creation: After registering, users can create wallets associated with their accounts. A wallet represents a digital container for storing funds.

Wallet Management: Users can perform various operations on their wallets, such as updating wallet information, deleting wallets, and checking wallet balances.

Deposit: Users can deposit funds into their wallets by specifying the wallet ID and the amount to be deposited.

Withdrawal: Users can withdraw funds from their wallets by specifying the wallet ID, customer ID, and the amount to be withdrawn. Withdrawals are subject to daily withdrawal limits.

Transfer: Users can transfer funds from their wallets to another customer's wallet. To initiate a transfer, the sender must specify their customer ID, sender wallet ID, receiver customer ID, receiver wallet ID, and the amount to be transferred. Transfers are subject to the sender's wallet balance and transaction limits.

Account Blocking: If a customer violates certain transaction or security rules, their account may be automatically blocked. Blocked customers cannot perform transactions until their accounts are unblocked.

Account Unblocking: Customers can request the unblocking of their accounts if they are blocked. They can submit an unblocking request by providing their customer ID. Once the request is submitted, it will be processed by the system.

## How It Works
The E-Wallet application is built using Java and Spring Boot. It follows a microservice architecture and utilizes several components:

Customer Service: Manages customer-related operations such as account creation, deletion, blocking, and unblocking.

Wallet Service: Handles wallet-related operations such as wallet creation, deletion, balance management, and fund transfers.

Transaction Service: Handles transaction-related operations such as deposit, withdrawal, and transfer processing.

Repositories: Provide data access and persistence for customers, wallets, and transactions.

Controllers: Expose RESTful endpoints for interacting with the application, allowing users to perform actions such as account registration, wallet management, and transaction processing.

The application uses a relational database to store customer, wallet, and transaction data. The database schema includes tables for customers, wallets, and transactions, with appropriate relationships and constraints.

To use the E-Wallet application, users can interact with the provided RESTful endpoints through API calls. They can register new accounts, create wallets, perform transactions, and manage their account and wallet details.

Please refer to the documentation provided in the source code for more detailed information on the available API endpoints and their usage.

## Getting Started
To run the E-Wallet application locally, follow these steps:

Clone the repository:
```bash
git clone https://github.com/grmskyi/e-wallet-app.git
```
Set up the necessary environment variables, such as the database connection details, in the application properties file (application.properties).

Build the application using a build tool such as Maven or Gradle:

```bash
mvn clean install
```
Run the application:
```bash
mvn spring-boot:run
```
The application should now be running locally, and you can interact with it by making API requests to the provided endpoints.
## Test cases:
* A customer should be able to get a wallet after providing his/her personal details:
        * Registration of new user:
        ![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/cf592885-ab3d-43f2-b43e-59bbb71a1c1d)
* A customer should be able to withdraw and deposit funds:
        * Daily withdrawal limit is EUR:
        ![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/b88f53fb-809b-4839-91db-bdaf387ad8b3)
        ![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/f9e8cba9-fb68-4420-a20b-54200894fecb)
        * Deposit funds:
        ![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/f1f89b10-dd6a-44fa-8126-4abfca85c2c8)
        ![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/68cd8c0c-7fab-41a7-915f-dd22f37d7e9c)
* Transactions over 10'000 should be flagged as suspicious:
![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/12a5ed99-7814-45ed-b603-4dd3a94ac0c0)
* Single transaction limit is 2000:
![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/99081c77-1bdb-4107-ab7d-89a633dee3df)
* If a customer makes more than 5 transactions within an hour he/she should be flagged as suspicious:
![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/8a124fff-3ee1-4629-a43c-9fa21153c3b2)
* If a customer makes 10 transactions within an hour he/she should be blocked from making any further transactions until the next working day unless the customer submits a reset request:
![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/e4300213-0e1e-497e-8ffd-d774f7dc446e)
![image](https://github.com/grmskyi/e-wallet-app/assets/74506286/9243d164-6bda-4f97-bac8-8f8000c8ec38)

## Conclusion
The E-Wallet application offers a convenient and secure way for customers to manage their digital wallets and perform various financial transactions. It provides flexibility and ease of use while maintaining the necessary security measures to protect customer funds. With its modular architecture and RESTful API, it can be easily extended and integrated into other systems as needed.
