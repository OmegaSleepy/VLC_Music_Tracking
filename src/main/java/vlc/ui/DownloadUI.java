package vlc.ui;

import sql.Log;
import vlc.tracker.DownloadMusicKt;
import vlc.tracker.ScriptsKt;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class DownloadUI {
    private JTextField writeDownTheFullTextField;
    private JButton DOWNLOADSENDTOTHEButton;
    private JLabel LastDownload;
    private JPanel Panel;

    public JPanel getPanel () {
        return Panel;
    }

    private final String WATCH_KEY_WORD = "watch?v=";
    private final String PLAYLIST_KEY_WORD = "playlist?list=OLAK5uy_";

    private final int SINGLE_CODE_LENGHT = 11;
    private final int PLAYLIST_CODE_LENGHT = 33;

    public String getSingleUrl(String fullUrl) {

        int codeStartlist = fullUrl.lastIndexOf(WATCH_KEY_WORD);

        return codeStartlist == -1 ? fullUrl : fullUrl
                .substring(codeStartlist+WATCH_KEY_WORD.length())
                .substring(0,SINGLE_CODE_LENGHT);
    }

    public String getAlbumUrl(String fullUrl){
        int codeStartlist = fullUrl.lastIndexOf(PLAYLIST_KEY_WORD);

        return codeStartlist == -1 ? fullUrl : fullUrl
                .substring(codeStartlist+PLAYLIST_KEY_WORD.length())
                .substring(0,PLAYLIST_CODE_LENGHT);
    }

    public DownloadUI () {
        DOWNLOADSENDTOTHEButton.addActionListener(e -> {

            String url = writeDownTheFullTextField.getText();

            if(url.equals("write down the full URL")) {
                Log.warn("The full URL is invalid!");
                return;
            }

            if(url.contains("watch?v=")) {
                downloadSingle(url);
            } else{
                downloadAlbum(url);
            }


        });
    }

    private void downloadAlbum (String url) {
        var formatedUrl = getAlbumUrl(url);

        Log.exec("Trying to download album with code %s".formatted(formatedUrl));

        DownloadMusicKt.runPython(
                new String[]{
                        ScriptsKt.packageAlbums(
                                Collections.singletonList(formatedUrl)
                        )
                },
                DownloadMusicKt.SCRIPT_PATH
        );
    }

    private void downloadSingle (String url) {
        var formatedUrl = getSingleUrl(url);

        Log.exec("Trying to download single with code %s".formatted(formatedUrl));

        DownloadMusicKt.runPython(
                new String[] {
                        ScriptsKt.packageSingles(
                                Collections.singletonList(getSingleUrl(url))
                        )
                },
                DownloadMusicKt.SCRIPT_PATH
        );
    }

    public static void main (String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Music Downloader");
            frame.setContentPane(new DownloadUI().getPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }
}
