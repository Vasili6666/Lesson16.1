package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.filter.log.LogDetail;

public class BookStoreSpecs {

    // Уже есть RequestSpec
    public static RequestSpecification addBookSpec(String token) {
        return new RequestSpecBuilder()
                .setContentType("application/json")
                .addHeader("Authorization", "Bearer " + token)
                .log(LogDetail.ALL)
                .build();
    }

    // Новый ResponseSpec для добавления книги
    public static ResponseSpecification addBookResponseSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)   // проверка успешного добавления
                .log(LogDetail.ALL)      // логируем ответ полностью
                .build();
    }
}
