package com.catchthing.catchthing.calculate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Point {

    private int x;
    private int y;

    public double getDistance(Point p) {
        return Math.sqrt(
                Math.pow((p.getX() - this.getX()), 2.) +
                        Math.pow((p.getY() - this.getY()), 2.));
    }

}
