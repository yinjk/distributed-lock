package com.intellif.lock;


/**
 * @author inori
 * @create 2018-11-22 20:10
 */
public abstract class AbstractIfLock implements IfLock{

    public AbstractIfLock(String name) {
        this.name = name;
    }

    /** 锁name **/
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}