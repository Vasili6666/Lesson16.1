package tests;

import io.qameta.allure.Step;
import models.AddBooksRequest;
import models.DeleteBookRequest;
import models.LoginBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import io.restassured.response.Response;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static specs.BookStoreSpecs.*;
import static io.qameta.allure.Allure.step;
import static utils.TestData.*;

public class ProfileTests extends TestBase {

    @DisplayName("Добавление и удаление книги через API + UI")
    @Test
    void addAndDeleteBookApiUi() {

        Response authResponse = loginViaApi(USER_NAME, PASSWORD);
        String token = authResponse.path("token");
        String userId = authResponse.path("userId");
        String expires = authResponse.path("expires");

        openUiAndSetCookies(userId, expires, token);
        addBookViaApi(token, userId, ISBN);
        deleteBookViaApi(token, userId, ISBN);
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
        open("/images/Toolsqa.jpg");
        getWebDriver().manage().addCookie(new Cookie("userID", userId));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));
        open("/profile");
    }

    @Step("Добавляем книгу с ISBN {isbn} через API")
    private void addBookViaApi(String token, String userId, String isbn) {
        AddBooksRequest requestBody = new AddBooksRequest(
                userId,
                List.of(new AddBooksRequest.Isbn(isbn))
        );

        given(authRequestSpec(token))
                .body(requestBody)
                .post("/BookStore/v1/Books")
                .then()
                .spec(universalResponseSpec())
                .statusCode(201);
    }

    @Step("Удаляем книгу с ISBN {isbn} через API")
    private void deleteBookViaApi(String token, String userId, String isbn) {
        DeleteBookRequest requestBody = new DeleteBookRequest(userId, isbn);

        given(authRequestSpec(token))
                .body(requestBody)
                .delete("/BookStore/v1/Book")
                .then()
                .spec(universalResponseSpec())
                .statusCode(204);
    }
}
