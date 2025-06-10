package com.ims.dto;

import lombok.Data;
import java.io.Serializable;


@Data
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String to;
    private String subject;
    private String body;
}
