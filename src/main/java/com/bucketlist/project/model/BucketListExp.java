package com.bucketlist.project.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bucket_list_exps")
public class BucketListExp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bucketListExpId;

    @ManyToOne
    @JoinColumn(name = "bucket_list_id")
    private BucketList bucketList;

    @ManyToOne
    @JoinColumn(name = "experience_id")
    private Experience experience;

    @Column(nullable = false)
    private LocalDate dateSaved;

    @Column(nullable = false)
    private boolean completed;
}
