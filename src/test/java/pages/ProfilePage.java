package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class ProfilePage {
    SelenideElement
            userName = $("#userName-value"),
            bookTable = $(".rt-tbody"),
            deleteBook = $("#delete-record-undefined"),
            ok = $("#closeSmallModal-ok");

    @Step("Открыть страницу с профилем и добавить куки")
    public ProfilePage openProfilePage(String userId, String expires, String token) {
        open("https://demoqa.com/images/Toolsqa.jpg"); // можно пустую страницу, чтобы установить куки
        getWebDriver().manage().addCookie(new Cookie("userID", userId));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));
        open("https://demoqa.com/profile");
        return this;
    }

    @Step("Проверить, что библиотека содержит книгу")
    public ProfilePage checkingProfileBeforeDelete(String user, String title) {
        userName.shouldHave(text(user));
        bookTable.shouldHave(text(title));
        return this;
    }

    @Step("Удалить книги из профиля")
    public ProfilePage deleteBooksInProfile() {
        deleteBook.click();
        ok.click();
        return this;
    }

    @Step("Проверить, что библиотека пуста после удаления")
    public ProfilePage checkingProfileAfterDelete(String user) {
        userName.shouldHave(text(user));
        bookTable.shouldBe(empty);
        return this;
    }
}
