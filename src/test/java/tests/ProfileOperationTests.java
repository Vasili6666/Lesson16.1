package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.AddBooksRequest;
import models.BookModel;
import models.DeleteBookRequest;
import models.LoginBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import specs.BookStoreSpecs;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ProfileOperationTests extends TestBase {

    String userName = "basil6";
    String password = "Basil1982!";

    @Test
    @DisplayName("Добавление книги в профиль и удаление через API (реальный ISBN)")
    void addAndDeleteBook() {

        // === 1. Авторизация ===
        LoginBodyModel loginBody = new LoginBodyModel(userName, password);

        Response loginResponse = given()
                .spec(BookStoreSpecs.loginRequestSpec())
                .body(loginBody)
                .when()
                .post("/Account/v1/Login")
                .then()
                .spec(BookStoreSpecs.universalResponseSpec())
                .extract().response();

        String token = loginResponse.path("token");
        String userId = loginResponse.path("userId");

        System.out.println("🔑 Token: " + token);
        System.out.println("👤 UserId: " + userId);

        // === 2. Получаем первую книгу ===
        BookModel firstBook = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/BookStore/v1/Books")
                .then()
                .spec(BookStoreSpecs.universalResponseSpec())
                .extract()
                .jsonPath()
                .getObject("books[0]", BookModel.class);

        System.out.println("📚 Используем книгу: " + firstBook.getTitle() + ", ISBN: " + firstBook.getIsbn());

        // === 3. Добавление книги в профиль через модель ===
        AddBooksRequest addBooksRequest = new AddBooksRequest(
                userId,
                List.of(new AddBooksRequest.Isbn(firstBook.getIsbn()))
        );

        given()
                .spec(BookStoreSpecs.authRequestSpec(token))
                .body(addBooksRequest)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .spec(BookStoreSpecs.universalResponseSpec());

        // === 4. Удаление книги через модель ===
        DeleteBookRequest deleteBookRequest = new DeleteBookRequest(firstBook.getIsbn(), userId);

        given()
                .spec(BookStoreSpecs.authRequestSpec(token))
                .body(deleteBookRequest)
                .when()
                .delete("/BookStore/v1/Book")
                .then()
                .spec(BookStoreSpecs.universalResponseSpec());
    }
}
