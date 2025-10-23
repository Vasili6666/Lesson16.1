package tests;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import models.AddBooksRequest;
import models.DeleteBookRequest;
import models.LoginBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import io.restassured.response.Response;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.given;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static specs.BookStoreSpecs.authRequestSpec;
import static specs.BookStoreSpecs.loginRequestSpec;

public class ProfileOperationTests extends TestBase {

    String userName = "basil6";
    String password = "Basil1982!";
    String isbn = "9781449331818"; // Git Pocket Guide

    @Test
    @DisplayName("Добавление и удаление книги через API + UI (Allure шаги)")
    void addAndDeleteBookApiUiAllureSteps() {

        String[] tokenUserId = loginViaApi(userName, password);
        String token = tokenUserId[0];
        String userId = tokenUserId[1];
        String expires = tokenUserId[2];

        openUiAndSetCookies(userId, expires, token);

        addBookViaApi(token, userId, isbn);

        deleteBookViaApi(token, userId, isbn);
    }

    @Step("Логинимся через API и получаем токен, userId и expires")
    private String[] loginViaApi(String userName, String password) {
        Response loginResponse = given()
                .spec(loginRequestSpec())
                .filter(withCustomTemplates())
                .body(new LoginBodyModel(userName, password))
                .when()
                .post("/Account/v1/Login")
                .then()
                .statusCode(200)
                .extract().response();

        String token = loginResponse.path("token");
        String userId = loginResponse.path("userId");
        String expires = loginResponse.path("expires");

        Allure.step("Token: " + token);
        Allure.step("UserId: " + userId);
        Allure.step("Expires: " + expires);

        return new String[]{token, userId, expires};
    }

    @Step("Открываем UI (маленькая страница) и добавляем куки для авторизации")
    private void openUiAndSetCookies(String userId, String expires, String token) {
        open("https://demoqa.com/images/Toolsqa.jpg");
        getWebDriver().manage().addCookie(new Cookie("userID", userId));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));
        open("https://demoqa.com/profile");
    }

    @Step("Отправляем запрос на добавление книги ISBN {2} через API")
    private void addBookViaApi(String token, String userId, String isbn) {
        AddBooksRequest requestBody = new AddBooksRequest(userId, List.of(new AddBooksRequest.Isbn(isbn)));

        given()
                .spec(authRequestSpec(token))
                .filter(withCustomTemplates())
                .body(requestBody)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .statusCode(201);

        Allure.step("Книга добавлена успешно");
    }

    @Step("Отправляем запрос на удаление книги ISBN {2} через API")
    private void deleteBookViaApi(String token, String userId, String isbn) {
        DeleteBookRequest deleteRequest = new DeleteBookRequest(userId, isbn);

        given()
                .spec(authRequestSpec(token))
                .filter(withCustomTemplates())
                .body(deleteRequest)
                .when()
                .delete("/BookStore/v1/Book")
                .then()
                .statusCode(204);

        Allure.step("Книга удалена успешно");
    }
}
