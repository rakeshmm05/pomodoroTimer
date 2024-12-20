package com.example.pomodoro.service;

import com.example.pomodoro.model.PomodoroSession;
import com.example.pomodoro.repository.PomodoroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PomodoroService {
    @Autowired
    private PomodoroRepository pomodoroRepository;

    public PomodoroSession getSession() {
        
        PomodoroSession session = pomodoroRepository.findTopByOrderByIdAsc();
        if (session == null) {
            session = new PomodoroSession();
            session.setWorkDuration(25); 
            session.setBreakDuration(5);
            session.setActive(false);       //set inactive at first
            session.setPomodorosCompleted(0);
            pomodoroRepository.save(session);
        }
        return session;
    }

    public PomodoroSession updateSession(PomodoroSession updatedSession) {
       
        PomodoroSession session = getSession();
        session.setWorkDuration(updatedSession.getWorkDuration());
        session.setBreakDuration(updatedSession.getBreakDuration());
        session.setActive(updatedSession.isActive());
        return pomodoroRepository.save(session);

    }

    public PomodoroSession completePomodoro() {
        
        PomodoroSession session = getSession();
        session.setPomodorosCompleted(session.getPomodorosCompleted() + 1);
        return pomodoroRepository.save(session);

    }

    public PomodoroSession stopSession() {
        PomodoroSession session = getSession();
        session.setActive(false);
        return pomodoroRepository.save(session);
    }

    public PomodoroSession resetSession() {

        PomodoroSession session = getSession();
        session.setActive(false); 
        session.setPomodorosCompleted(0);
        return pomodoroRepository.save(session);
    
    }
}
