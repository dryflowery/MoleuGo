package com.Moleugo.moleugo.service.member.profile;

import com.Moleugo.moleugo.entity.AnimationCount;
import com.Moleugo.moleugo.entity.DailyGoal;
import com.Moleugo.moleugo.entity.Id.DailyGoalId;
import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.animationcount.AnimationCountRepository;
import com.Moleugo.moleugo.repository.dailygoal.DailyGoalRepository;
import com.Moleugo.moleugo.repository.member.MemberRepository;
import com.Moleugo.moleugo.service.member.auth.AuthService;
import com.Moleugo.moleugo.service.member.mail.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailChangeService {

    private final HttpSession session;
    private final MemberRepository memberRepository;
    private final AnimationCountRepository animationCountRepository;
    private final DailyGoalRepository dailyGoalRepository;
    private final MailService mailService;
    private final AuthService authService;

    public HttpStatus requestEmailChange(String sessionId, String newEmail) {
        Member currentMember = (Member) session.getAttribute(sessionId);
        if (currentMember == null) return HttpStatus.UNAUTHORIZED;

        if (memberRepository.isRegisteredEmail(newEmail)) {
            return HttpStatus.CONFLICT;
        }

        Member newMember = new Member(newEmail, currentMember.getPassword(), currentMember.getAccount_type());
        String uuid = authService.createSession(newMember, 1800);
        session.setAttribute("old_email_" + uuid, currentMember.getEmail());

        String verificationLink = "http://localhost:8080/user/change-email/" + uuid;
        String content = """
            이메일 변경 요청이 접수되었습니다.<br/>
            아래 링크를 클릭하여 새 이메일을 인증해주세요.<br/><br/>
            🔗 <a href="%s">이메일 인증하기</a>
        """.formatted(verificationLink);

        mailService.setTo(newEmail);
        mailService.setTitle("[moleugo] 이메일 변경 인증 요청");
        mailService.setContent(content);
        mailService.send();

        return HttpStatus.OK;
    }

    public HttpStatus confirmEmailChange(String uuid) {
        Member sessionMember = (Member) session.getAttribute(uuid);
        String oldEmail = (String) session.getAttribute("old_email_" + uuid);

        if (sessionMember == null || oldEmail == null) {
            return HttpStatus.NOT_FOUND;
        }

        session.removeAttribute(uuid);
        session.removeAttribute("old_email_" + uuid);

        Member oldMember = memberRepository.findByEmail(oldEmail);
        Member newMember = new Member(
                sessionMember.getEmail(),
                oldMember.getPassword(),
                oldMember.getAccount_type(),
                null,
                oldMember.getNickname()
        );
        memberRepository.registerMember(newMember);

        // AnimationCount 마이그레이션
        AnimationCount oldCount = animationCountRepository.findByEmail(oldEmail);
        if (oldCount != null) {
            AnimationCount newCount = new AnimationCount();
            newCount.setEmail(newMember.getEmail());
            newCount.setLinkedList(oldCount.getLinkedList());
            newCount.setStack(oldCount.getStack());
            newCount.setQueue(oldCount.getQueue());
            newCount.setDeque(oldCount.getDeque());
            newCount.setHeap(oldCount.getHeap());
            newCount.setBinarySearch(oldCount.getBinarySearch());
            newCount.setBubbleSort(oldCount.getBubbleSort());
            newCount.setSelectionSort(oldCount.getSelectionSort());
            newCount.setInsertionSort(oldCount.getInsertionSort());
            newCount.setDfs(oldCount.getDfs());
            newCount.setBfs(oldCount.getBfs());
            newCount.setDijkstra(oldCount.getDijkstra());
            newCount.setBellmanFord(oldCount.getBellmanFord());
            newCount.setFloydWarshall(oldCount.getFloydWarshall());
            newCount.setConvexHull(oldCount.getConvexHull());
            animationCountRepository.insert(newCount);
            animationCountRepository.deleteByEmail(oldEmail);
        }

        // DailyGoal 마이그레이션
        List<DailyGoal> oldGoals = dailyGoalRepository.findAllByEmail(oldEmail);
        for (DailyGoal oldGoal : oldGoals) {
            DailyGoalId oldId = oldGoal.getId();
            DailyGoalId newId = new DailyGoalId(newMember.getEmail(), oldId.getGoalDate());
            DailyGoal newGoal = new DailyGoal();
            newGoal.setId(newId);
            newGoal.setAchievedCount(oldGoal.getAchievedCount());
            newGoal.setAchievedListJson(oldGoal.getAchievedListJson());
            newGoal.setGoalListJson(oldGoal.getGoalListJson());
            dailyGoalRepository.save(newGoal);
            dailyGoalRepository.delete(oldGoal);
        }

        memberRepository.delete(oldMember);

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attr = attributeNames.nextElement();
            Object obj = session.getAttribute(attr);

            if (obj instanceof Member m && m.getEmail().equals(oldEmail)) {
                session.setAttribute(attr, newMember);
                log.info("세션 이메일 업데이트 완료: {}", newMember.getEmail());
                break;
            }
        }

        return HttpStatus.OK;
    }
}
