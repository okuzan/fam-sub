# Family Subscription Manager 🏠💰

Welcome to the Family Subscription Manager! This is a fantastic Java Spring Boot application that helps manage and track family subscriptions and payments. It's a great tool for keeping everyone in the loop about their balances and upcoming payments. 

## Features 🚀

- **Track Balances**: Keep an eye on who owes what with our balance tracking feature. It's never been easier to stay on top of debts and contributions.
- **Seasonal Updates**: Our application works on a seasonal basis, updating balances and payments according to the time of year. 🌸☀️🍂❄️
- **Pinned Posts**: Stay informed with our pinned posts feature. It provides a quick and easy way to check balances and payment methods.
- **Emoji Support**: Who said finance has to be boring? Our application uses emojis to make tracking payments fun and engaging. 😄💸

## Tech Stack 🛠️

- **Java**: The application is written in Java, a class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.
- **Spring Boot**: This project uses Spring Boot, an open-source Java-based framework used to create stand-alone, production-grade Spring-based Applications.
- **Maven**: Maven is used as the project management and comprehension tool. It provides a complete build lifecycle framework to the developer.
- **PostgreSQL**: The application uses PostgreSQL as the relational database to store all the data.

## Getting Started 🚦

To get started with the Family Subscription Manager, you'll need to have Java and Maven installed on your machine. 

1. Clone the repository to your local machine.
2. Navigate to the root directory of the project in your terminal.
3. Run the application using the command `mvn spring-boot:run`.

## Configuration ⚙️

The application can be configured using the `application.properties` file located in `src/main/resources`. Here you can set the database connection details and other application-specific configurations.

## Contribute 🤝

We welcome contributions to the Family Subscription Manager! If you have a feature request, bug report, or want to contribute to the code, please feel free to open an issue or make a pull request.

## License 📄

This project is licensed under the MIT License. See the LICENSE file for more details.

Join us in making family subscriptions fun and easy to manage! 🎉

Algo of how to drop the seasonal bills:
1. Parse charged bills
2. Calculate current season bills for all users
3. Update balances