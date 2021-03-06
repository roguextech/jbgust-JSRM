package com.github.jbgust.jsrm.application.motor;

import com.github.jbgust.jsrm.application.motor.grain.HollowCylinderGrain;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;

class PropellantGrainTest {

    @Test
    void shouldBuildPropellantGrain() {
        // GIVEN
        double outerDiameter = 20;
        double coreDiameter = 6;
        double segmentLength = 60;
        int numberOfSegment = 2;
        HollowCylinderGrain grainConfigutation = new HollowCylinderGrain(outerDiameter, coreDiameter, segmentLength, numberOfSegment,
                INHIBITED, EXPOSED, INHIBITED);

        // WHEN
        PropellantGrain propellantGrain = new PropellantGrain(KNDX,
                grainConfigutation);

        // THEN
        assertThat(propellantGrain.getPropellant()).isEqualTo(KNDX);
        assertThat(propellantGrain.getGrainConfigutation()).isEqualTo(grainConfigutation);
    }

}
