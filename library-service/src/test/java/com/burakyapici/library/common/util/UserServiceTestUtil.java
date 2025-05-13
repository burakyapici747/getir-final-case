package com.burakyapici.library.common.util;

import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.request.UserUpdateRequest;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.UserDetailDto;
import com.burakyapici.library.domain.dto.UserDto;
import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.domain.enums.Role;
import com.burakyapici.library.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserServiceTestUtil {

    public static User createSampleUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("1234567890");
        user.setAddress("Test Address 123");
        user.setRole(Role.PATRON);
        user.setPatronStatus(PatronStatus.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }

    public static User createSampleUserWithId(UUID userId) {
        User user = createSampleUser();
        user.setId(userId);
        return user;
    }

    public static List<User> createSampleUsers(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    User user = new User();
                    user.setEmail("test" + i + "@example.com");
                    user.setPasswordHash("hashedpassword" + i);
                    user.setFirstName("Test" + i);
                    user.setLastName("User" + i);
                    user.setPhoneNumber("123456789" + i);
                    user.setAddress("Test Address " + i);
                    user.setRole(Role.PATRON);
                    user.setPatronStatus(PatronStatus.ACTIVE);
                    user.setId(UUID.randomUUID());
                    user.setCreatedAt(Instant.now());
                    user.setUpdatedAt(Instant.now());
                    return user;
                })
                .collect(Collectors.toList());
    }

    public static UserDto createSampleUserDto(User user) {
        return new UserDto(
                user.getId().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getPatronStatus()
        );
    }

    public static List<UserDto> createSampleUserDtos(List<User> users) {
        return users.stream()
                .map(UserServiceTestUtil::createSampleUserDto)
                .collect(Collectors.toList());
    }

    public static UserDetailDto createSampleUserDetailDto(User user) {
        return new UserDetailDto(
                user.getId().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getPatronStatus()
        );
    }

    public static RegisterRequest createSampleRegisterRequest() {
        return new RegisterRequest(
            "newuser@example.com",
            "password123",
            "password123",
            "New",
            "User",
            "9876543210",
            "New Address 456"
        );
    }

    public static UserUpdateRequest createSampleUserUpdateRequest() {
        return new UserUpdateRequest(
            "updated@example.com",
            "Updated",
            "User",
            "+905551112233",
            "Updated Address 789",
            PatronStatus.ACTIVE
        );
    }

    public static Page<User> createUserPage(List<User> users, int currentPage, int pageSize, long totalElements) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return new PageImpl<>(users, pageable, totalElements);
    }

    public static PageableDto<UserDto> createUserPageableDto(List<UserDto> userDtos, int totalPages, int elementsPerPage, int currentPage) {
        return new PageableDto<>(
                userDtos,
                totalPages,
                elementsPerPage,
                currentPage,
                currentPage < totalPages - 1,
                currentPage > 0
        );
    }
}
