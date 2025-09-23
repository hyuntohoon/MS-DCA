package dca.backend.infrastructure;

import dca.backend.entity.K6Result;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackEndRepository extends MongoRepository<K6Result, String> {

    // category 기준으로 executedAt 내림차순 정렬해서 최근 N개 가져오기
    List<K6Result> findTop5ByCategoryOrderByExecutedAtDesc(String category);
}