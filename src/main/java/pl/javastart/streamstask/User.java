package pl.javastart.streamstask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode
public class User {

    private Long id;
    private String name;
    private int age;

}
