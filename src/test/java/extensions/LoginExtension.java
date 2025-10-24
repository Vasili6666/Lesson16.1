package extensions;

import annotations.WithLogin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.LoginBodyModel;
import org.junit.jupiter.api.extension.*;
import utils.TestData;

public class LoginExtension implements BeforeTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        context.getTestMethod().ifPresent(method -> {
            WithLogin annotation = method.getAnnotation(WithLogin.class);
            if (annotation != null) {
                String username = TestData.USER_NAME;
                String password = TestData.PASSWORD;



                // Формируем тело запроса логина
                LoginBodyModel loginBody = new LoginBodyModel();
                loginBody.setUserName(username);
                loginBody.setPassword(password);

                // Отправляем запрос логина
                String token = RestAssured
                        .given()
                        .contentType(ContentType.JSON)
                        .body(loginBody)
                        .post("https://demoqa.com/Account/v1/Login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("token");

                // Настраиваем спецификацию RestAssured с токеном
                RequestSpecification spec = RestAssured
                        .given()
                        .contentType(ContentType.JSON)
                        .auth()
                        .oauth2(token);

                // Сохраняем токен в System property (или можно через ThreadLocal)
                System.setProperty("auth.token", token);
                RestAssured.requestSpecification = spec;
            }
        });
    }
}
