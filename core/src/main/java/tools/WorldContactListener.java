package tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import screens.GameScreen;
import screens.PlayScreen;

import java.io.IOException;

public class WorldContactListener implements ContactListener{

    public static int maxLap = 2;
    public static int[][] playerCount = new int[8][2];
    private GameScreen playScreen;

    public WorldContactListener(GameScreen playScreen) {
        this.playScreen = playScreen;
        start();
        System.out.println(playerCount[0][0]);
        System.out.println(Loader.maxCheck);
    }

    public void start() {
        long time = System.currentTimeMillis();
        if (time > 100) {
            for (int i = 0; i < playerCount.length; i++) {
                playerCount[i][0] = 1;
                playerCount[i][1] = 1;
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        String a = String.valueOf(fixA.getUserData());
        String b = String.valueOf(fixB.getUserData());

        String car = null;
        String check = null;

        if (a.contains("car") && b.contains("check")) {
            car = a;
            check = b;
        } else if (b.contains("car") && a.contains("check")) {
            car = b;
            check = a;
        } else {
            return;
        }

        int carIndex = Integer.parseInt(car.replaceAll("[^0-9]", "")) - 1;
        int checkIndex = Integer.parseInt(check.replaceAll("[^0-9]", ""));

        if (carIndex < 0 || carIndex >= playerCount.length) return;

        if (playerCount[carIndex][0] + 1 == checkIndex) {
            playerCount[carIndex][0]++;
            System.out.println("Auto: " + carIndex + " Check: " + playerCount[carIndex][0] + " Vuelta: " + playerCount[carIndex][1]);
        }

        else if (playerCount[carIndex][0] == Loader.maxCheck && checkIndex == 1) {
            playerCount[carIndex][0] = 1;

            if (playerCount[carIndex][1] == maxLap) {
                Timer.stopTimer();
                System.out.println("Auto " + carIndex + " llegó a la meta!");
                playerCount[carIndex][1]++;
                try {
                    playScreen.parentRoom.pingEveryone("ended;"+carIndex);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                playerCount[carIndex][1]++;
                System.out.println("Auto: " + carIndex + " reinicia vuelta -> Vuelta: " + playerCount[carIndex][1]);
            }
        }
    }

    public String posCheck(int totalPlayer) {
        int[] pos = new int[totalPlayer];
        boolean[] alrSelected = new boolean[totalPlayer];

        for (int j = 0; j < totalPlayer; j++) {
            int selected = -1;
            int maxLaps = -1;
            int maxCheck = -1;

            for (int i = 0; i < totalPlayer; i++) {
                if (alrSelected[i]) continue;

                int laps = playerCount[i][1];
                int checks = playerCount[i][0];

                if (laps > maxLaps || (laps == maxLaps && checks > maxCheck)) {
                    maxLaps = laps;
                    maxCheck = checks;
                    selected = i;
                }
            }

            pos[j] = selected;
            alrSelected[selected] = true;
        }
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < totalPlayer; j++) {
            sb.append("Pos: "+(j + 1))
                .append(" | Auto: ").append(pos[j])
                .append(" | "+ playScreen.parentRoom.users.get(pos[j]).getUsername())
                .append(" | laps: ").append(playerCount[pos[j]][1])
                .append("/"+maxLap)
                .append(" | Checks: ").append(playerCount[pos[j]][0])
                .append("$")
                .append("\n");
        }

//        HUD.leaderboard.setText(sb.toString());
        return sb.toString();
    }


    public void removeCar(int index) {
        playerCount[index][0]=1;
        playerCount[index][1]=1;
    }

    @Override
    public void endContact(Contact contact) { }

    @Override
    public void preSolve(Contact contact, Manifold oldManiFold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}
