package com.jsrm.core.pressure.function;

import com.jsrm.core.UnregisteredPropellantException;
import com.jsrm.motor.propellant.SolidPropellant;
import net.objecthunter.exp4j.function.Function;

import static com.jsrm.core.RegisteredPropellant.getSolidPropellant;
import static java.lang.Math.pow;

public class BurnRateCharacteristicFunction extends Function{

    public BurnRateCharacteristicFunction() {
        super("BurnRateCharacteristic", 2);
    }

    @Override
    public double apply(double... doubles) {
        Double propellantId = doubles[0];
        double chamberPressure = doubles[1];

        try {
            SolidPropellant propellant = getSolidPropellant(propellantId.intValue());
            return propellant.getBurnRateCoefficient(chamberPressure) * pow(chamberPressure, propellant.getPressureExponent(chamberPressure));
        } catch (UnregisteredPropellantException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
