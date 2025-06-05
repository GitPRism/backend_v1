-- V18__add_invite_sent_to_notification_enum.sql
-- 알림 타입 ENUM에 'INVITE_SENT' 추가

ALTER TABLE notifications
    MODIFY COLUMN type ENUM(
        'COMMENT_CREATED',
        'LIKE_ADDED',
        'BOOKMARK_ADDED',
        'INVITE_SENT'
        ) NOT NULL COMMENT '알림 타입';
