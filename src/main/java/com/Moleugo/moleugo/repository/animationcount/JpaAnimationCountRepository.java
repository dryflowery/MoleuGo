package com.Moleugo.moleugo.repository.animationcount;

import com.Moleugo.moleugo.entity.AnimationCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;


import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class JpaAnimationCountRepository implements AnimationCountRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Map<String, Integer> findAllCountsByEmail(String email) {
        AnimationCount animationCount = em.find(AnimationCount.class, email);
        return animationCount.toMap();
    }

    @Override
    public void incrementCountsByEmail(String email, String algorithm) {
        String query = "UPDATE AnimationCount a " +
                       "SET a." + algorithm + " = a." + algorithm + " + 1 " +
                       "WHERE a.email = :email";

        em.createQuery(query).setParameter("email", email).executeUpdate();
    }

    @Override
    public void insert(AnimationCount animationCount) {
        em.persist(animationCount);
    }
}
