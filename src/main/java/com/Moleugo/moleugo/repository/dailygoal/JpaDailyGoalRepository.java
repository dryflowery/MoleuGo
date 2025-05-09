package com.Moleugo.moleugo.repository.dailygoal;

import com.Moleugo.moleugo.entity.DailyGoal;
import com.Moleugo.moleugo.entity.Id.DailyGoalId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class JpaDailyGoalRepository implements DailyGoalRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(DailyGoal dailyGoal) {
        em.persist(dailyGoal);
    }

    @Override
    public DailyGoal findById(DailyGoalId id) {
        return em.find(DailyGoal.class, id);
    }

    @Override
    public List<DailyGoal> findAllByEmail(String email) {
        return em.createQuery("SELECT d FROM DailyGoal d WHERE d.id.email = :email ORDER BY d.id.goalDate ASC", DailyGoal.class)
                .setParameter("email", email)
                .getResultList();
    }

    @Override
    public void updateAchievedCount(DailyGoal dailyGoal) {
        em.merge(dailyGoal);
    }

    @Override
    public void delete(DailyGoal dailyGoal) {
        em.remove(em.contains(dailyGoal) ? dailyGoal : em.merge(dailyGoal));
    }

}