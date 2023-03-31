package kr.codesqaud.cafe.service;

import kr.codesqaud.cafe.domain.Post;
import kr.codesqaud.cafe.dto.PostListResponse;
import kr.codesqaud.cafe.dto.PostWriteRequest;
import kr.codesqaud.cafe.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void writePost(PostWriteRequest postWriteRequest) {
        postRepository.save(postWriteRequest);
    }

    public void showPostList(Model model) {
        List<PostListResponse> posts = postRepository.findAll().stream()
                .map(Post::toListResponse)
                .collect(Collectors.toList());
        model.addAttribute("posts", posts);
    }
}
