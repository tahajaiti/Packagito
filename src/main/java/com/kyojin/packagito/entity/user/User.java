package com.kyojin.packagito.entity.user;


import com.kyojin.packagito.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {

    @Id
    private String id;

    private String username;

    private String password;

    private Role role;

    private boolean active;
}
