package com.example.security.chat.repository;

import com.example.security.chat.Entity.Chatroom;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Chatroom c " +
            "SET c.maxCount = :maxCount, " +
            "c.name = :name, " +
            "c.owner = :owner " +
            "WHERE c.id = :id")
    void updateChatroom(@Param("id") Long id,@Param("owner")Long owner, @Param("name")String name,@Param("maxCount")int maxCount );

    Page<Chatroom> findAll(Pageable pageable);
    Page<Chatroom> findAllByNameContaining(String word, Pageable pageable);
}
