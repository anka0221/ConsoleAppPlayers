package helpers;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MyWatchers implements TestWatcher, BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        System.out.println("Старт тестирования:");
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("Test case: \"" + context.getDisplayName() + "\" is PASSED");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println("Test case: \"" + context.getDisplayName() + "\" is FAILED");
        System.out.println("\tПричина падения: " + cause.getMessage());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        System.out.println("Test case \"" + context.getDisplayName() + "\" is DISABLED."
                + "\n\tПричина: " + reason.get());
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("Тестирование завершено.");
    }

}
