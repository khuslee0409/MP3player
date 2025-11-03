import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class PlayerRegistry {
    private static MediaPlayer player;
    private static Media media;

    private PlayerRegistry() {}

    public static void set(MediaPlayer p, Media m) {
        player = p;
        media = m;
    }

    public static void release() {
        try {
            if (player != null) {
                player.stop();
                player.dispose();   // release player resources
            }
        } catch (Exception ignored) {}

        
        player = null;
        media = null;

        // Force GC and give Windows time to release the file handle
        System.gc();
        try {
            Thread.sleep(300);  // delay for windows release
        } catch (InterruptedException ignored) {}
    }
}
