package com.atguigu.gmall.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class BaseSaleAttr implements Serializable {
    @Id
    @Column
    String id ;

    @Column
    String name;
}

