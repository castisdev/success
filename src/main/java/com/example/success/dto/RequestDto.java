package com.example.success.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private String id;
    private String name;
    private String code;


    @Override
    public String toString() {
        return "RequestDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", encodeType='" + code + '\'' +
                '}';
    }
}
