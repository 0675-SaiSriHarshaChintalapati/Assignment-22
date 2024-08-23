package com.example.springJDBC.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
public class User {
    private String first_name;
    private String last_name;
    private int id;

}
