package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 동시성 문제를 위해 AtomicLong, ConcurrentHashMap 사용
 */
@Slf4j
@Repository
public class MemberRepository {
    // 메모리 DB 역할
    private static Map<Long, Member> store = new ConcurrentHashMap<>();

    // sequence 역할
    private static AtomicLong sequence = new AtomicLong(0);

    public Member save(Member member) {
        member.setId(sequence.incrementAndGet());
        log.info("save member={}", member);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

    public void clearStore() {
        store.clear();
    }
}
