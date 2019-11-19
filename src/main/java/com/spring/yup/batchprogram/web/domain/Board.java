package com.spring.yup.batchprogram.web.domain;

import com.spring.yup.batchprogram.web.domain.enums.BoardType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table
public class Board implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키가 자동적으로 할당되도록 설정하는 어노테이션.. 키 생성을 데이터베이스에 위임하는 IDENTITY전략
    private Long idx;

    @Column
    private String title;

    @Column
    private String subTitle;

    @Column
    private String content;

    @Column
    @Enumerated(EnumType.STRING)    // Enum타입 매핑용 어노테이션.
    private BoardType boardType;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    // 도메인 Board와 Board가 필드값으로 갖고 있는 User 도메인을 1:1 관계로 설정하는 어노테이션.
    // 실제로 DB에 저장될 때는 User 객체가 저장되는 것이 아니라 User 의 PK인 user_idx 값이 저장됨..
    // FetchType.eager는 처음 Board도메인을 조회할 때, 즉시 User객체를 함께 조회,
    // FetchType.lazy User객체를 조회하는 시점이 아니라 객체가 실제 사용될 때 조회..
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public Board(Long idx, String title, String subTitle, String content, BoardType boardType
            , LocalDateTime createdDate, LocalDateTime updatedDate, User user) {
        this.idx = idx;
        this.title = title;
        this.subTitle = subTitle;
        this.content = content;
        this.boardType = boardType;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.user = user;
    }
}
