package vlc.ui;

import sql.Log;
import vlc.common.DownloadMusicKt;
import vlc.common.ScriptsKt;

import javax.swing.*;
import java.util.Collections;

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

    public DownloadUI() {
        DOWNLOADSENDTOTHEButton.addActionListener(e -> {
            LastDownload.setText("Downloading...");
            DOWNLOADSENDTOTHEButton.setEnabled(false);

            String url = writeDownTheFullTextField.getText();

            if (url.equals("write down the full URL")) {
                Log.warn("The full URL is invalid!");
                LastDownload.setText("Downloading failure! Invalid URL!");
                DOWNLOADSENDTOTHEButton.setEnabled(true);
                return;
            }

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    if (url.contains("watch?v=")) {
                        downloadSingle(url);
                    } else {
                        downloadAlbum(url);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // rethrow exceptions from doInBackground
                        LastDownload.setText("Downloaded %s".formatted(url));
                    } catch (Exception ex) {
                        Log.warn("Download failed: %s".formatted(ex.getMessage()));
                        LastDownload.setText("Downloading failure! %s".formatted(ex.getMessage()));
                    } finally {
                        DOWNLOADSENDTOTHEButton.setEnabled(true);
                    }
                }
            };

            worker.execute();
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
        Log.MAX_LOGS = 8;
        Log.cleanUp();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Music Downloader");
            frame.setContentPane(new DownloadUI().getPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 303);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }
}
