package api.gossip.uz.service;

import api.gossip.uz.exception.EntityNotFound;
import lombok.RequiredArgsConstructor;
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
}
