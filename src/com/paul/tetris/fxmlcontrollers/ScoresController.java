package com.paul.tetris.fxmlcontrollers;

import com.paul.tetris.TetrisMain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ScoresController {
    private File scoresFile;
    private final char DELIMITER_CHAR = '\t';
    private final TetrisMain TETRIS_MAIN;
    private final RankedList<ScoreEntry> scoreEntryRankedList = new RankedList<>(
            FXCollections.observableArrayList(),
            (entry1, entry2) -> {
                int score1 = entry1.score.get(), score2 = entry2.score.get();
                String name1 = entry1.name.get(), name2 = entry2.name.get();

                if (score1 == score2){
                    return (name1.compareTo(name2));
                } else if (score1 < score2){
                    return 1;
                } else {
                    return -1;
                }
            }
    );
    @FXML
    private TableView<ScoreEntry> scoresTableView;
    @FXML
    private TableColumn<ScoreEntry, Integer> rankColumn, scoreColumn;
    @FXML
    private TableColumn<ScoreEntry, String> nameColumn;

    public ScoresController(TetrisMain tetrisMain){
        TETRIS_MAIN = tetrisMain;

        // Try to locate a scores file
        try {
            scoresFile = new File(Objects.requireNonNull(TetrisMain.class.getResource("/tetris-scores.txt")).toString());
        } catch (NullPointerException e){
            System.err.print(e.getMessage() + ": ");
            scoresFile = new File("tetris-scores.txt");
            if (scoresFile.exists()){
                System.err.print("A scores file named 'tetris-scores.txt' was found in the current directory");
            } else {
                System.err.print("No scores file was located");
            }
            System.out.println();
        }
    }

    @FXML
    public void initialize(){
        loadScores();
        scoresTableView.setItems(scoreEntryRankedList);
        scoresTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
    }

    @FXML
    private void goToMainMenu(){
        TETRIS_MAIN.changeScene(TetrisMain.MENU_SCENE);
    }

    public void addScoreEntry(String name, int score){
        ScoreEntry entry = new ScoreEntry(name, score);
        scoreEntryRankedList.add(entry);
        scoresTableView.refresh();
        scoresTableView.getSelectionModel().select(entry); // Not working for some reason
        saveScores();
    }

    @FXML
    public void clearScores(){
        // Only clear scores if user confirms in alert dialog box.
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure that you want to clear all scores?");
        confirmAlert.setResizable(true);
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            scoreEntryRankedList.clear();
            scoresTableView.refresh();
            saveScores();
        }
    }

    private void loadScores(){
        if (scoresFile.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(scoresFile))) {
                ArrayList<ScoreEntry> entries; // Place to store score entries that are read from file
                String firstLine;   // First line of scores file should tell how many entries there are
                String line;        // Store line after calling reader.readline()
                String[] splitLine; // Split each line using DELIMITER_CHAR
                int size;           // Number of score entries to size ArrayList for
                int lineNumber = 1; // Current line number to use in error messages

                // Stop because the file is empty
                if ((firstLine = reader.readLine()) == null){
                    return;
                }

                // Try to determine number of entries to set entries array list initial capacity to size
                try {
                    size = Integer.parseInt(firstLine);
                } catch (NumberFormatException e){
                    size = 25; // default number of entries to allocate if file doesn't tell
                }
                entries = new ArrayList<>(size); // Initialize entries array to initial size

                // Read remaining lines, which should have score entries in the format: rank \t name \t score \n
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    splitLine = line.split(Character.toString(DELIMITER_CHAR)); // Divide line string by DELIMITER_CHAR

                    // Check if split line has enough components to be a complete entry
                    if (splitLine.length < 3){
                        System.err.printf("ERROR: Incomplete score entry on line %d\n\t%s\n", lineNumber, line);
                        continue; // Incomplete entry. Continue to next one.
                    }

                    // Parse numbers from split line and create the entries
                    try {
                        int rank = Integer.parseInt(splitLine[0]);
                        int score = Integer.parseInt(splitLine[2]);
                        String name = splitLine[1];
                        entries.add(new ScoreEntry(rank, name, score));
                    } catch (NumberFormatException e) {
                        System.err.printf("NumberFormatException: tetris-scores.txt line %d\n\t%s\n", lineNumber, line);
                    }
                }
                scoreEntryRankedList.setAll(entries);
            } catch (IOException e){
                System.err.println("IOException: Not all scores were read successfully. Some may be missing.");
                e.printStackTrace();
            }
        } else {
            System.out.println("INFO: Scores file does not exist.");
            System.out.println(scoresFile.getPath());
            try {
                if (scoresFile.createNewFile()){
                    System.out.println("INFO: Scores file created successfully!");
                } else {
                    System.out.println("INFO: Scores file could not be created.");
                }
            } catch (IOException e){
                System.err.println("IOException: A problem occured creating a new scores file.");
            }
        }
    }

    private void saveScores(){
        if (scoresFile == null){
            System.err.println("scoresFile is null and scores could not be saved");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scoresFile))){
            // Write the number of score entries on the first line of the file
            writer.write(Integer.toString(scoreEntryRankedList.size()) + '\n');

            // Write each score entry
            for (ScoreEntry entry: scoreEntryRankedList){
                String entryString = Integer.toString(entry.rank.get()) +
                        DELIMITER_CHAR + entry.name.get() +
                        DELIMITER_CHAR + entry.score.get() + '\n';
                writer.write(entryString);
            }
        } catch (IOException e){
            System.err.println("IOException: A problem occured while saving scores.");
            e.printStackTrace();
        }
    }

    public static class ScoreEntry{
        private final IntegerProperty rank;
        private final StringProperty name;
        private final IntegerProperty score;

        private ScoreEntry(String name, int score){
            rank = new SimpleIntegerProperty(0);
            this.name = new SimpleStringProperty(name);
            this.score = new SimpleIntegerProperty(score);
        }

        private ScoreEntry(int rank, String name, int score){
            this.rank = new SimpleIntegerProperty(rank);
            this.name = new SimpleStringProperty(name);
            this.score = new SimpleIntegerProperty(score);
        }

        public StringProperty nameProperty(){
            return name;
        }

        public IntegerProperty scoreProperty(){
            return score;
        }

        public IntegerProperty rankProperty(){
            return rank;
        }
    }
}
