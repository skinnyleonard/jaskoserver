package tools;

import com.badlogic.gdx.Gdx;

public class Timer extends Thread {
    public static int seg = 0;
    public static int min = 0;
    public static int mil = 0;
    private static boolean running = true;

    @Override
    public void run() {
        try {
            // Wait a bit for LibGDX to fully initialize
            Thread.sleep(100);

            while (running) {
                mil++;
                if (mil >= 1000) {
                    mil = 0;
                    seg++;
                }
                if (seg >= 60) {
                    seg = 0;
                    min++;
                }

                // Use LibGDX's thread-safe postRunnable for UI updates
                final String timeText = String.format("Tiempo: %02d:%02d:%03d", min, seg, mil);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (HUD.timeLabel != null) {
                            HUD.timeLabel.setText(timeText);
                        }
                    }
                });

                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void stopTimer() {
        running = false;
    }

    public void resetTimer() {
        min = 0;
        seg = 0;
        mil = 0;
    }
}
