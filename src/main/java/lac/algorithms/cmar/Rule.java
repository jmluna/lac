/**
 * This file is part of Library for Associative Classification (LAC)
 *
 * Copyright (C) 2019
 *   
 * LAC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. You should have 
 * received a copy of the GNU General Public License along with 
 * this program.  If not, see http://www.gnu.org/licenses/
 */
package lac.algorithms.cmar;

/**
 * Class used to represent rules in CMAR algorithm
 */
public class Rule extends lac.algorithms.Rule {
    /**
     * Number of instances contained in mined dataset, it is used to calculate
     * chi-square value
     */
    public static double NUMBER_INSTANCES = 0;

    /**
     * Constructor
     * 
     * @param antecedent
     * @param klass
     */
    public Rule(short[] antecedent, short klass) {
        super(antecedent, klass);
    }

    /**
     * Set support for current antecedent
     * 
     * @param supportAntecedent
     */
    public void setSupportAntecedent(long supportAntecedent) {
        this.supportAntecedent = supportAntecedent;
    }

    /**
     * Set the support for current rule
     * 
     * @param supportRule
     */
    public void setSupportRule(long supportRule) {
        this.supportRule = supportRule;
    }

    /**
     * Set the value for the support of the consequent
     * 
     * @param supportConsequent
     */
    public void setSupportConsequent(long supportConsequent) {
        this.supportKlass = supportConsequent;
    }

    /**
     * Get chi square for current rule
     * 
     * @return chi square for current rule
     */
    public double getChiSquare() {
        double[] observedValues = new double[4];
        double[] expectedValues = new double[4];
        // Calculate observed and expected values
        observedValues[0] = this.supportRule;
        observedValues[1] = this.supportAntecedent - this.supportRule;
        observedValues[2] = this.supportKlass - this.supportRule;
        observedValues[3] = NUMBER_INSTANCES - this.supportAntecedent - this.supportKlass + this.supportRule;

        // Calculate additional support values
        double supNotAntecedent = NUMBER_INSTANCES - this.supportAntecedent;
        double supNotConsequent = NUMBER_INSTANCES - this.supportKlass;

        // Expected values
        expectedValues[0] = (this.supportKlass * this.supportAntecedent) / NUMBER_INSTANCES;
        expectedValues[1] = (supNotConsequent * this.supportAntecedent) / NUMBER_INSTANCES;
        expectedValues[2] = (this.supportKlass * supNotAntecedent) / NUMBER_INSTANCES;
        expectedValues[3] = (supNotConsequent * supNotAntecedent) / NUMBER_INSTANCES;

        double sumChiSquaredValues = 0.0;

        for (int index = 0; index < observedValues.length; index++) {
            double chiValue = Math.pow((observedValues[index] - expectedValues[index]), 2.0) / expectedValues[index];
            sumChiSquaredValues = sumChiSquaredValues + chiValue;
        }

        return sumChiSquaredValues;
    }

    /**
     * Check whether specified rule is more general than current rule
     * 
     * @param rule to be checked if is more general
     * @return true if current rule is more general, false otherwise
     */
    public boolean isMoreGeneral(Rule rule) {
        return antecedent.size() < rule.antecedent.size();
    }

    /**
     * Compares two rules to check which one is greater in function of confidence,
     * support, and size
     * 
     * @param other to be compared
     * @return true if current rule is greater than specified rule, false otherwise
     */
    public boolean isGreater(Rule other) {
        if (this.getConfidence() > other.getConfidence())
            return true;

        // If confidences are the same compare support values
        if (Double.compare(this.getConfidence(), other.getConfidence()) == 0) {
            if (this.getSupportRule() > other.getSupportRule())
                return true;

            if (Double.compare(this.getSupportRule(), other.getSupportRule()) == 0) {
                if (this.getAntecedent().size() < other.getAntecedent().size())
                    return true;
            }
        }

        return false;
    }

    /**
     * Calculates the upper bound for the chi-squared vlaue
     * 
     * @return the chi-squared upper bound
     */
    public double getChiSquareUpperBound() {
        double term;

        if (this.supportAntecedent < this.supportKlass)
            term = Math.pow(this.supportAntecedent - ((this.supportAntecedent * this.supportKlass) / NUMBER_INSTANCES),
                    2.0);

        else
            term = Math.pow(this.supportKlass - ((this.supportAntecedent * this.supportKlass) / NUMBER_INSTANCES), 2.0);

        double eVlaue = calculateChiSquare();

        // Upper bound will be:
        return term * eVlaue * NUMBER_INSTANCES;
    }

    /**
     * Calculate weighted chi squared value for current rule
     * 
     * @return the weighted chi-squared value
     */
    private double calculateChiSquare() {
        double term1 = 1.0 / (this.supportAntecedent * this.supportKlass);
        double term2 = 1.0 / (this.supportAntecedent * (NUMBER_INSTANCES - this.supportKlass));
        double term3 = 1.0 / (this.supportKlass * (NUMBER_INSTANCES - this.supportAntecedent));
        double term4 = 1.0 / ((NUMBER_INSTANCES - this.supportAntecedent) * (NUMBER_INSTANCES - this.supportKlass));

        return (term1 + term2 + term3 + term4);
    }
}
