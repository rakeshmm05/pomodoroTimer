package com.example.pomodoro.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.swing.ImageIcon;


public class PomodoroGUI {

    private Frame mainFrame;
    private Label timerLabel, statusLabel;
    private Button startButton, stopButton, resetButton, pauseResumeButton;
    private TextField workDurationField, breakDurationField;
    private boolean isTimerRunning = false;
    private boolean isPaused = false;

    private int workDuration = 25; // Default 25 minutes for work;
    private int breakDuration = 5; // Default 5 minutes for break;
    private int timeLeft; // rem time in seconds
    private boolean isWorkTime = true; // work time or not

    private int pomodorosCompleted = 0; // no of pomodoros completed

    private Thread timerThread;

    public PomodoroGUI() {
        prepareGUI();
    }

    public static void main(String[] args) {

        PomodoroGUI app = new PomodoroGUI();
        app.showEventDemo();
    
    }

    private void prepareGUI() {

        mainFrame = new Frame("Pomodoro Timer");
        mainFrame.setSize(400, 300);
        mainFrame.setLayout(new GridLayout(4, 1));

        ImageIcon img = new ImageIcon("src/main/resources/logo.png");
        mainFrame.setIconImage(img.getImage());


        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        timerLabel = new Label("25:00", Label.CENTER); // default timer on screen
        timerLabel.setFont(new Font("Arial", Font.BOLD, 30));

        statusLabel = new Label("Work Time", Label.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        Panel inputPanel = new Panel(new FlowLayout());
        Label workLabel = new Label("Work (min):");
        workDurationField = new TextField("25", 5);
        Label breakLabel = new Label("Break (min):");
        breakDurationField = new TextField("5", 5);

        inputPanel.add(workLabel);
        inputPanel.add(workDurationField);
        inputPanel.add(breakLabel);
        inputPanel.add(breakDurationField);

        Panel buttonPanel = new Panel(new FlowLayout());
        startButton = new Button("Start");
        pauseResumeButton = new Button("Pause");
        stopButton = new Button("Stop");
        resetButton = new Button("Reset");

        buttonPanel.add(startButton);
        buttonPanel.add(pauseResumeButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);

        mainFrame.add(timerLabel);
        mainFrame.add(statusLabel);
        mainFrame.add(inputPanel);
        mainFrame.add(buttonPanel);
        mainFrame.setVisible(true);

    }

    private void showEventDemo() {

        startButton.addActionListener(e -> startPomodoro());
        pauseResumeButton.addActionListener(e -> pauseResumePomodoro());
        stopButton.addActionListener(e -> stopPomodoro());
        resetButton.addActionListener(e -> resetPomodoro());

    }

    private void startPomodoro() {

        if (isTimerRunning) return; 
    
        try {
            workDuration = Integer.parseInt(workDurationField.getText());
            breakDuration = Integer.parseInt(breakDurationField.getText());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid input. Please enter numbers.");
            return;
        }
    
        timeLeft = workDuration * 60; 
        isWorkTime = true;
        isTimerRunning = true;
        isPaused = false;
        pauseResumeButton.setLabel("Pause"); // resume -> pause button
    
        sendBackendRequest("http://localhost:8080/api/pomodoro/update", true);
    
        timerThread = new Thread(() -> {
            while (isTimerRunning) {
                synchronized (timerThread) {
                    while (isPaused) { // wait if paused
                        try {
                            timerThread.wait();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
    
                if (timeLeft > 0) {
                    try {
                        Thread.sleep(100); //for testing done as 0.1 second
                        timeLeft--;
                        updateTimerLabel();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else if (timeLeft == 0) {
                    if (isWorkTime) {
                        
                        //work -> break

                        isWorkTime = false;
                        pomodorosCompleted++; 

                        timeLeft = breakDuration * 60; //break left

                        statusLabel.setText("Break Time");
                        updateTimerLabel();
                        showPopupMessage("Work time is over! Take a break.");
                        sendCompletePomodoroRequest(); //request the backced to update the completed pomodoros

                    } else {

                        //break -> work


                        isWorkTime = true; // set to work for next pomodoro
                        isTimerRunning = false; // stop timer
                        statusLabel.setText("Pomodoro Complete!");
                        updateTimerLabel();
                        showPopupMessage("Break time is over! Start next Pomodoro.");
                    }
                }
            }
        });
        timerThread.start();

    }
    
    
    
    

    // private void pauseResumePomodoro() {
    //     if (!isTimerRunning) return;
    //     isPaused = !isPaused;
    //     pauseResumeButton.setLabel(isPaused ? "Resume" : "Pause");
    // }

    private void pauseResumePomodoro() {

        if (!isTimerRunning) return; // allow pause/resume only if the timer is running
    
        isPaused = !isPaused; // toggling pause state

        pauseResumeButton.setLabel(isPaused ? "Resume" : "Pause");
    
        // If resuming, ensure the timer thread keeps running
        if (!isPaused) {
            synchronized (timerThread) {
                timerThread.notify(); // Notify the thread to resume
            }
        }

    }
    

    private void stopPomodoro() {

        isTimerRunning = false;
        isPaused = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
        statusLabel.setText("Stopped");
        sendBackendRequest("http://localhost:8080/api/pomodoro/stop", false);

    }

    private void resetPomodoro() {

        isTimerRunning = false;
        isPaused = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
        timeLeft = workDuration * 60; // resetting to work
        isWorkTime = true;
        pomodorosCompleted = 0; // resetting counter
        updateTimerLabel();
        statusLabel.setText("Reset to Work Time");
        sendBackendRequest("http://localhost:8080/api/pomodoro/reset", false);

    }
    

    private void updateTimerLabel() {

        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

    }

    private void sendBackendRequest(String urlStr, boolean isUpdate) {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .header("Content-Type", "application/json");

            if (isUpdate) {
                String jsonBody = String.format("{\"workDuration\":%d,\"breakDuration\":%d,\"active\":%b}", 
                        workDuration, breakDuration, isTimerRunning);
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
            } else {
                requestBuilder.PUT(HttpRequest.BodyPublishers.noBody());
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Response: " + response.body());
            } else {
                System.out.println("Request failed with response code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Error sending request to backend: " + e.getMessage());
        }

    }

    private void sendCompletePomodoroRequest() {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/pomodoro/complete"))
                    .PUT(HttpRequest.BodyPublishers.noBody()) 
                    .header("Content-Type", "application/json")
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            if (response.statusCode() == 200) {
                System.out.println("Pomodoro completion updated successfully.");
            } else {
                System.out.println("Failed to update completed Pomodoros. Response code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Error sending complete Pomodoro request: " + e.getMessage());
        }

    }
    

    private void showPopupMessage(String message) {

        // Display a message box (popup) with the given message

        Dialog dialog = new Dialog(mainFrame, "Pomodoro Timer", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(250, 100);
        dialog.setLocation(mainFrame.getX() + 50, mainFrame.getY() + 100);
        Label msgLabel = new Label(message, Label.CENTER);
        Button closeButton = new Button("OK");
        closeButton.addActionListener(e -> dialog.setVisible(false));
        dialog.add(msgLabel);
        dialog.add(closeButton);
        dialog.setVisible(true);

    }
}

