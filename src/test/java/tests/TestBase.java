package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    protected static String BASE_URI = "https://demoqa.com";

    @BeforeAll
    static void setup() {
        // Устанавливаем базовый URI для всех запросов
        RestAssured.baseURI = BASE_URI;
    }
}
