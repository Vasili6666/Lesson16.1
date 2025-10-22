package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class ProfileOperationTests {

    String userName = "basil6";
    String password = "Basil1982!";

    @Test
    @DisplayName("Добавление книги в профиль и удаление через API (реальный ISBN)")
    void addAndDeleteBook() {

        // === 1. Авторизация ===
        Response loginResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body("{ \"userName\": \"" + userName + "\", \"password\": \"" + password + "\" }")
                        .log().all()
                        .when()
                        .post("https://demoqa.com/Account/v1/Login")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract().response();

        String token = loginResponse.path("token");
        String userId = loginResponse.path("userId");
        System.out.println("🔑 Token: " + token);
        System.out.println("👤 UserId: " + userId);

        // === 2. Получаем список всех книг и берём первый ISBN ===
        Response booksResponse =
                given()
                        .contentType(ContentType.JSON)
                        .log().all()
                        .when()
                        .get("https://demoqa.com/BookStore/v1/Books")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract().response();

        String isbn = booksResponse.jsonPath().getString("books[0].isbn");
        System.out.println("📚 Используем ISBN: " + isbn);

        // === 3. Добавление книги в профиль ===
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body("{ \"userId\": \"" + userId + "\", " +
                        "\"collectionOfIsbns\": [ { \"isbn\": \"" + isbn + "\" } ] }")
                .log().all()
                .when()
                .post("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().all()
                .statusCode(201);

        // === 4. Удаление книги из профиля ===
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body("{ \"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\" }")
                .log().all()
                .when()
                .delete("https://demoqa.com/BookStore/v1/Book")
                .then()
                .log().all()
                .statusCode(204);
    }
}
