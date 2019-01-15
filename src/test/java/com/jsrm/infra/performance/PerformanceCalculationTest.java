package com.jsrm.infra.performance;

import com.google.common.collect.ImmutableMap;
import com.jsrm.application.JSRMConfig;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.infra.ConstantsExtractor;
import com.jsrm.infra.JSRMConstant;
import com.jsrm.infra.performance.csv.CsvToPerformanceLine;
import com.jsrm.infra.pressure.ChamberPressureCalculation;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;
import java.util.Map;

import static com.jsrm.application.JSRMSimulationIT.createMotorAsSRM_2014ExcelFile;
import static com.jsrm.infra.JSRMConstant.atfinal;
import static com.jsrm.infra.performance.PerformanceCalculation.Results.deliveredImpulse;
import static com.jsrm.infra.performance.PerformanceCalculation.Results.thrust;
import static com.jsrm.infra.performance.PerformanceFormulas.DELIVERED_IMPULSE;
import static com.jsrm.infra.performance.PerformanceFormulas.THRUST;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class PerformanceCalculationTest {

    private Map<PerformanceCalculation.Results, Offset<Double>> precisionByResults = ImmutableMap.<PerformanceCalculation.Results, Offset<Double>>builder()
            .put(thrust, offset(1.0))
            .put(deliveredImpulse, offset(0.001))
            .build();

    private Map<PerformanceCalculation.Results, String> formulasByResult = ImmutableMap.of(
            thrust, THRUST.getName(),
            deliveredImpulse, DELIVERED_IMPULSE.getName()
    );

    private static Map<PerformanceCalculation.Results, List<Double>> performanceResults;
    private static int lineNumber = 0;
    private static PerformanceResultProvider chamberPressureProvider;
    private static PerformanceResultProvider throatAreaProvider;
    private static PerformanceResultProvider nozzleCriticalPassageAreaProvider;
    private static PerformanceResultProvider timeSinceBurnStartProvider;

    @BeforeAll
    static void init(){
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();
        fillResultProviders(solidRocketMotor);

        Map<JSRMConstant, Double> constants = ImmutableMap.<JSRMConstant, Double>builder()
                .putAll(ConstantsExtractor.extract(solidRocketMotor, new JSRMConfig.Builder().createJSRMConfig()))
                .put(atfinal, 237.74683)
                .build();

        performanceResults = new PerformanceCalculation(solidRocketMotor, constants,
                chamberPressureProvider, throatAreaProvider,
                nozzleCriticalPassageAreaProvider, timeSinceBurnStartProvider)
                .compute(new JSRMConfig.Builder().withNozzleExpansionRatio(8).createJSRMConfig())
                .getResults();

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_PERFORMANCE_QUALIFICATION.csv", numLinesToSkip = 2, delimiter = '|')
    @DisplayName("Check performance with SRM results")
    void qualification(@CsvToPerformanceLine Map<String, Double> performanceLine) {

        precisionByResults.forEach((result, offset) ->
                assertThat(performanceResults.get(result).get(lineNumber))
                        .as(result.name())
                        .isEqualTo(performanceLine.getOrDefault(formulasByResult.get(result), -111.0), offset));
        lineNumber++;
    }

    private static void fillResultProviders(SolidRocketMotor solidRocketMotor) {
        JSRMConfig config = new JSRMConfig.Builder().createJSRMConfig();
        Map<JSRMConstant, Double> constantsChamberPressure = ConstantsExtractor.extract(solidRocketMotor, config);

        Map<Results, List<Double>> chamberPressureResults = new ChamberPressureCalculation(solidRocketMotor, config, constantsChamberPressure).compute();

        chamberPressureProvider = new PerformanceResultProvider(chamberPressureMPA, chamberPressureResults.get(chamberPressureMPA));
        throatAreaProvider = new PerformanceResultProvider(throatArea, chamberPressureResults.get(throatArea));
        nozzleCriticalPassageAreaProvider = new PerformanceResultProvider(nozzleCriticalPassageArea, chamberPressureResults.get(nozzleCriticalPassageArea));
        timeSinceBurnStartProvider = new PerformanceResultProvider(timeSinceBurnStart, chamberPressureResults.get(timeSinceBurnStart));
    }
}