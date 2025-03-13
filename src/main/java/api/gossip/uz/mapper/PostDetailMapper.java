package api.gossip.uz.mapper;

import api.gossip.uz.enums.GeneralStatus;

import java.time.LocalDateTime;

public interface PostDetailMapper {
    String getPostId();

    String getPostTitle();

    String getPostPhotoId();

    String getPhotoId();

    GeneralStatus getPostCreatedDate();

    Integer getProfileId();

    String getProfileName();

    String getProfileUserName();

    LocalDateTime getCreatedDate();
}
