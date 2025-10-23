package tests;

import models.LoginBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import io.restassured.response.Response;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static specs.BookStoreSpecs.*;

import io.qameta.allure.Step;

public class ProfileTests extends TestBase {

    String userName = "basil6";
    String password = "Basil1982!";
    String isbn = "9781449331818"; // Git Pocket Guide

    @DisplayName("Добавление и удаление книги через API + UI")
    @Test
    void addAndDeleteBookApiUi() {

        Response authResponse = loginViaApi(userName, password);
        String token = authResponse.path("token");
        String userId = authResponse.path("userId");
        String expires = authResponse.path("expires");

        openUiAndSetCookies(userId, expires, token);
        addBookViaApi(token, userId, isbn);
        deleteBookViaApi(token, userId, isbn);
    }

    @Step("Логинимся через API под пользователем {userName}")
    private Response loginViaApi(String userName, String password) {
        return given(loginRequestSpec())
                .body(new LoginBodyModel(userName, password))
                .post("/Account/v1/Login")
                .then()
                .spec(universalResponseSpec())
                .statusCode(200)
                .extract().response();
    }

    @Step("Открываем UI и устанавливаем куки для авторизации")
    private void openUiAndSetCookies(String userId, String expires, String token) {
        open("https://demoqa.com/images/Toolsqa.jpg");
        getWebDriver().manage().addCookie(new Cookie("userID", userId));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));
        open("https://demoqa.com/profile");
    }

    @Step("Добавляем книгу с ISBN {isbn} через API")
    private void addBookViaApi(String token, String userId, String isbn) {
        String addBody = "{ \"userId\": \"" + userId + "\", \"collectionOfIsbns\": [{\"isbn\": \"" + isbn + "\"}] }";
        given(authRequestSpec(token))
                .body(addBody)
                .post("/BookStore/v1/Books")
                .then()
                .spec(universalResponseSpec())
                .statusCode(201);
    }

    @Step("Удаляем книгу с ISBN {isbn} через API")
    private void deleteBookViaApi(String token, String userId, String isbn) {
        String deleteBody = "{ \"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\" }";
        given(authRequestSpec(token))
                .body(deleteBody)
                .delete("/BookStore/v1/Book")
                .then()
                .spec(universalResponseSpec())
                .statusCode(204);
    }
}
