package com.qainfotech.tap.training.snl.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import org.testng.annotations.BeforeTest;
import static org.assertj.core.api.Assertions.*;
import org.json.JSONObject;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author piyusharora
 */
public class BoardTest {

    Board test;

    @BeforeTest
    public void SetupBoard() throws UnsupportedEncodingException, IOException, PlayerExistsException, GameInProgressException, FileNotFoundException, MaxPlayersReachedExeption {
        test = new Board();
        test.registerPlayer("Piyush");
        test.registerPlayer("Aman");

    }

    @Test(expectedExceptions = PlayerExistsException.class, priority = 1)
    public void registerPlayer_Already_Existing_User() throws PlayerExistsException, GameInProgressException, UnsupportedEncodingException, MaxPlayersReachedExeption, IOException {
        test.registerPlayer("Aman");

    }

    @Test(priority = 2)
    public void registerPlayer_CheckNumberOfPlayers() throws UnsupportedEncodingException, IOException, PlayerExistsException, GameInProgressException, FileNotFoundException, MaxPlayersReachedExeption {
        test.registerPlayer("ram");
        test.registerPlayer("shyam");
        assertThat(test.data.getJSONArray("players").length()).isEqualTo(4);

    }

    @Test(expectedExceptions = MaxPlayersReachedExeption.class, priority = 3)
    public void registerPlayer_MaxPlayerReached() throws IOException, MaxPlayersReachedExeption, PlayerExistsException, GameInProgressException {
        test.registerPlayer("Raman");

    }

    @Test(priority = 4)
    public void PositionShouldBeZero() {
        int pos = (int) ((JSONObject) test.getData().getJSONArray("players").get(0)).get("position");

        assertThat(pos == 0);
    }

    @Test(priority = 5)
    public void deletePlayer_NoUserWithSuchUUID() throws NoUserWithSuchUUIDException,
            FileNotFoundException, UnsupportedEncodingException, UnsupportedEncodingException {
        UUID id = (UUID) ((JSONObject) test.data.getJSONArray("players").get(0)).get("uuid");
        int COMP = 0;
        test.deletePlayer(id);
        for (int index = 0; index < test.getData().getJSONArray("players").length(); index++) {
            UUID uuid = (UUID) ((JSONObject) test.data.getJSONArray("players").getJSONObject(index)).get("uuid");
            if (uuid.equals(id)) {
                COMP = 1;
            }
        }
        assertThat(COMP == 0);

    }

    @Test(expectedExceptions = GameInProgressException.class, priority = 6)
    public void registerPlayer_GameInProgress() throws InvalidTurnException, FileNotFoundException,
            UnsupportedEncodingException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption,
            IOException {
        UUID id = (UUID) ((JSONObject) test.getData().getJSONArray("players").get(0)).get("uuid");
        test.rollDice(id);
        test.registerPlayer("somil");
    }

    @Test(priority = 7)
    public void rollDice_InvalidTurn() throws InvalidTurnException, FileNotFoundException, UnsupportedEncodingException {
        UUID id = (UUID) ((JSONObject) test.getData().getJSONArray("players").get(1)).get("uuid");
        test.rollDice(id);
    }

    @Test(priority = 8)
    public void rollDice_CorrectPositionAfterRoll() throws InvalidTurnException, FileNotFoundException, UnsupportedEncodingException {

        UUID uuid = (UUID) test.getData().getJSONArray("players").getJSONObject(2).get("uuid");
        Integer currentPosition = (Integer) test.getData().getJSONArray("players").getJSONObject(2).get("position");
        Integer dice = (Integer) (test.rollDice(uuid)).get("dice");
        Integer newPosition = (Integer) test.getData().getJSONArray("players").getJSONObject(2).get("position");
        int type = test.getData().getJSONArray("steps").getJSONObject(newPosition).getInt("type");
        currentPosition = currentPosition + dice;
        assertThat(type == 0);
        assertEquals(currentPosition, newPosition);

    }

