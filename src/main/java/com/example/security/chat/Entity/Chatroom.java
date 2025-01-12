package com.example.security.chat.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long owner;

    @Column(length = 10)
    private String name;

    private int count;

    private int maxCount;

    @OneToMany(mappedBy = "chatRoom",fetch = FetchType.LAZY)
    @Builder.Default
    private List<Chatmessage> chatMessage = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserChatroom> Users = new HashSet<>();


    public void addDetail(UserChatroom userChatroom) {
        Users.add(userChatroom);
    }
    public void removeDetail(UserChatroom userChatroom) {
        Users.remove(userChatroom);
    }

    public void setUserCount(int userCount) {
        this.count = userCount;
    }

}
