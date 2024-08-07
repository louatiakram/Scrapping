# Scrapping

Scrapping is a Spring Boot project designed to scrape computer data from Tunisian stores like Tunisianet.

## Features

- Scrapes computer information from Mytek and Tunisianet websites.
- Stores scraped data in a PostgreSQL database.
- Provides an API to retrieve the scraped computer data (Test with Postman).

## Prerequisites

- Java 11 or higher
- Maven 3.6.3 or higher
- PostgreSQL

## Installation

1. **Clone the repository:**

    ```bash
   git clone https://github.com/louatiakram/Scrapping.git
   cd Scrapping

2. **Create a PostgreSQL database named scrapping and update the src/main/resources/application.properties file with your
   database credentials.**

    ```bash
   spring.datasource.url=jdbc:postgresql://localhost:5432/scrapping

3. **Run ScrappingApplication, Enjoy!**
