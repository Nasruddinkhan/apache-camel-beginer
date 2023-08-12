package com.mypractice.routes.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mypractice.routes.anotations.CsvField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Products {
    @JsonProperty("Id")
    @CsvField(name = "Id")
   // @CsvField("Id")
    private String id;
    @JsonProperty("Maker")
    @CsvField(name = "Maker")
    private String maker;
    @JsonProperty("img")
    @CsvField(name = "img")
    private String img;
    @JsonProperty("Url")
    @CsvField(name = "Url")
    private String url;
    @JsonProperty("Title")
    @CsvField(name = "Title")
    private String title;
    @JsonProperty("Description")
    @CsvField(name = "Description")
    private String description;
    @JsonProperty("Ratings")
    @CsvField(name = "Ratings")
    private List<Integer> ratings;
}
