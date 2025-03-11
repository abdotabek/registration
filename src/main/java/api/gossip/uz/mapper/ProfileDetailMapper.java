package api.gossip.uz.mapper;

import api.gossip.uz.enums.GeneralStatus;

import java.time.LocalDateTime;

public interface ProfileDetailMapper {

    Integer getId();

    String getName();

    String getUsername();

    String getPhotoId();

    GeneralStatus getStatus();

    LocalDateTime getCratedDate();

    Long getPostCount();

    String getRoles();
}
