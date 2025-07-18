package api.gossip.uz.service;

import api.gossip.uz.exception.BadRequestException1;
import api.gossip.uz.exception.EntityNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component

public class BaseService {

    @Lazy
    protected ResourceBundleService bundleService;

    protected Supplier<EntityNotFound> notFound(final String entity) {
        return () -> new EntityNotFound(bundleService.getMessage("not.found", entity));
    }

    protected Supplier<EntityNotFound> notFound() {
        return () -> new EntityNotFound(bundleService.getMessage("not.found"));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final Integer errorCode) {
        return () -> new EntityNotFound(bundleService.getMessage("not.found", entity), errorCode);
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final Long id) {
        return () -> new EntityNotFound((bundleService.getMessage("not.found", entity, id)));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final String id) {
        return () -> new EntityNotFound(bundleService.getMessage("not.found", entity, id));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final String id, final Integer errorCode) {
        return () -> new EntityNotFound(bundleService.getMessage("not.found", entity, id), errorCode);
    }

    protected Supplier<BadRequestException1> badRequest(String message) {
        return () -> new BadRequestException1(message);
    }

    protected Supplier<BadRequestException1> badRequest(String message, int errorCode) {
        return () -> new BadRequestException1(message, errorCode);
    }
}
