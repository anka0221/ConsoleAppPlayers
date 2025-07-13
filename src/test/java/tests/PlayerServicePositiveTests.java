package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.MyWatchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.course.player.data.DataProviderJSON;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MyWatchers.class)
public class PlayerServicePositiveTests {

    public PlayerServicePositiveTests() {
    }


    @BeforeEach
    public void clearBefore() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Path FILEPATH = Path.of("data.json");

        Collection<Player> currentList = Collections.EMPTY_LIST;

        mapper.writerWithDefaultPrettyPrinter().writeValue(FILEPATH.toFile(), currentList);
    }

    @Test
    @DisplayName("1.1 Добавить игрока в пустой список")
    @Tag("Positive_TC")
    public void createPlayerInEmptyListTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";
        int expectedPlayerId = 1;
        int actualPlayerId = service.createPlayer(expectedPlayerNick);

        String expectedPlayer = "Player{id=" + expectedPlayerId + ", nick='" + expectedPlayerNick + "', points=0, isOnline=true}";

        assertThat(Path.of("data.json")).exists();
        assertThat(service.getPlayerById(actualPlayerId).toString()).isEqualTo(expectedPlayer);
    }

    @Test
    @DisplayName("1.2 Добавить игрока в не пустой список")
    @Tag("Positive_TC")
    public void createPlayerInNotEmptyListTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick2";
        int expectedPlayerId = 2;

        service.createPlayer("Nick1");
        int actualPlayerId = service.createPlayer(expectedPlayerNick);

        String expectedPlayer = "Player{id=" + expectedPlayerId + ", nick='" + expectedPlayerNick + "', points=0, isOnline=true}";

        assertEquals(expectedPlayer, service.getPlayerById(actualPlayerId).toString());

    }

    @Test
    @DisplayName("2. Удалить не последнего игрока")
    @Tag("Positive_TC")
    public void deleteNotLastPlayerTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedDeletedPlayerNick = "Nick1";
        int expectedDeletedPlayerId = 1;
        service.createPlayer(expectedDeletedPlayerNick);
        service.createPlayer("Nick2");
        Player deletedPlayer = service.deletePlayer(expectedDeletedPlayerId);

        String expectedDeletedPlayer = "Player{id=" + expectedDeletedPlayerId + ", nick='" + expectedDeletedPlayerNick + "', points=0, isOnline=true}";

        assertThat(deletedPlayer.toString()).isEqualTo(expectedDeletedPlayer);
        assertThatThrownBy(() -> service.getPlayerById(expectedDeletedPlayerId)).hasMessage("No such user: " + expectedDeletedPlayerId);
        assertThatThrownBy(() -> service.getPlayerById(expectedDeletedPlayerId)).isInstanceOf(NoSuchElementException.class);
        assertThat(service.getPlayers()).doesNotContain(new Player(expectedDeletedPlayerId, expectedDeletedPlayerNick, 0, true));
    }

    @Test
    @DisplayName("2. Удалить последнего игрока")
    @Tag("Positive_TC")
    public void deleteLastPlayerTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedDeletedPlayerNick = "Nick1";
        int expectedDeletedPlayerId = 1;
        service.createPlayer(expectedDeletedPlayerNick);
        Player deletedPlayer = service.deletePlayer(expectedDeletedPlayerId);

        String expectedDeletedPlayer = "Player{id=" + expectedDeletedPlayerId + ", nick='" + expectedDeletedPlayerNick + "', points=0, isOnline=true}";

        assertThat(deletedPlayer.toString()).isEqualTo(expectedDeletedPlayer);
        assertThatThrownBy(() -> service.getPlayerById(expectedDeletedPlayerId)).hasMessage("No such user: " + expectedDeletedPlayerId);
        assertThatThrownBy(() -> service.getPlayerById(expectedDeletedPlayerId)).isInstanceOf(NoSuchElementException.class);
        assertThat(service.getPlayers()).isEmpty();
    }

    @Test
    @DisplayName("3. Создать игрока при условии что JSON файл не существует")
    @Tag("Positive_TC")
    public void createPlayerWithoutJSONTest() throws IOException {
        Files.deleteIfExists(Path.of("data.json"));

        // Создаем буфер для захвата вывода
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;

        // Перенаправляем System.err в наш буфер
        System.setErr(new PrintStream(baos));

        // Вызываем ошибочный вывод
        new PlayerServiceImpl();

        // Возвращаем System.err в исходное состояние
        System.setErr(originalErr);

        // Получаем строку из буфера
        String errorOutput = baos.toString();

        // Теперь можно использовать значение
        assertThat(errorOutput).isEqualToIgnoringNewLines("File loading error 1. java.io.FileNotFoundException: data.json (The system cannot find the file specified)");
    }

    // TC 4 пропущен для реализации. Покрывается TC 1

    @Test
    @DisplayName("5. Начислить баллы существующему игроку")
    @Tag("Positive_TC")
    public void addPointsForExistingPlayerTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";
        int actualPlayerId = service.createPlayer(expectedPlayerNick);
        int expectedPoints = 100;
        int actualPoints = service.addPoints(actualPlayerId, expectedPoints);

        assertEquals(expectedPoints, actualPoints);
        assertEquals(expectedPoints, service.getPlayerById(actualPlayerId).getPoints());
    }

    @Test
    @DisplayName("6. Начислить баллы поверх существующих существующему игроку")
    @Tag("Positive_TC")
    public void addAdditionalPointsForExistingPlayerTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";
        int points1 = 100;
        int points2 = 250;
        int actualPlayerId = service.createPlayer(expectedPlayerNick);
        service.addPoints(actualPlayerId, points1);
        int expectedPoints = points1 + points2;
        int actualPoints = service.addPoints(actualPlayerId, points2);

        assertEquals(expectedPoints, actualPoints);
        assertEquals(expectedPoints, service.getPlayerById(actualPlayerId).getPoints());
    }

    @Test
    @DisplayName("7. Получить игрока по ID")
    @Tag("Positive_TC")
    public void getPlayerByIDTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick = "Nick";
        int expectedPlayerId = 1;

        int actualPlayerId = service.createPlayer(expectedPlayerNick);
        Player actualPlayer = service.getPlayerById(actualPlayerId);

        //Формируем эталонную строку нашего игрока
        String expectedPlayer = "Player{id=" + expectedPlayerId + ", nick='" + expectedPlayerNick + "', points=0, isOnline=true}";

        assertEquals(expectedPlayer, actualPlayer.toString());
    }

    @Test
    @DisplayName("8. Проверить корректность загрузки json файла")
    @Tag("Positive_TC")
    public void savingToFileTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick1 = "Nick1";
        String expectedPlayerNick2 = "Nick2";

        service.createPlayer(expectedPlayerNick1);
        service.createPlayer(expectedPlayerNick2);

        File file = new File("data.json");

        assertThat(file).exists();
        assertThat(file).isNotEmpty();
    }

    @Test
    @DisplayName("9. Проверить корректность загрузки json файла")
    @Tag("Positive_TC")
    public void loadJSONFromFileTest() throws IOException {
        PlayerService service = new PlayerServiceImpl();

        String expectedPlayerNick1 = "Nick1";
        String expectedPlayerNick2 = "Nick2";

        int actualPlayerId1 = service.createPlayer(expectedPlayerNick1);
        int actualPlayerId2 = service.createPlayer(expectedPlayerNick2);


        //Формируем эталонную коллекцию
        Collection<Player> expectedPlayerCollection = new ArrayList<>();
        expectedPlayerCollection.add(service.getPlayerById(actualPlayerId1));
        expectedPlayerCollection.add(service.getPlayerById(actualPlayerId2));

        //Получаем то что было сохранено в файл
        DataProviderJSON dataProviderJSON = new DataProviderJSON();
        Collection<Player> actualPlayerCollection = dataProviderJSON.load();

        assertEquals(expectedPlayerCollection, actualPlayerCollection);
    }

    @Test
    @DisplayName("10. Проверить что id всегда уникальный")
    @Tag("Positive_TC")
    //@Disabled("Временно")
    public void idIsUniqueTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedNick = "Nick";
        int expectedPlayerId = 6;

        //Создаем 5 игроков
        for (int i = 0; i < expectedPlayerId - 1; i++) {
            service.createPlayer(expectedNick + (i + 1));
        }

        //Удаляем третьего
        service.deletePlayer(3);

        //Добавляем шестого
        int actualPlayerId = service.createPlayer(expectedNick + expectedPlayerId);

        assertEquals(expectedPlayerId, actualPlayerId);
    }

    // TC 11 пропущен для реализации, так как на этапе инициализации уже есть exception
    // в случае если JSON файл не существует

    @Test
    @DisplayName("12. Проверить создание игрока с 15 символами.")
    @Tag("Positive_TC")
    public void createPlayerWith15SymbolsTest() {
        PlayerService service = new PlayerServiceImpl();

        String expectedNick = "A".repeat(15);

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
