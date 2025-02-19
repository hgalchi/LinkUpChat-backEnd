INSERT INTO users(id,name,email,password,is_deleted)
VALUES
    (1,'producer','producer@naver.com','password','f'),
    (2,'receiver','receiver@naver.com','password','f');

INSERT INTO chat_room (name,participant_count,capacity,is_deleted,chat_room_type)
VALUES
    ('TEST GROUP CHAT_ROOM',0,8,'f','GROUP'),
    ('TEST DM CHAT_ROOM',0,2,'f','DM')
