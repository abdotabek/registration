package api.gossip.uz.repository;

import api.gossip.uz.dto.post.FilterResultDTO;
import api.gossip.uz.dto.post.PostFilterDTO;
import api.gossip.uz.entity.PostEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomRepository {

    EntityManager entityManager;

    public FilterResultDTO<PostEntity> filter(PostFilterDTO filter, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder(" where visible = true ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getQuery() != null) {
            queryBuilder.append("and lower(p.title) like :query ");
            params.put("query", "%" + filter.getQuery().toLowerCase() + "%");
        }
        StringBuilder selectBuilder = new StringBuilder("select p from Post p ").append(queryBuilder).append(" order by p.createdDate desc ");
        StringBuilder countBuilder = new StringBuilder("select count(p) from PostEntity p ").append(queryBuilder);

        Query selectQuery = entityManager.createQuery(selectBuilder.toString());
        selectQuery.setFirstResult((page) * size);
        selectQuery.setMaxResults(size);
        params.forEach(selectQuery::setParameter);
        List<PostEntity> entityList = selectQuery.getResultList();

        Query countQuery = entityManager.createQuery(countBuilder.toString());
        params.forEach(countQuery::setParameter);
        Long totalCount = (Long) countQuery.getSingleResult();


        return new FilterResultDTO<>(entityList, totalCount);
    }
}
