package com.nothing.onsite.productmanagementzk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private String sender;
    private String content;
    private Date timestamp;
    
    @Override
    public String toString() {
        return sender + ": " + content;
    }
} 