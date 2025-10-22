package models;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class DeleteBookRequest {
    private String isbn;
    private String userId;
}