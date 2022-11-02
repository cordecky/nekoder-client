package eu.kordecki.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Header {
    int fileSize;
    int headerEnd;
}
