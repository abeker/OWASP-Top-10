package com.owasp.adservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Photo extends BaseEntity {

    private String name;

    private String type;

    @Column(name = "pic_byte", length = 1000)
    private byte[] picByte;

    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Ad ad;
}
