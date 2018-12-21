package com.jsrm.core.pressure;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.Calculator;
import com.jsrm.calculation.Formula;
import com.jsrm.core.pressure.csv.CsvToPressureLine;
import com.jsrm.infra.Extract;
import com.jsrm.motor.MotorChamber;
import com.jsrm.motor.PropellantGrain;
import com.jsrm.motor.SolidRocketMotor;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.core.pressure.PressureFormulas.*;
import static com.jsrm.core.pressure.csv.PressureCsvLineAggregator.INTERVAL;
import static com.jsrm.motor.GrainSurface.EXPOSED;
import static com.jsrm.motor.GrainSurface.INHIBITED;
import static com.jsrm.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class QualificationPressureCalculations {

    private static List<Map<Formula, Double>> results;

    Map<Formula, Offset> precisionByFormulas = ImmutableMap.<Formula, Offset>builder()
            .put(GRAIN_CORE_DIAMETER, offset(0.01))
            .put(GRAIN_OUTSIDE_DIAMETER, offset(0.01))
            .put(GRAIN_LENGTH, offset(0.1))
            .put(WEB_THICKNESS, offset(0.001))
            .put(THROAT_AREA, offset(0.1))
            .put(NOZZLE_CRITICAL_PASSAGE_AREA, offset(0.00000001))
            .put(EROSIVE_BURN_FACTOR, offset(0.01))
            .put(GRAIN_VOLUME, offset(1d))
            .put(TEMPORARY_CHAMBER_PRESSURE, offset(0.001))
            .put(PROPELLANT_BURN_RATE, offset(0.001))
            .put(TIME_SINCE_BURN_STARTS, offset(0.0001))
            .put(AI, offset(0.0001))
            .put(MASS_GENERATION_RATE, offset(0.0001))
            .put(NOZZLE_MASS_FLOW_RATE, offset(0.0001))
            .put(MASS_STORAGE_RATE, offset(0.0001))
            .put(MASS_COMBUSTION_PRODUCTS, offset(0.0001))
            .put(DENSITY_COMBUSTION_PRODUCTS, offset(0.001))
            .put(CHAMBER_PRESSURE_MPA, offset(0.001))
            .put(ABSOLUTE_CHAMBER_PRESSURE, offset(0.001))
            .put(ABSOLUTE_CHAMBER_PRESSURE_PSIG, offset(0.1))
            .build();

    @BeforeAll
    static void init(){
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, 20, 1d,
                                                 60d, 4d,
                                                              INHIBITED, EXPOSED, EXPOSED);
        MotorChamber motorChamber = new MotorChamber(75d, 470d);

        double throatDiameter = 17.3985248919802;

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, motorChamber,
                                                 6d, throatDiameter, 0d);

        Map<String, Double> constants = Extract.extractConstants(solidRocketMotor);

        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(GRAIN_CORE_DIAMETER, 20d);
        initialValues.put(GRAIN_OUTSIDE_DIAMETER, 69d);
        initialValues.put(GRAIN_LENGTH, 460d);

        //TODO
        initialValues.put(TIME_SINCE_BURN_STARTS, 0d);
        initialValues.put(TEMPORARY_CHAMBER_PRESSURE, 0.101);//patm?
        initialValues.put(MASS_GENERATION_RATE, 0d);
        initialValues.put(NOZZLE_MASS_FLOW_RATE, 0d);
        initialValues.put(MASS_STORAGE_RATE, 0d);
        initialValues.put(MASS_COMBUSTION_PRODUCTS, 0d);
        initialValues.put(DENSITY_COMBUSTION_PRODUCTS, 0d);

        Calculator calculator = new Calculator(ABSOLUTE_CHAMBER_PRESSURE_PSIG, constants, initialValues);
        results = calculator.compute(0, 835);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_QUALIFICATION.csv", numLinesToSkip = 1, delimiter = '|')
    @DisplayName("Check pressure with SRM results")
    void qualification1(@CsvToPressureLine Map<String, Double> expectedLine) {
        Map<Formula, Double> resultLIneToAssert = results.get(expectedLine.get(INTERVAL).intValue());

        precisionByFormulas.forEach((formula, offset) -> {
            assertThat(resultLIneToAssert.get(formula)).as(formula.getName())
                    .isEqualTo(expectedLine.getOrDefault(formula.getName(),-111d), offset);
        });


//        assertThat(resultLIneToAssert.get(GRAIN_CORE_DIAMETER))
//                .isEqualTo(expectedLine.get(GRAIN_CORE_DIAMETER.getName()), Offset.offset(0.01));
//
//        assertThat(resultLIneToAssert.get(GRAIN_OUTSIDE_DIAMETER))
//                .isEqualTo(expectedLine.get(GRAIN_OUTSIDE_DIAMETER.getName()), Offset.offset(0.01));
//
//        assertThat(resultLIneToAssert.get(GRAIN_LENGTH))
//                .isEqualTo(expectedLine.get(GRAIN_LENGTH.getName()), Offset.offset(0.1));
//
//        assertThat(resultLIneToAssert.get(WEB_THICKNESS))
//                .isEqualTo(expectedLine.get(WEB_THICKNESS.getName()), Offset.offset(0.001));
//
//        assertThat(resultLIneToAssert.get(THROAT_AREA))
//                .isEqualTo(expectedLine.get(THROAT_AREA.getName()), Offset.offset(0.1));
//
//        assertThat(resultLIneToAssert.get(NOZZLE_CRITICAL_PASSAGE_AREA))
//                .isEqualTo(expectedLine.get(NOZZLE_CRITICAL_PASSAGE_AREA.getName()), Offset.offset(0.00000001));

//        assertThat(resultLIneToAssert.get(EROSIVE_BURN_FACTOR))
//                .isEqualTo(expectedLine.get(EROSIVE_BURN_FACTOR.getName()), Offset.offset(0.01d));

//        assertThat(resultLIneToAssert.get(GRAIN_VOLUME))
//                .isEqualTo(expectedLine.get(GRAIN_VOLUME.getName()), Offset.offset(1d));
//
//        assertThat(resultLIneToAssert.get(ABSOLUTE_CHAMBER_PRESSURE_PSIG))
//                .isEqualTo(expectedLine.get(ABSOLUTE_CHAMBER_PRESSURE_PSIG.name()), Offset.offset(0.1));
    }
}