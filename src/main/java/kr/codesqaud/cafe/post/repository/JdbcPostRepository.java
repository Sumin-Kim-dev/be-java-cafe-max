package kr.codesqaud.cafe.post.repository;

import kr.codesqaud.cafe.post.service.Post;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Post save(Post post) {
        String sql = "insert into post (writer_id, writer_name, title, contents, writing_time) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, post.getWriterId());
            ps.setString(2, post.getWriterName());
            ps.setString(3, post.getTitle());
            ps.setString(4, post.getContents());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(post.getWritingTime()));
            return ps;
        }, keyHolder);

        long key = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return post.create(key);
    }

    @Override
    public Post update(long id, Post post) {
        String sql = "update `post` set writer_name = ?, title = ?, contents = ? where id = ?";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, post.getWriterName());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getContents());
            ps.setLong(4, id);
            return ps;
        });
        return post.create(id);
    }

    @Override
    public void delete(long id) {
        String sql = "update `post` set deleted = true where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Post> findById(long id) {
        String sql = "select id, writer_id, writer_name, title, contents, writing_time, deleted from post where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper(), id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Post> findAll() {
        String sql = "select id, writer_id, writer_name, title, contents, writing_time, deleted from post " +
                "where deleted = false order by writing_time desc";
        return jdbcTemplate.query(sql, rowMapper());
    }

    private RowMapper<Post> rowMapper() {
        return (rs, rowNum) -> new Post(
                rs.getLong("id"),
                rs.getString("writer_id"),
                rs.getString("writer_name"),
                rs.getString("title"),
                rs.getString("contents"),
                rs.getTimestamp("writing_time").toLocalDateTime(),
                rs.getBoolean("deleted")
        );
    }
}
