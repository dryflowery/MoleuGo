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

import java.time.DayOfWeek;
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

    public ResponseEntity<List<Integer>> getDailyGoal(String userSession, int dailyGoalYear) {
        Member member = getMember(userSession);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            // 선택한 년도, 시작날짜, 마지막날짜 설정 (1월 1일 ~ 12월 31일)
            LocalDate startDate = LocalDate.of(dailyGoalYear, Month.JANUARY, 1);
            LocalDate endDate = LocalDate.of(dailyGoalYear, Month.DECEMBER, 31);

            List<DailyGoal> dailyGoals = dailyGoalRepository.findAllByEmail(member.getEmail());

            // 사용자가 클릭한 년도의 튜플을 { 날짜, DailyGoal }로 매핑
            Map<LocalDate, DailyGoal> goalMap = dailyGoals.stream()
                    .filter(d -> d.getId().getGoalDate().getYear() == dailyGoalYear)
                    .collect(Collectors.toMap(d -> d.getId().getGoalDate(), Function.identity()));

            // 1주일(일 ~ 토) 기준으로 53주(가능한 최대 주)
            List<Integer> result = new ArrayList<>();

            // 시작 요일
            int startDayOfWeek = (startDate.getDayOfWeek() == DayOfWeek.SUNDAY) ? 0 : startDate.getDayOfWeek().getValue();

            // 총 53주 * 7일 = 371
            int totalDays = 53 * 7;
            LocalDate currentDate = startDate;

            for (int i = 0; i < totalDays; i++) {
                int week = i / 7;
                int day = i % 7;

                // startDate 이전과 endDate 이후는 -1 (프론트에서는 transparent로 보임)
                if ((week == 0 && day < startDayOfWeek) || currentDate.isAfter(endDate)) {
                    result.add(-1);
                }
                else {
                    // date가 currentDate인 튜플이 null이면 0, 아니면 achieved_count 대입
                    DailyGoal goal = goalMap.get(currentDate);
                    result.add(goal != null ? goal.getAchievedCount() : 0);
                    currentDate = currentDate.plusDays(1);
                }
            }

            return ResponseEntity.ok(result);
        }
    }
}