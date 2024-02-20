package br.ufpa.linc.util.keel.algorithms;

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
import br.ufpa.linc.util.arff.InformacoesArff;
import java.util.*;
import br.ufpa.linc.util.keel.util.FreqList;
import br.ufpa.linc.util.keel.util.ValueFreq;

/**
 * <p>
 * This class computes the mean (numerical) or mode (nominal) value of the
 * attributes with missing values for each class
 * </p>
 */
public class ConceptMostCommonValue {

    FreqList[] timesSeen = null; //matrix with frequences of attribute values
    String[] mostCommon;

    int ndatos;
    int nvariables;

    InformacoesArff input_arff;

    /**
     * Creates a new instance of MostCommonValue
     *
     * @param in in put arff
     */
    public ConceptMostCommonValue(InformacoesArff in) {
        this.input_arff = in;
    }

    /**
     * <p>
     * Process the training and test files provided in the parameters file to
     * the constructor.
     * </p>
     */
    public ArrayList<String> process() {
        String outputs;
        String outputs2;
        ValueFreq vf;
        double mean;
        try {

             ndatos = input_arff.getData().size();
            nvariables = input_arff.getNomeAtributo().size();

            timesSeen = new FreqList[nvariables];
            mostCommon = new String[nvariables];

            //now, search for missed data, and replace them with
            //the most common value
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < ndatos; i++) {
                String inst = input_arff.getData().get(i);

                if (inst.contains("?")) {

                    String line = "";
                    String attributeValues[] = inst.split(",");
                    for (int j = 0; j < nvariables; j++) {
                        String value = attributeValues[j].trim();
                        String tipo = input_arff.getTipoAtributo().get(j).trim().toUpperCase();

                        if (value.equals("?")) {
                            outputs = attributeValues[nvariables - 1].trim();
                            timesSeen[j] = new FreqList();
                            for (int m = 0; m < ndatos; m++) {
                                String inst2 = input_arff.getData().get(m);
                                String value2 = inst2.split(",")[j].trim();
                                outputs2 = inst2.split(",")[nvariables - 1].trim();
                                boolean sameClass = outputs.equals(outputs2);
                                if (sameClass) {
                                    if (!value2.equals("?")) {
                                        timesSeen[j].AddElement(value2);
                                    }
                                }
                            }
                            if (timesSeen[j].numElems() == 0) {
                                for (int m = 0; m < ndatos; m++) {
                                    String inst2 = input_arff.getData().get(m);
                                    String value2 = inst2.split(",")[j].trim();
                                    if (!value2.equals("?")) {
                                        timesSeen[j].AddElement(value2);
                                    }
                                }
                            }
                            if (tipo.equals("NUMERIC") || tipo.equals("REAL") || tipo.equals("INTEGER")) {
                                timesSeen[j].reset();
                                mean = 0;
                                while (!timesSeen[j].outOfBounds()) {
                                    vf = timesSeen[j].getCurrent();
                                    mean += (Double.parseDouble(vf.getValue()) * vf.getFreq());
                                    timesSeen[j].iterate();
                                }
                                mean = mean / (double) timesSeen[j].totalElems();
                                value = String.valueOf(mean);
                            } else {
                                if (timesSeen[j].mostCommon() != null) {
                                    value = timesSeen[j].mostCommon().getValue(); //replace missing data
                                } else {
                                    value = "?";
                                }
                            }
                        }
                        line += "," + value;
                    }
                    data.add(line.substring(1));
                } else {
                    data.add(inst);
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
