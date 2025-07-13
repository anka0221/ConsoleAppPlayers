package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.MyWatchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
import ru.inno.course.player.data.DataProviderJSON;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MyWatchers.class)
public class PlayerServiceNegativeTests {

    @BeforeEach
    public void clearBefore() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Path FILEPATH = Path.of("data.json");

        Collection<Player> currentList = Collections.EMPTY_LIST;

        mapper.writerWithDefaultPrettyPrinter().writeValue(FILEPATH.toFile(), currentList);
    }

    @Test
    @DisplayName("1. Удалить игрока которого нет")
    @Tag("Negative_TC")
    public void createPlayerInEmptyListTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";
        int expectedPlayerId = 10;

        for (int i = 0; i < 8; i++) {
            service.createPlayer(expectedPlayerNick + (i + 1));
        }

        assertThatThrownBy(() -> service.deletePlayer(expectedPlayerId)).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> service.deletePlayer(expectedPlayerId)).hasMessage("No such user: " + expectedPlayerId);
    }

    @Test
    @DisplayName("2. Создать дубликат (имя уже занято)")
    @Tag("Negative_TC")
    public void createDuplicateTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";

        service.createPlayer(expectedPlayerNick);

        assertThatThrownBy(() -> service.createPlayer(expectedPlayerNick)).hasMessage("Nickname is already in use: " + expectedPlayerNick);
        assertThatThrownBy(() -> service.createPlayer(expectedPlayerNick)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("3. Получить игрока по id, которого нет")
    @Tag("Negative_TC")
    public void getPlayerForNonExistingIdTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";
        int expectedPlayerId = 1000;

        service.createPlayer(expectedPlayerNick);

        assertThatThrownBy(() -> service.getPlayerById(expectedPlayerId)).hasMessage("No such user: " + expectedPlayerId);
        assertThatThrownBy(() -> service.getPlayerById(expectedPlayerId)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("4. Сохранить игрока с пустым ником")
    @Tag("Negative_TC")
    public void createPlayerWithEmptyNickTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "";

        assertThatThrownBy(() -> service.createPlayer(expectedPlayerNick)).hasMessage("Nickname is empty!");
        assertThatThrownBy(() -> service.createPlayer(expectedPlayerNick)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("5. Начислить отрицательное число очков")
    @Tag("Negative_TC")
    public void addNegativePointsTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";
        int expectedPoints = -200;

        assertThatThrownBy(() -> service.addPoints(service.createPlayer(expectedPlayerNick), expectedPoints)).hasMessage("You cannot add negative points!");
        assertThatThrownBy(() -> service.addPoints(service.createPlayer(expectedPlayerNick), expectedPoints)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("6. Начислить очки игроку которого нет")
    @Tag("Negative_TC")
    public void addPointsForNonExistingPlayerTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";
        int expectedPoints = 200;
        int expectedPLayerId = 1000;

        service.createPlayer(expectedPlayerNick);

        assertThatThrownBy(() -> service.addPoints(expectedPLayerId, expectedPoints)).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> service.addPoints(expectedPLayerId, expectedPoints)).hasMessage("No such user: " + expectedPLayerId);
    }

    // TC 7 пропущен для реализации, так как addPoints на вход в качестве аргумента принимает playerId
    // TC 8 пропущен для реализации, так как методы на вход принимают int playerId
    // TC 9 пропущен для реализации, так как имя файла зашито в DataProviderJSON
    // TC 10 пропущен для реализации, так как addPoints на вход в качестве аргумента принимает int points

    @Test
    @DisplayName("11. Проверить корректность загрузки JSON файла. Есть дубликаты")
    @Tag("Negative_TC")
    public void loadJSONWithDuplicatesTest() throws IOException {
        Collection<Player> players = new ArrayList<>();
        players.add(new Player(1, "Nick", 100, true));
        players.add(new Player(2, "Nick", 200, false));
        players.add(new Player(1, "Nick1", 300, false));

        new DataProviderJSON().save(players);

        assertThatThrownBy(() -> new DataProviderJSON().load()).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new DataProviderJSON().load()).hasMessage("JSON file contains duplicates!");
    }

    @Test
    @DisplayName("12. Проверить создание игрока с 16 символами")
    @Tag("Negative_TC")
    public void createPlayerWith16SymbolsTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedNick = "A".repeat(16);

        String actualNick = service.getPlayerById(service.createPlayer(expectedNick)).getNick();

        assertEquals(expectedNick, actualNick);
    }

    @AfterEach
    public void clearAfter() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Path FILEPATH = Path.of("data.json");

        Collection<Player> currentList = Collections.EMPTY_LIST;

        mapper.writerWithDefaultPrettyPrinter().writeValue(FILEPATH.toFile(), currentList);
    }
}
