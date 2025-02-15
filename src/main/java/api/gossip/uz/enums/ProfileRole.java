package api.gossip.uz.enums;

import api.gossip.uz.dto.jwt.AuthoritiesConstants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ProfileRole {
    ADMIN(1, "Супер Админ", AuthoritiesConstants.ADMIN, "Super Foydalanuvchi", "Super Admin"),
    USER(2, "Пользователь", AuthoritiesConstants.USER, "Foydalanuvchi", "User"),
    OWNER(3, "Владелец", AuthoritiesConstants.OWNER, "Egasi", "Owner");


    private final Integer priority;
    private final String nameRu;
    private final String code;
    private final String roleCode;
    private final String nameUz;
    private final String nameEng;

    ProfileRole(Integer priority, String nameRu, String roleCode, String nameUz, String nameEng) {
        this.priority = priority;
        this.nameRu = nameRu;
        this.roleCode = roleCode;
        this.code = name();
        this.nameUz = nameUz;
        this.nameEng = nameEng;
    }

    @JsonCreator
    public static ProfileRole forValue(Map<String, String> value) {
        return ProfileRole.valueOf(value.get("code"));
    }

    public static ProfileRole fromProfileRoleCode(String roleCode) {
        return Arrays.stream(ProfileRole.values()).filter(profileRole -> profileRole.getRoleCode().equals(roleCode)).findAny().orElse(null);
    }

}
