package root.general.main.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import root.general.main.data.dto.DefaultMessageDTO;
import root.general.main.data.User;
import root.general.main.data.dto.userprofile.*;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.exceptions.ProfilePictureUploadException;
import root.general.main.exceptions.UserProfileEditException;
import root.general.main.services.tokens.TokenChangeEmailService;
import root.general.main.services.user.UserProfileService;
import root.general.main.utils.MapperUtils;

@RestController
@RequestMapping("/profile")
@Tag(
        name = "Операции над профилем пользователя",
        description = "Предоставляет API для изменения информации о профиле пользователя.")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final TokenChangeEmailService tokenChangeEmailService;

    public UserProfileController(UserProfileService userProfileService, TokenChangeEmailService tokenChangeEmailService) {
        this.userProfileService = userProfileService;
        this.tokenChangeEmailService = tokenChangeEmailService;
    }

    @Operation(
            summary = "Получить всю информацию о профиле пользователя",
            description = "Получить всю информацию о профиле пользователя от его лица. "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о профиле пользователя получена успешно.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping
    public ResponseEntity<UserProfileFullDTO> getUserProfileInfo(@AuthenticationPrincipal User user) {
        UserProfileFullDTO userProfile = MapperUtils.mapUserToUserFullProfileInfo(user);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    @Operation(
            summary = "Получить информацию о профиле пользователя, доступную для изменения",
            description = "Получить информацию о профиле пользователя, доступную для изменения (шаблон)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о профиле пользователя, доступная для изменения, получена успешно.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/edit")
    public ResponseEntity<UserProfileEditDTO> getUserProfileEditInfoDto (@AuthenticationPrincipal User user) {
        UserProfileEditDTO profileToEdit = MapperUtils.mapUserToUserEditProfileInfo(user);
        return new ResponseEntity<>(profileToEdit, HttpStatus.OK);
    }

    @Operation(
            summary = "Изменить информация о профиле пользователя",
            description = "Изменение информации о профиле пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о профиле пользователя была успешно изменена. " +
                    "Если же были предоставлены недопустимые данные, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/edit")
    public ResponseEntity<?> editUserProfileInfo(@AuthenticationPrincipal User user,
                                                 @RequestBody UserProfileEditDTO editedProfile) {
        try {
            UserProfileEditDTO changedProfile = MapperUtils.mapUserToUserEditProfileInfo(
                    userProfileService.changeUserProfileInfo(user, editedProfile)
            );
            return new ResponseEntity<>(changedProfile, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO(profileEditException.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Запросить изменение почты пользователя",
            description = "Запросить изменение почты пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Запрос на изменение почты пользователя был успешно отправлен. " +
                    "Если же были предоставлены недопустимые данные, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/edit/email")
    public ResponseEntity<DefaultMessageDTO> requestEditEmail(@AuthenticationPrincipal User user,
                                                   @RequestBody UserProfileEmailDTO emailInfoDTO) {
        try {
            String msg = userProfileService.requestChangeUserEmail(user, emailInfoDTO.getEmail());
            return new ResponseEntity<>(
                    new DefaultMessageDTO(msg),
                    HttpStatus.OK);
        } catch (UserProfileEditException | DatabaseRecordNotFound profileEditException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO(profileEditException.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Failed processing the provided data."),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Запросить шаблон для изменения почты пользователя",
            description = "Запросить шаблон для изменения почты пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Шаблон для изменения почты был успешно получен.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/edit/email")
    public ResponseEntity<UserProfileEmailDTO> requestEditEmailDto (@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(new UserProfileEmailDTO(user.getEmail()), HttpStatus.OK);
    }

    @Operation(
            summary = "Подтвердить изменение почты",
            description = "Подтвердить изменение почты по токену (ссылке), отправленному ранее на почту (предыдущую)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Почта пользователя была успешно измененена на новую. " +
                    "Если же токен был неверен, или был аутентифицирован иной пользователь, " +
                    "будет возвращен код состояния `NOT_FOUND`",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/edit/email/confirm/{token}")
    public ResponseEntity<DefaultMessageDTO> getUserProfileEditInfo(@AuthenticationPrincipal User user,
                                                                    @PathVariable String token) {
        try {
            if (tokenChangeEmailService.confirmTokenForUser(token, user))
                return new ResponseEntity<>(
                        new DefaultMessageDTO("You have successfully changed your e-mail!"),
                        HttpStatus.OK);
        } catch (DatabaseRecordNotFound | NullPointerException ignored) {}
        return new ResponseEntity<>(
                new DefaultMessageDTO("Not found."),
                HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Изменить аватарку пользователя",
            description = "Изменить аватарку пользователя (на предоставленную в запросе)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Аватарка пользователя была успешно измененена на новую. " +
                    "Если же предоставленный файл аватарки неверного формата (не: \".jpeg, .jpg, .png\"), " +
                    "или же произошла непредвиденная ошибка во время обработки предоставленного файла, " +
                    "будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/edit/pfp")
    public ResponseEntity<DefaultMessageDTO> uploadProfilePicture(@AuthenticationPrincipal User user,
                                                       @RequestParam("image") MultipartFile pfp) {
        try {
            String msg = userProfileService.uploadUserProfilePicture(user, pfp);
            return new ResponseEntity<>(
                    new DefaultMessageDTO(msg),
                    HttpStatus.OK);
        } catch (ProfilePictureUploadException pictureUploadException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO(pictureUploadException.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Failed processing the provided data."),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Получить аватарку пользователя",
            description = "Получить аватарку пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Аватарка пользователя была успешно получена.",
            content = @Content(mediaType = "image/jpg")
    )
    @GetMapping({"/pfp", "/edit/pfp"})
    public ResponseEntity<InputStreamResource> getProfilePicture(@AuthenticationPrincipal User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        InputStreamResource inputStream = userProfileService.getUserProfilePictureAsInputStreamResource(user);

        return new ResponseEntity<>(inputStream, headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Получить шаблон для изменения пароля пользователя",
            description = "Получить шаблон для изменения пароля пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Шаблон для изменения пароля пользователя был успешно получен.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/edit/password")
    public ResponseEntity<UserProfilePasswordDTO> getEditUserPasswordDto (@AuthenticationPrincipal User user,
                                                 @RequestBody UserProfilePasswordDTO userProfilePassword) {
        return new ResponseEntity<>(
                new UserProfilePasswordDTO("your old password", "your new password"),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Изменить пароль",
            description = "Изменить пароль пользователя (подтвердив старый)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пароль пользователя был успешно изменён," +
                    "в противном случае, если предоставленный старый пароль неверный, " +
                    "или же предоставленные данные оказались некорректными (пустые), " +
                    "будет возвращен код состояния `BAD_REQUEST`.",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/edit/password")
    public ResponseEntity<DefaultMessageDTO> editUserPassword(@AuthenticationPrincipal User user,
                                                   @RequestBody UserProfilePasswordDTO userProfilePassword) {
        try {
            String msg = userProfileService.changeUserPassword(user, userProfilePassword);
            return new ResponseEntity<>(
                    new DefaultMessageDTO(msg),
                    HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO(profileEditException.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Failed processing the provided data."),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Получить шаблон для изменения настроек приватности",
            description = "Получить шаблон для изменения настроек приватности пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Шаблон для изменения настроек приватности пользователя был успешно получен.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/edit/privacy")
    public ResponseEntity<UserProfilePrivacyDTO> getEditPrivacySettings(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                new UserProfilePrivacyDTO(),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Изменить настройки приватности",
            description = "Изменить настройки приватности пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Настройки приватности были успешно изменены. " +
                    "Пользователи возвращены обновлённые настройки приватности.",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/edit/privacy")
    public ResponseEntity<UserProfilePrivacyDTO> editPrivacySettings(@AuthenticationPrincipal User user,
                                                 @RequestBody UserProfilePrivacyDTO userProfilePrivacy) {
        userProfileService.setFriendsListHidden(user, userProfilePrivacy.isFriendsListHidden());
        userProfileService.setMessagesFriendsOnly(user, userProfilePrivacy.isFriendsOnlyMessages());
        return new ResponseEntity<>(userProfilePrivacy, HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удалить пользователя (приготовить пользователя к удалению)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь был успешно заблокирован (locked), " +
                    "и приготовлен к удалению. Если во время обработки запроса возникнет ошибка, " +
                    "будет возвращен код состояния `BAD_REQUEST`.",
            content = @Content(mediaType = "application/json")
    )
    @DeleteMapping("/delete")
    public ResponseEntity<DefaultMessageDTO> deleteUser(@AuthenticationPrincipal User user, HttpServletRequest request) {
        try {
            String msg = userProfileService.prepareUserForDelete(user, request);
            return new ResponseEntity<>(
                    new DefaultMessageDTO(msg),
                    HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO(profileEditException.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Failed processing the provided data."),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
