package com.microtech.smartshop.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreateRequestDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
}
