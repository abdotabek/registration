package api.gossip.uz.repository;

import api.gossip.uz.dto.post.FilterResultDTO;
import api.gossip.uz.dto.post.PostFilterDTO;
import api.gossip.uz.dto.profile.PostAdminFilterDTO;
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
public class CustomPostRepository {

    EntityManager entityManager;

    public FilterResultDTO<PostEntity> filter(PostFilterDTO filter, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder(" where visible = true and status = 'ACTIVE' ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getQuery() != null) {
            queryBuilder.append("and lower(p.title) like :query ");
            params.put("query", "%" + filter.getQuery().toLowerCase() + "%");
        }
        if (filter.getExceptId() != null) {
            queryBuilder.append("and p.id != :exceptId");
            params.put("exceptId", filter.getExceptId());
        }

        StringBuilder selectBuilder = new StringBuilder("select p from PostEntity p ").append(queryBuilder).append(" order by p.createdDate desc ");
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

    public FilterResultDTO<Object[]> filter(PostAdminFilterDTO filter, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder(" where p.visible = true ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getProfileQuery() != null && !filter.getProfileQuery().isBlank()) {
            queryBuilder.append(" and (lower(pr.name) like :profileQuery or lower(pr.username) like :profileQuery)");
            params.put(" profileQuery", "%" + filter.getProfileQuery().toLowerCase() + "%");
        }
        if (filter.getPostQuery() != null && !filter.getPostQuery().isBlank()) {
            queryBuilder.append(" and (lower(p.title) like :postQuery or p.id = :postId)");
            params.put(" postQuery", "%" + filter.getPostQuery().toLowerCase() + "%");
            params.put(" postId", filter.getPostQuery().toLowerCase());
        }
        // full column from PostEntity
        /*StringBuilder selectBuilder = new StringBuilder(" from PostEntity p ")
                .append(" inner join fetch p.profile as pr ")
                .append(queryBuilder)*/
        // Mapped column from PostEntity
        StringBuilder selectBuilder = new StringBuilder("select p.id as postId, p.title as postTitle, p.photoId as postPhotoId, p.createdDate as postCreatedDate," +
                " pr.id as profileId, pr.name as profileName, pr.username as profileUsername ")
                .append(" from PostEntity p ")
                .append(" inner join p.profile as pr ")
                .append(queryBuilder)
                .append(" order by p.createdDate desc ");
        StringBuilder countBuilder = new StringBuilder("select count(p) from PostEntity p inner join p.profile as pr ")
                .append(queryBuilder);

        Query selectQuery = entityManager.createQuery(selectBuilder.toString());
        selectQuery.setFirstResult((page) * size);
        selectQuery.setMaxResults(size);
        params.forEach(selectQuery::setParameter);
        List<Object[]> entityList = selectQuery.getResultList();

        Query countQuery = entityManager.createQuery(countBuilder.toString());
        params.forEach(countQuery::setParameter);
        Long totalCount = (Long) countQuery.getSingleResult();
        return new FilterResultDTO<>(entityList, totalCount);
    }
}
