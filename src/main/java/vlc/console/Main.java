package vlc.console;

import sql.Log;
import sql.Query;

import java.util.Scanner;

public class Main {

    public static void printAll(){
        Log.logSelect.accept(Query.getResult("Select * from "));
    }



    public static void main (String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true){

            String command = scanner.nextLine();
            command = command.toLowerCase();

            switch (command){

            }

        }


    }

}
