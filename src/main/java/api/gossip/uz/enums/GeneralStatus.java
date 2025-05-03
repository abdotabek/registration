package api.gossip.uz.enums;

import lombok.Getter;

@Getter
public enum GeneralStatus {
    ACTIVE(1), BLOCK(2),
    IN_REGISTRATION(3), NOT_ACTIVE(4);
    private final int numberOfGeneraStatus;

    GeneralStatus(int size) {
        this.numberOfGeneraStatus = size;
    }
}
