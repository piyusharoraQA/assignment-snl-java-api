package com.qainfotech.tap.training.snl.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import org.testng.annotations.BeforeTest;
import static org.assertj.core.api.Assertions.*;
import org.json.JSONObject;
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
        //   String name1 = ((JSONObject) test.data.getJSONArray("players").get(1)).get("name").toString();
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
    public void PositionShouldBeZero(){
        int pos=(int) ((JSONObject) test.getData().getJSONArray("players").get(0)).get("position");
                
                assertThat(pos==0);
    }

    @Test(priority = 5)//(expectedExceptions = NoUserWithSuchUUIDException.class, priority = 4)
    public void registerPlayer_NoUserWithSuchUUID() throws NoUserWithSuchUUIDException,
            FileNotFoundException, UnsupportedEncodingException, UnsupportedEncodingException {
        UUID id = (UUID) ((JSONObject) test.data.getJSONArray("players").get(0)).get("uuid");
        System.out.println(id);
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
    public void registerPlayer_GameInProgress() throws InvalidTurnException, FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption, IOException {
        UUID id = (UUID) ((JSONObject) test.getData().getJSONArray("players").get(0)).get("uuid");

        test.rollDice(id);
        test.registerPlayer("somil");
    }

    @Test( priority = 7)
    public void rollDice_InvalidTurn() throws InvalidTurnException, FileNotFoundException, UnsupportedEncodingException {
        UUID id2 = (UUID) ((JSONObject) test.getData().getJSONArray("players").get(1)).get("uuid");
        test.rollDice(id2);
    }
    
}
