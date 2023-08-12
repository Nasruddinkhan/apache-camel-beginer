package com.mypractice;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Products {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Maker")
    private String maker;
    @JsonProperty("img")
    private String img;
    @JsonProperty("Url")
    private String url;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Ratings")
    private List<Integer> ratings;
}
