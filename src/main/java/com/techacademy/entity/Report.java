package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="reports")
@SQLRestriction("delete_flg = false")
public class Report {

    //日報ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    //日付
    @Column(nullable = false)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    //タイトル
    @Column(length = 100, nullable = false)
    @NotEmpty
    @Length(max = 100)
    private String title;

    //内容
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    @NotEmpty
    @Length(max = 600)
    private String content;

    //従業員コード
    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code" , nullable = false)
    private Employee employee;

    //削除フラグ
    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg;

    //登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    //更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
