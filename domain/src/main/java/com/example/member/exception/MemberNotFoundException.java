package com.example.member.exception;

public class MemberNotFoundException extends RuntimeException{

    public MemberNotFoundException() {
        super("사용자 없음: ");
    }
}
