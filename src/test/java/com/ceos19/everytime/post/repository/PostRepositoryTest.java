package com.ceos19.everytime.post.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ceos19.everytime.board.domain.Board;
import com.ceos19.everytime.board.repository.BoardRepository;
import com.ceos19.everytime.post.domain.Post;
import com.ceos19.everytime.helper.RepositoryTest;
import com.ceos19.everytime.user.domain.User;
import com.ceos19.everytime.user.repository.UserRepository;

@RepositoryTest
class PostRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardRepository boardRepository;

    private User user;
    private Board board;
    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("test")
                .password("qwe123!")
                .nickname("nick")
                .build();
        board = Board.builder()
                .name("board")
                .build();
        post1 = Post.builder()
                .title("title1")
                .writer(user)
                .board(board)
                .content("content1")
                .build();
        post2 = Post.builder()
                .title("title2")
                .writer(user)
                .board(board)
                .content("content2")
                .build();
        post3 = Post.builder()
                .title("title3")
                .writer(user)
                .board(board)
                .content("content3")
                .build();
        boardRepository.save(board);
        userRepository.save(user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
    }

    @Test
    void 게시글_조회_테스트() {
        // given & when
        final Post post1 = postRepository.findById(this.post1.getId()).get();
        final Post post2 = postRepository.findById(this.post2.getId()).get();
        final Post post3 = postRepository.findById(this.post3.getId()).get();

        // then
        assertSoftly(softly -> {
            softly.assertThat(post1).isEqualTo(this.post1);
            softly.assertThat(post2).isEqualTo(this.post2);
            softly.assertThat(post3).isEqualTo(this.post3);
        });
    }

    @Test
    void 게시판에_해당하는_모든_게시글을_조회한다() {
        // given & when
        final List<Post> posts = postRepository.findAllFetchJoin(board);

        // then
        assertSoftly(softly -> {
            softly.assertThat(posts.size()).isEqualTo(3);
            softly.assertThat(posts.get(0)).isEqualTo(post1);
            softly.assertThat(posts.get(1)).isEqualTo(post2);
            softly.assertThat(posts.get(2)).isEqualTo(post3);
        });
    }
}