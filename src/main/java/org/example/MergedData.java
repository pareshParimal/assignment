package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MergedData {
    private String id;
    private Location location;
    private Metadata metadata;

    public MergedData(String id, Location location, Metadata metadata) {
        this.id = id;
        this.location = location;
        this.metadata = metadata;
    }

    public boolean isComplete() { return location != null && metadata != null; }
}