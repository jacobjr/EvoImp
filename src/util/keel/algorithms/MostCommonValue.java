package util.keel.algorithms;

/**
 * *********************************************************************
 *
 * This file is part of KEEL-software, the Data Mining tool for regression,
 * classification, clustering, pattern mining and so on.
 *
 * Copyright (C) 2004-2010
 *
 * F. Herrera (herrera@decsai.ugr.es) L. Sánchez (luciano@uniovi.es) J.
 * Alcalá-Fdez (jalcala@decsai.ugr.es) S. García (sglopez@ujaen.es) A. Fernández
 * (alberto.fernandez@ujaen.es) J. Luengo (julianlm@decsai.ugr.es)
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/
 *
 *********************************************************************
 */
/**
 * <p>
 * @author Written by Julián Luengo Martín 31/12/2005
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
import util.arff.InformacoesArff;
import java.util.ArrayList;
import util.keel.util.FreqList;
import util.keel.util.ValueFreq;

/**
 * <p>
 * This class computes the mean (numerical) or mode (nominal) value of the
 * attributes with missing values for all classes
 * </p>
 */
public class MostCommonValue {

    String[][] X = null; //matrix of transformed data
    FreqList[] timesSeen = null; //matrix with frequences of attribute values
    String[] mostCommon;

    int ndatos = 0;
    int nvariables = 0;

    InformacoesArff input_arff;

    /**
     * Creates a new instance of MostCommonValue
     *
     * @param in input arff
     */
    public MostCommonValue(InformacoesArff in) {
        this.input_arff = in;
    }

    /**
     * <p>
     * Takes a value and checks if it belongs to the attribute interval. If not,
     * it returns the nearest limit. IT DOES NOT CHECK IF THE ATTRIBUTE IS NOT
     * NOMINAL
     * </p>
     *
     * @param value the value to be checked
     * @param a the attribute to which the value will be checked against
     * @return the original value if it was in the interval limits of the
     * attribute, or the nearest boundary limit otherwise.
     */
    /**
     * <p>
     * Process the training and test files provided in the parameters file to
     * the constructor.
     * </p>
     */
    public ArrayList<String> process() {
        ValueFreq vf;
        double mean;
        try {

            ndatos = input_arff.getData().size();
            nvariables = input_arff.getNomeAtributo().size();

            timesSeen = new FreqList[nvariables];
            mostCommon = new String[nvariables];
            for (int j = 0; j < nvariables; j++) {
                timesSeen[j] = new FreqList();
            }

            //First, create a reference list with all values
            //for each attribute, so we can pick the most common one
            for (int i = 0; i < ndatos; i++) {
                String inst = input_arff.getData().get(i);
                String attributeValue[] = inst.split(",");
                for (int j = 0; j < nvariables; j++) {
                    String value = attributeValue[j].trim();
                    String tipo = input_arff.getTipoAtributo().get(j).trim().toUpperCase();

                    if (!value.equals("?")) {
                        timesSeen[j].AddElement(value);
                    }
                }
            }

            //take for each attribute the most common value, so it
            //can be taken quickly
            ValueFreq elem = null;
            for (int k = 0; k < nvariables; k++) {
                elem = timesSeen[k].mostCommon();
                if (elem != null) {
                    mostCommon[k] = elem.getValue();
                } else {
                    mostCommon[k] = "?"; //this attribute has no good values (all are missing data)
                }
            }
            //now, search for missed data, and replace them with
            //the most common value
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < ndatos; i++) {
                String inst = input_arff.getData().get(i);

                if (!inst.contains("?")) {
                    data.add(inst);
                } else {
                    String line = "";
                    String attributeValue[] = inst.split(",");
                    for (int j = 0; j < nvariables; j++) {
                        String value = attributeValue[j].trim();
                        if (value.equals("?")) {
                            String tipo = input_arff.getTipoAtributo().get(j).trim().toUpperCase();

                            if (!input_arff.getPossiveisClasses().contains(j)) {
                                value = mostCommon[j]; //replace missing data
                            } else {
                                if (input_arff.getPossiveisClasses().contains(j)) {
                                    if (tipo.equals("NUMERIC") || tipo.equals("REAL")) {
                                        timesSeen[j].reset();
                                        mean = 0;
                                        while (!timesSeen[j].outOfBounds()) {
                                            vf = timesSeen[j].getCurrent();
                                            mean += (Double.parseDouble(vf.getValue()) * vf.getFreq());
                                        }
                                        mean = mean / (double) timesSeen[j].totalElems();
                                        value = String.valueOf(mean);

                                    } else if (tipo.equals("INTEGER")) {
                                        timesSeen[j].reset();
                                        mean = 0;
                                        while (!timesSeen[j].outOfBounds()) {
                                            vf = timesSeen[j].getCurrent();
                                            mean += (Double.parseDouble(vf.getValue()) * vf.getFreq());
                                        }
                                        mean = mean / (double) timesSeen[j].totalElems();
                                        value = String.valueOf(((int) mean));
                                    } else {
                                        value = mostCommon[j]; //replace missing data
                                    }
                                }
                            }
                        }
                        line += "," + value;
                    }
                    data.add(line.substring(1));
                }
            }
            return data;
        } catch (Exception e) {
            System.out.println("Dataset exception = " + e);
            System.exit(-1);
        }
        return null;
    }

}
