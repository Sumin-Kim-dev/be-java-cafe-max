package kr.codesqaud.cafe.user.service;

import kr.codesqaud.cafe.user.controller.response.UserListResponse;
import kr.codesqaud.cafe.user.controller.response.UserProfileResponse;
import kr.codesqaud.cafe.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(User user) {
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    public List<UserListResponse> getUserList() {
        List<User> users = userRepository.findAll();
        return IntStream.range(0, users.size())
                .mapToObj(index -> UserListResponse.from(users.get(index), index + 1))
                .collect(Collectors.toUnmodifiableList());
    }

    public UserProfileResponse getProfile(String userId) {
        return UserProfileResponse.from(userRepository.findByUserId(userId).orElseThrow());
    }

    public Optional<User> login(String userId, String password) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent() && !password.equals(user.get().getPassword())) {
            user = Optional.empty();
        }
        return user;
    }

    public boolean updateUser(User currentUser, User newProfile, String currPassword) {
        boolean isIdMatch = newProfile.getUserId().equals(currentUser.getUserId());
        boolean isPasswordMatch = currPassword.equals(currentUser.getPassword());
        boolean success = isIdMatch && isPasswordMatch;
        if (success) {
            userRepository.update(currentUser, newProfile);
        }
        return success;
    }
}
