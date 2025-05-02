package com.example.sosgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;  // Add this import
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.util.Optional;


public class SOSGameGUI extends Application {
    private SOSGameBase game;
    private GridPane boardGrid;
    private Label statusLabel;
    private Label blueScoreLabel;
    private Label redScoreLabel;
    private Pane boardContainer;
    private RadioButton blueHuman, redHuman;
    private RadioButton recordGameRadio;
    private Button replayButton;
    private boolean isReplaying = false;


    @Override
    public void start(Stage primaryStage) {
        try {
            initializeComponents();
            BorderPane mainPane = createMainLayout();
            
            Scene scene = new Scene(mainPane, 900, 700);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SOS Game");
            primaryStage.show();
        } catch (Exception e) {
            showErrorAlert("Application Startup Error", 
                "Failed to initialize the application: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        boardContainer = new Pane();
        boardContainer.setMinSize(500, 500);
        boardGrid = new GridPane();
        boardContainer.getChildren().add(boardGrid);
        
        statusLabel = new Label("Select game options and click Start Game");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        blueScoreLabel = new Label("Blue: 0");
        blueScoreLabel.setTextFill(Color.BLUE);
        blueScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        redScoreLabel = new Label("Red: 0");
        redScoreLabel.setTextFill(Color.RED);
        redScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    }

    private BorderPane createMainLayout() {
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(15));
        
        // Top panel - Game settings + game info
        VBox topPanel = new VBox(10);
        topPanel.getChildren().addAll(
            createGameSettingsPanel(),
            createGameInfoPanel()
        );
        mainPane.setTop(topPanel);
        
        // Left panel - Blue player with right margin
        VBox leftPanel = createPlayerPanel("BLUE PLAYER", true);
        leftPanel.setPadding(new Insets(0, 20, 0, 0)); // Right margin only
        mainPane.setLeft(leftPanel);
        
        // Right panel - Red player with left margin
        VBox rightPanel = createPlayerPanel("RED PLAYER", false);
        rightPanel.setPadding(new Insets(0, 0, 0, 20)); // Left margin only
        mainPane.setRight(rightPanel);
        
        // Center - Game board with equal padding
        StackPane centerPane = new StackPane(boardContainer);
        centerPane.setPadding(new Insets(10, 0, 10, 0)); // Top/bottom padding only
        mainPane.setCenter(centerPane);
        
     // In createMainLayout():
        HBox centerWrapper = new HBox(20); // 20px horizontal gap
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.getChildren().addAll(
            leftPanel,
            centerPane,
            rightPanel
        );
        mainPane.setCenter(centerWrapper);
        
        return mainPane;
    }

    private HBox createGameSettingsPanel() {
        ComboBox<Integer> sizeCombo = new ComboBox<>();
        sizeCombo.getItems().addAll(3, 4, 5, 6, 7, 8, 9, 10);
        sizeCombo.setValue(5);
        
        ToggleGroup modeGroup = new ToggleGroup();
        RadioButton simpleMode = new RadioButton("Simple Game");
        RadioButton generalMode = new RadioButton("General Game");
        simpleMode.setToggleGroup(modeGroup);
        generalMode.setToggleGroup(modeGroup);
        simpleMode.setSelected(true);
        
        recordGameRadio = new RadioButton("Record Game");
        
        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> startNewGame(
            sizeCombo.getValue(),
            simpleMode.isSelected() ? SOSGameBase.GameMode.SIMPLE : SOSGameBase.GameMode.GENERAL
        ));
        
        replayButton = new Button("Replay Game");
        replayButton.setOnAction(e -> replayGame());
        replayButton.setDisable(true);
        
        HBox panel = new HBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5;");
        panel.getChildren().addAll(
            new Label("Board Size:"), sizeCombo,
            new Separator(Orientation.VERTICAL),
            simpleMode, generalMode,
            new Separator(Orientation.VERTICAL),
            recordGameRadio,
            new Separator(Orientation.VERTICAL),
            startButton, replayButton
        );
        
        return panel;
    }
    
