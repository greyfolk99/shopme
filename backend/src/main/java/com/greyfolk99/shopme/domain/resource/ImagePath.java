package com.greyfolk99.shopme.domain.resource;


import java.nio.file.Paths;

public enum ImagePath {
    ITEM("item"),
    ITEM_REVIEW("item/review"),
    PROFILE("profile"),
    BANNER("banner")
    ;
    final String path;
    ImagePath(String path) {this.path = path;}
    public String getPathString() {
        return Paths.get("image")
                .resolve(Paths.get(this.path)).toString();
    }
}
