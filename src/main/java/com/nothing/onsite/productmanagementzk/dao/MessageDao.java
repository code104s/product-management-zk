package com.nothing.onsite.productmanagementzk.dao;

import com.nothing.onsite.productmanagementzk.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public class MessageDao {

    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public MessageDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<Message> findAll() {
        String sql = "SELECT * FROM messages ORDER BY timestamp";
        return jdbcTemplate.query(sql, new MessageRowMapper());
    }
    
    public List<Message> findRecentMessages(int limit) {
        String sql = "SELECT * FROM messages ORDER BY timestamp DESC LIMIT ?";
        return jdbcTemplate.query(sql, new MessageRowMapper(), limit);
    }
    
    public Message findById(Long id) {
        String sql = "SELECT * FROM messages WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new MessageRowMapper(), id);
    }
    
    public void save(Message message) {
        if (message.getId() == null) {
            // Tạo mới
            String sql = "INSERT INTO messages (sender, content, timestamp) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, 
                message.getSender(), 
                message.getContent(), 
                new Timestamp(message.getTimestamp().getTime())
            );
            
            // Lấy ID mới nhất
            Long newId = jdbcTemplate.queryForObject("SELECT max(id) FROM messages", Long.class);
            message.setId(newId);
        } else {
            // Cập nhật
            String sql = "UPDATE messages SET sender = ?, content = ?, timestamp = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                message.getSender(), 
                message.getContent(), 
                new Timestamp(message.getTimestamp().getTime()),
                message.getId()
            );
        }
    }
    
    public void delete(Message message) {
        String sql = "DELETE FROM messages WHERE id = ?";
        jdbcTemplate.update(sql, message.getId());
    }
    
    public long count() {
        String sql = "SELECT count(*) FROM messages";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    private static class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
            Message message = new Message();
            message.setId(rs.getLong("id"));
            message.setSender(rs.getString("sender"));
            message.setContent(rs.getString("content"));
            message.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));
            return message;
        }
    }
} 