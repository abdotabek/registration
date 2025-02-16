package api.gossip.uz.service;

import api.gossip.uz.exception.BadRequestException;
import api.gossip.uz.exception.EntityNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component

public class BaseService {

    @Lazy
    @Autowired
    protected ResourceBundleService resourceBundleService;

    protected Supplier<EntityNotFound> notFound(final String entity) {
        return () -> new EntityNotFound(resourceBundleService.getMessage("not.found", entity));
    }

    protected Supplier<EntityNotFound> notFound() {
        return () -> new EntityNotFound(resourceBundleService.getMessage(""));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final Integer errorCode) {
        return () -> new EntityNotFound(resourceBundleService.getMessage("not.found", entity), errorCode);
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final Long id) {
        return () -> new EntityNotFound((resourceBundleService.getMessage("not.found", entity, id)));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final String id) {
        return () -> new EntityNotFound(resourceBundleService.getMessage("not.found", entity, id));
    }

    protected Supplier<EntityNotFound> notFound(final String entity, final String id, final Integer errorCode) {
        return () -> new EntityNotFound(resourceBundleService.getMessage("not.found", entity, id), errorCode);
    }

    protected Supplier<BadRequestException> badRequest(String message) {
        return () -> new BadRequestException(message);
    }

    protected Supplier<BadRequestException> badRequest(String message, int errorCode) {
        return () -> new BadRequestException(message, errorCode);
    }
}
