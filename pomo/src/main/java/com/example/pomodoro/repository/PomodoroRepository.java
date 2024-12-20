package com.example.pomodoro.repository;

import com.example.pomodoro.model.PomodoroSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PomodoroRepository extends JpaRepository<PomodoroSession, Integer> {

    PomodoroSession findTopByOrderByIdAsc(); // fetch the one and only session

}
