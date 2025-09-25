package dca.backend.infrastructure;


import dca.backend.entity.Review;
import dca.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 리뷰 많이 쓴 TOP 10 유저 구하기
    @Query(value = "SELECT user_seq " +
            "FROM review " +
            "GROUP BY user_seq " +
            "ORDER BY COUNT(*) DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<Integer> findTop10UsersByReviewCount();
}