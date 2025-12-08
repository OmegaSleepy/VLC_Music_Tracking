package vlc;

public class Main {
    public static void main (String[] args) throws Exception {
        for(String s: args){

            switch (s.toLowerCase()){
                case "tracker":
                    vlc.tracker.Main.main(null);
                    break;
                case "console":
                    vlc.console.Main.main(null);
            }
        }
    }
}
