package com.bosco.stdata.teaModel;

import java.io.Serializable;

import lombok.Data;


@Data
public class FixedTest {

    private String one;
    private String two;
    private String three;
    private String four;

    // this is OK.
    private String nothing;

    

}
