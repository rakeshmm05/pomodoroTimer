package com.example.pomodoro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PomodoroSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // necessary for h2
    private Long id;

    private int workDuration; // in minutes
    private int breakDuration; // in minutes
    private boolean active; 
    private int pomodorosCompleted;
    
}
