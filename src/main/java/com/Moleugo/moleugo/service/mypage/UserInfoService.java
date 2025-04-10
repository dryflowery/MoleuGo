package com.Moleugo.moleugo.service.mypage;

import com.Moleugo.moleugo.entity.DailyGoal;
import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.dailygoal.DailyGoalRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final HttpSession session;
    private final DailyGoalRepository dailyGoalRepository;

    private Member getMember(String userSession) {
        return (Member) session.getAttribute(userSession);
    }

    public ResponseEntity<String> getUserEmail(String userSession) {
        Member member = getMember(userSession);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            return ResponseEntity.ok(member.getEmail());
        }
    }

    public ResponseEntity<String> getUserNickname(String userSession) {
        Member member = getMember(userSession);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            return ResponseEntity.ok(member.getNickname());
        }
    }

    public ResponseEntity<String> getUserAccountType(String userSession) {
        Member member = getMember(userSession);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            return ResponseEntity.ok(member.getAccount_type());
        }
    }

    public ResponseEntity<List<Integer>> getDailyGoal(String userSession) {
        Member member = getMember(userSession);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            // 년도, 시작날, 마지막날 설정
            int currentYear = LocalDate.now().getYear();
            LocalDate startDate = LocalDate.of(currentYear, Month.JANUARY, 1);
            LocalDate endDate = LocalDate.of(currentYear, Month.DECEMBER, 31);

            // 해당 이메일의 모든 DailyGoal 데이터 조회
            List<DailyGoal> dailyGoals = dailyGoalRepository.findAllByEmail(member.getEmail());

            // 현재 연도에 해당하는 goalDate 기준으로 매핑
            Map<LocalDate, DailyGoal> goalMap = dailyGoals.stream()
                    .filter(d -> d.getId().getGoalDate().getYear() == currentYear)
                    .collect(Collectors.toMap(d -> d.getId().getGoalDate(), Function.identity()));

            // 1월 1일부터 12월 31일까지 순회하면서 해당 날짜에 대한 achievedCount 채우기
            List<Integer> result = new ArrayList<>();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                DailyGoal goal = goalMap.get(date);

                if(goal != null) {
                    result.add(goal.getAchievedCount());
                }
                else {
                    result.add(0);
                }
            }

            return ResponseEntity.ok(result);
        }
    }
}
