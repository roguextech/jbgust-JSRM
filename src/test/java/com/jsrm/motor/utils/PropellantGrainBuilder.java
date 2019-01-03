package com.jsrm.motor.utils;

import com.jsrm.motor.GrainSurface;
import com.jsrm.motor.PropellantGrain;
import com.jsrm.motor.propellant.SolidPropellant;

import static com.jsrm.motor.GrainSurface.EXPOSED;
import static com.jsrm.motor.GrainSurface.INHIBITED;
import static com.jsrm.motor.propellant.PropellantType.KNDX;

public class PropellantGrainBuilder {

    private int coreDiameter = 10;
    private int segmentLength = 50;
    private int numberOfSegments = 1;
    private GrainSurface outerSurface = INHIBITED;
    private GrainSurface endsSurface = INHIBITED;
    private GrainSurface coreSurface = EXPOSED;
    private SolidPropellant propellantType = KNDX;
    private int outerDiameter = 20;

    public PropellantGrain build() {
        return new PropellantGrain(propellantType, outerDiameter, coreDiameter, segmentLength, numberOfSegments, outerSurface, endsSurface, coreSurface);
    }

    public PropellantGrainBuilder withCoreSurface(GrainSurface surface){
        coreSurface = surface;
        return this;
    }

    public PropellantGrainBuilder withOuterSurface(GrainSurface surface) {
        outerSurface = surface;
        return this;
    }

    public PropellantGrainBuilder withEndsSurface(GrainSurface surface) {
        endsSurface = surface;
        return this;
    }

    public PropellantGrainBuilder withNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
        return this;
    }
}
