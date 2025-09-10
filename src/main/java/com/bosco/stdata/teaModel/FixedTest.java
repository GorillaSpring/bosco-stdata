package com.bosco.stdata.teaModel;

import java.io.Serializable;

public class FixedTest implements Serializable{

    private String one;
    private String two;
    private String three;
    private String four;
    public String getOne() {
        return one;
    }
    public void setOne(String one) {
        this.one = one;
    }
    public String getTwo() {
        return two;
    }
    public void setTwo(String two) {
        this.two = two;
    }
    public String getThree() {
        return three;
    }
    public void setThree(String three) {
        this.three = three;
    }
    public String getFour() {
        return four;
    }
    public void setFour(String four) {
        this.four = four;
    }
    @Override
    public String toString() {
        return "FixedTest [one=" + one + ", two=" + two + ", three=" + three + ", four=" + four + "]";
    }

    

}
