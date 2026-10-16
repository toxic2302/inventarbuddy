package de.toxic2302.inventarbuddy.core.modules.media.service;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.item.repository.ItemRepository;
import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaContentDto;
import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaDto;
import de.toxic2302.inventarbuddy.core.modules.media.entity.Media;
import de.toxic2302.inventarbuddy.core.modules.media.mapper.MediaMapper;
import de.toxic2302.inventarbuddy.core.modules.media.repository.MediaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class MediaService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String IMAGE_CONTENT_TYPE_PREFIX = "image/";

    private final MediaRepository mediaRepository;
    private final ItemRepository itemRepository;
    private final MediaMapper mediaMapper;

    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public MediaDto getMedia(UUID id) {
        return mediaMapper.mapToDto(findOwnedMedia(id));
    }

    @Transactional(readOnly = true)
    public List<MediaDto> getAllMediaByUser() {
        return mediaMapper.mapToDtoList(mediaRepository.findAllByUser(authenticatedUserService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public List<MediaDto> getAllMediaByItem(UUID itemId) {
        final Item item = findOwnedItem(itemId);
        return mediaMapper.mapToDtoList(
                mediaRepository.findAllByItemAndUser(item, authenticatedUserService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public MediaContentDto getMediaContent(UUID id) {
        final Media media = findOwnedMedia(id);
        return new MediaContentDto(media.getData(), media.getContentType(), media.getFileName());
    }

    @Transactional
    public MediaDto createMedia(String fileName, byte[] data, String contentType, String itemId) {
        if (data == null || data.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must not be empty");
        }
        if (!isSupportedContentType(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be an image or a PDF");
        }

        final Media media = new Media();
        media.setFileName(fileName);
        media.setContentType(contentType);
        media.setFileSize(data.length);
        media.setData(data);
        media.setUser(authenticatedUserService.getCurrentUser());
        media.setItem(findOwnedItem(itemId));

        return mediaMapper.mapToDto(mediaRepository.save(media));
    }

    @Transactional
    public MediaDto updateMedia(MediaDto mediaDto) {
        final Media media = findOwnedMedia(UUID.fromString(mediaDto.id()));
        media.setFileName(mediaDto.fileName());
        media.setItem(findOwnedItem(mediaDto.itemId()));

        return mediaMapper.mapToDto(mediaRepository.save(media));
    }

    @Transactional
    public void deleteMedia(UUID id) {
        mediaRepository.delete(findOwnedMedia(id));
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType != null
                && (contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX) || PDF_CONTENT_TYPE.equals(contentType));
    }

    private Item findOwnedItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return null;
        }
        return findOwnedItem(UUID.fromString(itemId));
    }

    private Item findOwnedItem(UUID itemId) {
        return itemRepository.findByIdAndUser(itemId, authenticatedUserService.getCurrentUser()).orElseThrow();
    }

    private Media findOwnedMedia(UUID id) {
        return mediaRepository.findByIdAndUser(id, authenticatedUserService.getCurrentUser()).orElseThrow();
    }
}
