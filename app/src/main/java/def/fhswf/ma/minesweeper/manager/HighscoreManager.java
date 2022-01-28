package def.fhswf.ma.minesweeper.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import def.fhswf.ma.minesweeper.highscore.Difficulty;
import def.fhswf.ma.minesweeper.highscore.Highscore;

/**
 * Autor: Dominik Müller
 */
public class HighscoreManager {

    private static HighscoreManager instance;
    public static HighscoreManager getInstance() throws IOException {
        if(instance != null && instance.connection == null)
            instance = new HighscoreManager();
        if(instance == null)
            instance = new HighscoreManager();
        return instance;
    }

    private static final String SERVER_IP = "167.86.73.232";
    private static final Integer SERVER_PORT = 9956;

    private Socket connection = null;

    private Map<Difficulty, Highscore[]> highscoreByDifficulty = null;

    private HighscoreManager() throws IOException {
        connect();
    }

    private void connect() throws IOException {
        if(connection == null || connection.isClosed())
            connection = new Socket(SERVER_IP, SERVER_PORT);
    }

    public Highscore[] getHighscoreByDifficulty(Difficulty difficulty){
        return highscoreByDifficulty.get(difficulty);
    }

    public void loadHighscore() throws IOException {
        connect();

        OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
        PrintWriter pw = new PrintWriter(os);
        pw.println("highscore_info");
        pw.flush();

        receiveHighscore();
    }

    public boolean checkHighscore(Highscore highscore, Difficulty difficulty) throws IOException {
        if(highscoreByDifficulty == null)
            return false;

        Highscore[] highscoreA = highscoreByDifficulty.get(difficulty);
        if(highscoreA == null || highscoreA.length == 0)
            return true;
        if(highscoreA[9] == null || highscoreA[9].getPoints() < highscore.getPoints()
                || (highscoreA[9].getPoints() == highscore.getPoints() && highscoreA[9].getTime() < highscore.getTime()))
            return true;
        return false;
    }

    private void receiveHighscore() throws IOException {
        highscoreByDifficulty = new HashMap<Difficulty, Highscore[]>();

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<String> lines = new ArrayList<String>();
        lines.add(br.readLine());
        while(br.ready()){
            lines.add(br.readLine());
        }

        Difficulty currentDifficulty = null;
        Highscore[] highscores = null;
        int highscoreIndex = 0;

        System.out.println(lines);

        for(int i = 0; i < lines.size(); i++){
            if(lines.get(i).startsWith("difficulty: ")) {
                if (currentDifficulty != null) {
                    highscoreByDifficulty.put(currentDifficulty, highscores);
                }

                currentDifficulty = Difficulty.valueOf(lines.get(i).replaceFirst("difficulty: ", ""));
                highscores = new Highscore[10];
                highscoreIndex = 0;
            } else {
                if(i + 2 >= lines.size())
                    break;
                highscores[highscoreIndex++] = new Highscore(lines.get(i + 2), Integer.parseInt(lines.get(i)), Integer.parseInt(lines.get(i + 1)));
                i = i + 2;
            }

        }
        if (currentDifficulty != null) {
            highscoreByDifficulty.put(currentDifficulty, highscores);
        }

        connection.close();
    }

    public void addHighscore(Highscore highscore, Difficulty difficulty) throws IOException {
        connect();



        OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
        PrintWriter pw = new PrintWriter(os);
        pw.println("highscore_add");
        pw.println(difficulty.toString());
        pw.println(highscore.getPoints());
        pw.println(highscore.getTime());
        pw.println(highscore.getName());
        pw.flush();

        connection.close();
    }

}
