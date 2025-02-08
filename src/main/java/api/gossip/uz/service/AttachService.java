package api.gossip.uz.service;

import api.gossip.uz.dto.AttachDTO;
import api.gossip.uz.entity.AttachEntity;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.AttachRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachService {

    final AttachRepository attachRepository;

    @Value("${attach.upload.folder}")
    String folderName;

    @Value("${attach.upload.url}")
    String attachUrl;


    public AttachDTO upload(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw ExceptionUtil.throwNotFoundException("file not found");
        }
        try {
            String pathFolder = getYmDString(); // 2024/09/27
            String key = UUID.randomUUID().toString();
            String extension = getExtension(Objects.requireNonNull(multipartFile.getOriginalFilename())); // .jpg, .png, .mp4

            // create folder if not exist
            File folder = new File(folderName + "/" + pathFolder);
            if (!folder.exists()) {
                boolean file = folder.mkdirs();
            }
            // save to system
            byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(folderName + "/" + pathFolder + "/" + key + "." + extension);
            Files.write(path, bytes);

            //attach to db
            AttachEntity attachEntity = new AttachEntity();
            attachEntity.setId(key + "." + extension);
            attachEntity.setPath(pathFolder);
            attachEntity.setSize(multipartFile.getSize());
            attachEntity.setOriginName(multipartFile.getOriginalFilename());
            attachEntity.setExtension(extension);
            attachEntity.setVisible(true);

            return toDTO(attachRepository.save(attachEntity));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<Resource> open(String id) {
        AttachEntity entity = getEntity(id);
        Path filePath = Paths.get(folderName + "/" + entity.getPath() + "/" + entity.getId()).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw ExceptionUtil.throwNotFoundException("file not found : " + id);
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<Resource> download(String id) {
        try {
            AttachEntity attachEntity = getEntity(id);
            Path filePath = Paths.get(getPath(attachEntity)).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; fileName=\"" + attachEntity.getOriginName() + "\"").body(resource);
            } else {
                throw new RuntimeException("could not read the file!");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("could not read the dile");
        }
    }

    public boolean delete(String id) {
        AttachEntity entity = getEntity(id);
        attachRepository.delete(id);
        File file = new File(folderName + "/" + entity.getPath() + "/" + entity.getId());
        boolean result = false;
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    public PageImpl<AttachDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AttachEntity> entityPage = attachRepository.findAll(pageable);
        return new PageImpl<>(entityPage.stream().map(this::toDTO).toList(), pageable, entityPage.getTotalElements());
    }

    private String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    public String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    public AttachEntity getEntity(String id) {
        Optional<AttachEntity> optional = attachRepository.findById(id);
        if (optional.isEmpty()) {
            System.out.println("Attach error : file not found");
            throw ExceptionUtil.throwNotFoundException("attach not found");
        }
        return optional.get();
    }

    private String getPath(AttachEntity attachEntity) {
        return folderName + "/" + attachEntity.getPath() + "/" + attachEntity.getId();
    }

    public String openURL(String fileName) {
        return attachUrl + "/open/" + fileName;
    }

    private AttachDTO toDTO(AttachEntity attachEntity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(attachEntity.getId());
        attachDTO.setSize(attachEntity.getSize());
        attachDTO.setOriginName(attachEntity.getOriginName());
        attachDTO.setExtension(attachEntity.getExtension());
        attachDTO.setCreatedDate(attachEntity.getCreatedDate());
        attachDTO.setUrl(openURL(attachEntity.getId()));
        return attachDTO;
    }


    public AttachDTO attachDTO(String photoId) {
        if (photoId == null) return null;
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(photoId);
        attachDTO.setUrl(openURL(photoId));
        return attachDTO;
    }
}