    @Test(priority = 9)
    public void PositionDecreasesOnSnake() throws InvalidTurnException, FileNotFoundException, UnsupportedEncodingException {
        UUID uuid = (UUID) test.getData().getJSONArray("players").getJSONObject(0).get("uuid");
        (test.getData().getJSONArray("players").getJSONObject(0)).put("position", 68);
        (test.rollDice(uuid)).put("dice", 2);
        Integer newPos = (Integer) test.getData().getJSONArray("players").getJSONObject(0).get("position");
        System.out.println(newPos);
        assertThat(newPos < 68);

    }

    @Test(priority = 10)
    public void rollDice_Near100() throws InvalidTurnException, IOException, PlayerExistsException, GameInProgressException,
            MaxPlayersReachedExeption {

        Board testObject = new Board();
        testObject.registerPlayer("ABC");
        testObject.registerPlayer("XYZ");
        testObject.registerPlayer("PQR");
        testObject.registerPlayer("MNO");

        Object ob;
        for (int index = 0; index < test.data.getJSONArray("players").length(); index++) {

            UUID uuid = UUID.fromString(testObject.data.getJSONArray("players").getJSONObject(index).get("uuid").toString());
            Object playerObject = testObject.getData().getJSONArray("players").getJSONObject(index);

            JSONObject player = (JSONObject) playerObject;

            player.put("position", 97);
            Integer playerPositionBeforeRollDice = player.getInt("position");

            System.out.println("Player Position Before Roll Of Dice : " + playerPositionBeforeRollDice);
            JSONObject rollDiceObject = testObject.rollDice(uuid);
            System.out.println("rollDiceObject" + rollDiceObject);

            int dice = rollDiceObject.getInt("dice");

            int playerPositionAfterRollDice = (int) testObject.getData().getJSONArray("players").getJSONObject(index)
                    .get("position");

            System.out.println("Player Position After Roll Of Dice : " + playerPositionAfterRollDice);
            int playerType = testObject.getData().getJSONArray("steps").getJSONObject(playerPositionAfterRollDice)
                    .getInt("type");
            System.out.println("Player Type : " + playerType);

            if (playerType == 1 && playerPositionAfterRollDice <= 100) {
                String msg = "Player was bit by a snake, moved back to  " + playerPositionAfterRollDice;
                System.out.println(msg);
                assertThat(rollDiceObject.getString("message")).isEqualTo(msg);
            } else if (playerType == 2 && playerPositionAfterRollDice <= 100) {
                String msg = "Player climbed a ladder, moved to " + playerPositionAfterRollDice;
                System.out.println(msg);
                assertThat(rollDiceObject.getString("message")).isEqualTo(msg);
            } else if (playerType == 0 && playerPositionAfterRollDice <= 100
                    && playerPositionAfterRollDice != playerPositionBeforeRollDice) {
                String msg = "Player moved to " + playerPositionAfterRollDice;
                System.out.println(msg);
                assertThat(rollDiceObject.getString("message")).isEqualTo(msg);
            } else if (playerPositionAfterRollDice > 100
                    || playerPositionAfterRollDice == playerPositionBeforeRollDice) {
                String msg = "Incorrect roll of dice. Player did not move";
                System.out.println(msg);
                assertThat(rollDiceObject.getString("message")).isEqualTo(msg);
            }
        }
    }

//    @Test(priority = 11)
//     public toString() throws Exception{
//        String value = "UUID:" + test.uuid.toString() + "\n" + test.data.toString();
// 		assertThat(value.Equals(test.toString()));
//    }
    @Test(priority = 12)
    public void rollDice_ValueOfDiceIsLessThan_6_AndGreaterThan_0() throws InvalidTurnException, FileNotFoundException, UnsupportedEncodingException {
        UUID uuid = (UUID) test.getData().getJSONArray("players").getJSONObject(1).get("uuid");
        Integer dice = (Integer) (test.rollDice(uuid)).get("dice");
        System.out.println("Value of Dice is : " + dice);
        assertThat((dice > 0) && (dice < 6));

    }

}
