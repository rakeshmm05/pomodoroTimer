package com.example.pomodoro.controller;

import com.example.pomodoro.model.PomodoroSession;
import com.example.pomodoro.service.PomodoroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pomodoro")
public class PomodoroController {

    @Autowired
    private PomodoroService pomodoroService;

    @GetMapping("/session")
    public ResponseEntity<PomodoroSession> getSession() {
        PomodoroSession session = pomodoroService.getSession();
        return ResponseEntity.ok(session);
    }

    
    @PutMapping("/update")
    public ResponseEntity<PomodoroSession> updateSession(@RequestBody PomodoroSession updatedSession) {
        PomodoroSession session = pomodoroService.updateSession(updatedSession);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/complete")
    public ResponseEntity<PomodoroSession> completePomodoro() {
        PomodoroSession session = pomodoroService.completePomodoro();
        return ResponseEntity.ok(session);
    }

    @PutMapping("/stop")
    public ResponseEntity<PomodoroSession> stopSession() {
        PomodoroSession session = pomodoroService.stopSession();
        return ResponseEntity.ok(session);
    }

    @PutMapping("/reset")
    public ResponseEntity<PomodoroSession> resetSession() {
        PomodoroSession session = pomodoroService.resetSession();
        return ResponseEntity.ok(session);
    }
}