    private void replayGame() {
        if (game == null || isReplaying) return;
        
        List<GameMove> moves = game.loadGameRecording();
        if (moves == null || moves.isEmpty()) {
            showErrorAlert("Replay Error", "No game recording found or recording is empty.");
            return;
        }
        
        isReplaying = true;
        replayButton.setDisable(true);
        statusLabel.setText("Replaying game...");
        
        // Create a temporary game for replay
        int size = game.getBoard().getSize();
        SOSGameBase.GameMode mode = game instanceof SimpleSOSGame ? 
            SOSGameBase.GameMode.SIMPLE : SOSGameBase.GameMode.GENERAL;
        SOSGameBase replayGame = SOSGameBase.createGame(mode, size);
        
        updateBoardForReplay(replayGame);
        
        // Play each move with a delay
        new Thread(() -> {
            try {
                for (GameMove move : moves) {
                    Thread.sleep(700);
                    
                    Platform.runLater(() -> {
                        replayGame.makeMove(move.getRow(), move.getCol(), 
                                          move.getSymbol(), move.getPlayer());
                        updateBoardForReplay(replayGame);
                        
                        if (moves.indexOf(move) == moves.size() - 1) {
                            isReplaying = false;
                            replayButton.setDisable(false);
                            statusLabel.setText("Replay completed.");
                        }
                    });
                }
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    isReplaying = false;
                    replayButton.setDisable(false);
                    statusLabel.setText("Replay interrupted.");
                });
            }
        }).start();
    }
    
    private void updateBoardForReplay(SOSGameBase replayGame) {
        boardGrid.getChildren().clear();
        boardContainer.getChildren().clear();
        boardContainer.getChildren().add(boardGrid);
        
        int size = replayGame.getBoard().getSize();
        boardGrid.setGridLinesVisible(true);

        int cellSize = Math.max(30, 500 / size);
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Button cell = new Button();
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                char symbol = replayGame.getBoard().getBoard()[row][col];
                if (symbol != ' ') {
                    cell.setText(String.valueOf(symbol));
                    cell.setFont(Font.font(cellSize * 0.4));
                }
                boardGrid.add(cell, col, row);
            }
        }

        if (replayGame instanceof GeneralSOSGame) {
            for (GeneralSOSGame.SOSLine line : ((GeneralSOSGame) replayGame).getAllSosLines()) {
                drawSOSLine(line);
            }
        }
    }

    private VBox createPlayerPanel(String title, boolean isBlue) {
        ToggleGroup group = new ToggleGroup();
        RadioButton human = new RadioButton("Human");
        RadioButton computer = new RadioButton("Computer");
        human.setToggleGroup(group);
        computer.setToggleGroup(group);
        human.setSelected(true);
        
        if (isBlue) {
            blueHuman = human;
        } else {
            redHuman = human;
        }
        
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle(String.format(
            "-fx-padding: 15; -fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2;",
            isBlue ? "#e6f2ff" : "#ffebee",
            isBlue ? "#3498db" : "#e74c3c"
        ));
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold;");
        panel.getChildren().addAll(titleLabel, new Separator(), human, computer);
        
        return panel;
    }

    private VBox createGameInfoPanel() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle("-fx-padding: 15; -fx-background-color: #f8f8f8;");
        
        Label titleLabel = new Label("GAME INFO");
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            statusLabel,
            new Separator(),
            blueScoreLabel,
            redScoreLabel
        );
        
        return panel;
    }

    private void startNewGame(int size, SOSGameBase.GameMode mode) {
        try {
            boardGrid.getChildren().clear();
            boardContainer.getChildren().clear();
            
            game = SOSGameBase.createGame(mode, size);
            game.setRecordGame(recordGameRadio.isSelected());
            
            // Set player types based on radio button selections
            game.setPlayerType("Blue", blueHuman.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER_EASY);
            game.setPlayerType("Red", redHuman.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER_EASY);
            
            boardGrid = new GridPane();
            boardContainer.getChildren().add(boardGrid);
            
            updateBoard();
            updateStatus();
            
            statusLabel.setText("Game started - " + 
                (mode == SOSGameBase.GameMode.SIMPLE ? "Simple" : "General") + 
                " mode, Size: " + size);
                
            replayButton.setDisable(true);
            
            if (game.getCurrentPlayer().isComputer()) {
                makeComputerMoveWithDelay();
            }
        } catch (Exception e) {
            showErrorAlert("Game Initialization Error", "Failed to start new game: " + e.getMessage());
        }
    }

    private void makeComputerMoveWithDelay() {
        try {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.7));
            delay.setOnFinished(event -> {
                try {
                    if (game != null && !game.isGameOver()) {
                        game.makeComputerMove();
                        updateBoard();
                        updateStatus();
                        
                        if (game.isGameOver()) {
                            game.saveGameRecording();
                            replayButton.setDisable(false);
                            showGameOverAlert();
                        } else if (game.getCurrentPlayer().isComputer()) {
                            makeComputerMoveWithDelay();
                        }
                    }
                } catch (Exception e) {
                    showErrorAlert("Computer Move Error", "Computer failed to make move: " + e.getMessage());
                }
            });
            delay.play();
        } catch (Exception e) {
            showErrorAlert("Animation Error", "Failed to schedule computer move: " + e.getMessage());
        }
    }

    private void updateBoard() {
        try {
            boardGrid.getChildren().clear();
            boardContainer.getChildren().clear();
            boardContainer.getChildren().add(boardGrid);
            
            int size = game.getBoard().getSize();
            boardGrid.setGridLinesVisible(true);

            int cellSize = Math.max(30, 500 / size);
            
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    Button cell = createCell(row, col, cellSize);
                    boardGrid.add(cell, col, row);
                }
            }

            if (game instanceof GeneralSOSGame) {
                for (GeneralSOSGame.SOSLine line : ((GeneralSOSGame) game).getAllSosLines()) {
                    drawSOSLine(line);
                }
            }
        } catch (Exception e) {
            showErrorAlert("Board Update Error", "Failed to update game board: " + e.getMessage());
        }
    }

    private Button createCell(int row, int col, int cellSize) {
        Button cell = new Button();
        cell.setMinSize(cellSize, cellSize);
        cell.setMaxSize(cellSize, cellSize);
        char symbol = game.getBoard().getBoard()[row][col];
        if (symbol != ' ') {
            cell.setText(String.valueOf(symbol));
            cell.setFont(Font.font(cellSize * 0.4));
        }
        
        cell.setOnAction(_ -> handleMove(row, col));
        return cell;
    }

    private void drawSOSLine(GeneralSOSGame.SOSLine line) {
        int cellSize = 500 / game.getBoard().getSize();
        double startX = line.startCol * cellSize + cellSize/2;
        double startY = line.startRow * cellSize + cellSize/2;
        double endX = line.endCol * cellSize + cellSize/2;
        double endY = line.endRow * cellSize + cellSize/2;
        
        Line sosLine = new Line(startX, startY, endX, endY);
        sosLine.setStroke(line.player.equals("Blue") ? Color.BLUE : Color.RED);
        sosLine.setStrokeWidth(3);
        sosLine.setMouseTransparent(true);
        
        boardContainer.getChildren().add(sosLine);
    }

    private void handleMove(final int row, final int col) {
        try {
            if (game == null || game.isGameOver() || isReplaying) return;
            if (game.getCurrentPlayer().isComputer()) return;

            Dialog<Character> dialog = new Dialog<>();
            dialog.setTitle("Make Move");
            dialog.setHeaderText("Player " + game.getCurrentPlayer().getName() + "'s Turn");
            
            ButtonType sButton = new ButtonType("S", ButtonBar.ButtonData.OK_DONE);
            ButtonType oButton = new ButtonType("O", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(sButton, oButton, ButtonType.CANCEL);
            
            dialog.setResultConverter(buttonType -> {
                if (buttonType == sButton) return 'S';
                if (buttonType == oButton) return 'O';
                return null;
            });
            
            Optional<Character> result = dialog.showAndWait();
            result.ifPresent(symbol -> {
                try {
                	if (game.makeMove(row, col, symbol, game.getCurrentPlayer().getName())) {
                	    updateBoard();
                	    updateStatus();
                	    
                	    if (!game.isGameOver() && game.getCurrentPlayer().isComputer()) {
                	        makeComputerMoveWithDelay();
                	    } else if (game.isGameOver()) {
                	        game.saveGameRecording();
                	        replayButton.setDisable(false);
                	        showGameOverAlert();
                	    }
                	}
                } catch (Exception e) {
                    showErrorAlert("Move Error", "Failed to make move: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showErrorAlert("Game Error", "Error during move handling: " + e.getMessage());
        }
    }
    
    private void updateStatus() {
        try {
            if (game == null) {
                statusLabel.setText("Select game options and click Start Game");
                return;
            }
            
            String playerName = game.getCurrentPlayer().getName();
            String playerType = game.getCurrentPlayer().isComputer() ? "Computer" : "Human";
            String mode = game instanceof SimpleSOSGame ? "Simple" : "General";
            
            statusLabel.setText(String.format("%s's turn (%s) - %s Mode", 
                playerName, playerType, mode));
            
            if (game instanceof GeneralSOSGame) {
                blueScoreLabel.setText("Blue: " + ((GeneralSOSGame) game).getBlueScore());
                redScoreLabel.setText("Red: " + ((GeneralSOSGame) game).getRedScore());
            }
        } catch (Exception e) {
            showErrorAlert("Status Update Error", "Failed to update game status: " + e.getMessage());
        }
    }
    
    private void showGameOverAlert() {
        try {
            if (game == null) return;
            
            final StringBuilder messageBuilder = new StringBuilder();
            if (game.getWinner() != null) {
                messageBuilder.append("Player ").append(game.getWinner()).append(" wins!\n");
            } else {
                messageBuilder.append("The game is a tie!\n");
            }
            
            if (game instanceof GeneralSOSGame) {
                messageBuilder.append("Final Score - Blue: ")
                            .append(((GeneralSOSGame) game).getBlueScore())
                            .append(" | Red: ")
                            .append(((GeneralSOSGame) game).getRedScore());
            }
            
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game Over");
                alert.setHeaderText(null);
                alert.setContentText(messageBuilder.toString());
                alert.showAndWait();
            });
        } catch (Exception e) {
            showErrorAlert("Alert Error", "Failed to show game over alert: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
