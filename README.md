\# Mini Netflix - Movie/Book Recommender



Mini Netflix is an \*\*API that provides movie and book recommendations\*\* based on each user's preferences, using a basic recommendation algorithm based on similar users.



---



\## 🚀 Features



\- Add \*\*ratings\*\* for movies or books.

\- Get \*\*personalized recommendations\*\* for each user.

\- Simple \*\*user-based recommendation algorithm\*\*.

\- Unit tests to validate ratings and recommendations.



---



\## 🛠 Technologies



\- \*\*Backend:\*\* Java, Spring Boot  

\- \*\*Database:\*\* MongoDB  

\- \*\*Cache:\*\* Redis  

\- \*\*Containers:\*\* Docker  



---



\## 🗂 Database Structure



\- \*\*users:\*\* user information (name, preferences, etc.)  

\- \*\*movies:\*\* catalog of movies or books  

\- \*\*ratings:\*\* ratings assigned by users



---



\## 🔗 Main Endpoints



\- \*\*Add rating\*\*  

&nbsp; `POST /ratings`  

&nbsp; Allows a user to add a rating for a movie or book.



\- \*\*Get recommendations\*\*  

&nbsp; `GET /recommendations/{userId}`  

&nbsp; Returns a list of recommended movies or books for the user.



---



\## ⚙ Installation \& Running



1\. Clone the repository:  

```bash

git clone https://github.com/your-username/mini-netflix.git

cd mini-Netflix

```

2\. Set up MongoDB and Redis (locally or via Docker).



3 .Build and run the application with Maven:



```bash

./mvnw clean install

./mvnw spring-boot:run

```



4\. Or run with Docker:

```bash

docker-compose up --build

```



🧪 Testing



The project includes unit tests to:



&nbsp;- Validate rating storage.



&nbsp;- Verify the recommendation logic based on similar users.



Run tests with Maven:



```bash

./mvnw test

```



📈 Recommendation Algorithm



Uses a simple user-based similarity algorithm, comparing user ratings to recommend movies or books enjoyed by users with similar tastes.



✨ Future Improvements

&nbsp;- Content-based recommendations (genre, author, director).



&nbsp;- User authentication and profiles.



&nbsp;- Web or mobile UI for interacting with the API.

