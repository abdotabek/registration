package api.gossip.uz.service;

import api.gossip.uz.dto.AttachDTO;
import api.gossip.uz.entity.AttachEntity;
import api.gossip.uz.repository.AttachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachServiceTest {
    @InjectMocks
    private AttachService attachService;
    @Mock
    private AttachRepository repository;
    @Mock
    private ResourceBundleService bundleService;
    String ATTACH_ID = "123.jpg";
    @TempDir
    Path tempDir;


    @BeforeEach
    void setUp() {
        attachService = new AttachService(repository, bundleService);
        attachService.setFolderName(tempDir.toString());
        attachService.setAttachUrl("http://localhost:8080/attach");
    }

    @Test
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void upload() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        AttachEntity attachEntity = new AttachEntity();
        attachEntity.setId(ATTACH_ID);
        attachEntity.setVisible(true);
        attachEntity.setOriginName("originName");
        attachEntity.setPath("pathTest");
        attachEntity.setSize(10L);

        when(repository.save(any(AttachEntity.class))).thenReturn(attachEntity);

        AttachDTO result = attachService.upload(mockFile);
        assertEquals("123.jpg", result.getId());
        assertEquals("originName", result.getOriginName());
        assertEquals(10L, result.getSize());

    }

    @Test
    void open() throws IOException {

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        AttachEntity attachEntity = new AttachEntity();
        attachEntity.setId(ATTACH_ID);
        attachEntity.setVisible(true);
        attachEntity.setOriginName("test.jpg");
        attachEntity.setPath("2025/03/27");
        attachEntity.setSize(mockFile.getSize());
        attachEntity.setExtension(".jpg");

        when(repository.save(any(AttachEntity.class))).thenReturn(attachEntity);
        when(repository.findById(ATTACH_ID)).thenReturn(Optional.of(attachEntity));
        when(bundleService.getMessage("file.not.found" + ATTACH_ID)).thenReturn("File not found: " + ATTACH_ID);

        AttachService spyService = spy(attachService);
        doReturn("2025/03/27").when(spyService).getYmDString();

        AttachDTO uploadResult = spyService.upload(mockFile);
        assertNotNull(uploadResult);

        Path filePath = tempDir.resolve("2025/03/27").resolve(ATTACH_ID);

        try (var mockFiles = mockStatic(Files.class)) {
            mockFiles.when(() -> Files.probeContentType(filePath)).thenReturn("image/jpeg");

            ResponseEntity<Resource> response = spyService.open(ATTACH_ID);
            assertNotNull(response);
        }
    }


    @Test
    void download() {
    }

    @Test
    void delete() {
    }

    @Test
    void getAll() {
    }

    @Test
    void getExtension() {
    }

    @Test
    void getEntity() {
    }

    @Test
    void openURL() {
    }

    @Test
    void attachDTO() {
    }
}