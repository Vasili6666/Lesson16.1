package tests;

import annotations.WithLogin;
import extensions.LoginExtension;
import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import models.AddBooksRequest;
import models.DeleteBookRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Cookie;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static io.qameta.allure.Allure.step;
import static specs.BookStoreSpecs.*;
import static utils.TestData.*;

@ExtendWith(LoginExtension.class)
public class ProfileTests extends TestBase {

    @Test
    @WithLogin
    @DisplayName("Добавление и удаление книги через API + UI (авторизация с @WithLogin)")
    @Description("Авторизация выполняется автоматически через аннотацию @WithLogin")
    void addAndDeleteBookApiUi() {
        String token = System.getProperty("auth.token");

        step("Получаем данные пользователя через токен", () -> {
            Response userResponse = given(authRequestSpec(token))
                    .get("/Account/v1/User/" + USER_NAME)
                    .then()
                    .spec(universalResponseSpec())
                    .statusCode(200)
                    .extract().response();

            String userId = userResponse.path("userId");
            String expires = userResponse.path("expires");

            openUiAndSetCookies(userId, expires, token);
            addBookViaApi(token, userId, ISBN);
            deleteBookViaApi(token, userId, ISBN);
        });
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
