package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;

public class BookStoreSpecs {

    public static RequestSpecification loginRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://demoqa.com")
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification authRequestSpec(String token) {
        return with()
                .baseUri("https://demoqa.com")
                .filter(withCustomTemplates())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .log().all();
    }

    public static ResponseSpecification universalResponseSpec() {
        return new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }
}
