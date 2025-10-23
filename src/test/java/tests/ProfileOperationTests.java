package tests;

import io.restassured.response.Response;
import models.AddBooksRequest;
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
    String isbn = "9781449325862"; // конкретная книга Git Pocket Guide

    @Test
    @DisplayName("Добавление и удаление конкретной книги (Git Pocket Guide)")
    void addAndDeleteSpecificBook() {

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

        // === 2. Добавление книги в профиль ===
        AddBooksRequest addBooksRequest = new AddBooksRequest(
                userId,
                List.of(new AddBooksRequest.Isbn(isbn))
        );

        given()
                .spec(BookStoreSpecs.authRequestSpec(token))
                .body(addBooksRequest)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .spec(BookStoreSpecs.universalResponseSpec());

        // === 3. Удаление книги ===
        DeleteBookRequest deleteBookRequest = new DeleteBookRequest(isbn, userId);

        given()
                .spec(BookStoreSpecs.authRequestSpec(token))
                .body(deleteBookRequest)
                .when()
                .delete("/BookStore/v1/Book")
                .then()
                .spec(BookStoreSpecs.universalResponseSpec());
    }
}
