package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class BookStoreSpecs {

    // ==================== Авторизация ====================
    public static RequestSpecification loginRequestSpec() {
        return new RequestSpecBuilder()
                .setContentType("application/json")
                .log(LogDetail.ALL)
                .build();
    }

    // ==================== Все действия с токеном ====================
    public static RequestSpecification authRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setContentType("application/json")
                .addHeader("Authorization", "Bearer " + token)
                .log(LogDetail.ALL)
                .build();
    }

    // ==================== Универсальный ResponseSpec ====================
    public static ResponseSpecification universalResponseSpec() {
        return new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }
}
