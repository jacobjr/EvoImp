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
 * @author Written by Julián Luengo Martín 05/06/2006
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
import util.arff.InformacoesArff;
import java.util.*;
import util.keel.util.FreqList;

/**
 * <p>
 * This class computes the mean (numerical) or mode (nominal) value of the
 * attributes with missing values for the selected neighbours, weighting them
 * according to the relative distance to the considered instance with missing
 * values.
 * </p>
 */
public class WKNNImpute {

    FreqList[] timesSeen = null; //matrix with frequences of attribute values
    String[] mostCommon;

    int ndatos;
    int nvariables;
    int nneigh; //number of neighbours
    InformacoesArff input_arff;

    /**
     * Creates a new instance of MostCommonValue
     *
     * @param in input arff
     * @param k number of neighbours
     */
    public WKNNImpute(InformacoesArff in, int k) {
        this.input_arff = in;
        this.nneigh = k;
    }

    /**
     * <p>
     * Computes the distance between two instances (without previous
     * normalization)
     * </p>
     *
     * @param i First instance
     * @param j Second instance
     * @return The Euclidean distance between i and j
     */
    private double distance(String i, String j) {
        double dist = 0;

        String attributeValueI[] = i.split(",");
        String attributeValueJ[] = j.split(",");

        for (int l = 0; l < nvariables; l++) {
            String valueI = attributeValueI[l].trim();
            String valueJ = attributeValueJ[l].trim();

            String tipo = input_arff.getTipoAtributo().get(l).trim().toUpperCase();

            if (!valueI.equals("?") && !valueJ.equals("?")) {
                if (tipo.equals("NUMERIC") || tipo.equals("REAL") || tipo.equals("INTEGER")) {
                    //real value, apply euclidean distance
                    dist += (Double.parseDouble(valueI) - Double.parseDouble(valueJ)) * (Double.parseDouble(valueI) - Double.parseDouble(valueJ));
                } else {
                    if (!valueI.equals(valueJ)) {
                        dist += 1;
                    }
                }
            }
        }
        return Math.sqrt(dist);
    }

    /**
     * <p>
     * Checks if two instances present MVs for the same attributes
     * </p>
     *
     * @param inst1 the first instance
     * @param inst2 the second instance
     * @return true if both instances have missing values for the same
     * attributes, false otherwise
     */
    protected boolean sameMissingInputAttributes(String inst1, String inst2) {
        boolean sameMVs = true;

        String attributeValue1[] = inst1.split(",");
        String attributeValue2[] = inst2.split(",");
        for (int i = 0; i < nvariables && sameMVs; i++) {
            String value1 = attributeValue1[i].trim();
            String value2 = attributeValue2[i].trim();

            if (value1.equals("?") != value2.equals("?")) {
                sameMVs = false;
            }
        }

        return sameMVs;
    }

    /**
     * Finds the nearest neighbor with a valid value in the specified attribute
     *
     * @param inst the instance to be taken as reference
     * @param a the attribute which will be checked
     * @return the nearest instance that has a valid value in the attribute 'a'
     */
    protected String nearestValidNeighbor(String inst, int a) {
        double distance = Double.POSITIVE_INFINITY;
        String inst2;
        int nn = 0;

        for (int i = 0; i < ndatos; i++) {
            inst2 = input_arff.getData().get(i);
            String value2 = inst2.split(",")[a].trim();
            if (!inst.equals(inst2) && !value2.equals("?") && distance(inst, inst2) < distance) {
                distance = distance(inst, inst2);
                nn = i;
            }

        }

        return input_arff.getData().get(nn);
    }

    /**
     * <p>
     * Process the training and test files provided in the parameters file to
     * the constructor.
     * </p>
     */
    public ArrayList<String> process() {
        String neighbor;
        double dist, mean, totalDist;
        int actual;
        int[] N = new int[nneigh];
        double[] Ndist = new double[nneigh];
        boolean allNull;

        try {

            ndatos = input_arff.getData().size();
            nvariables = input_arff.getNomeAtributo().size();

            timesSeen = new FreqList[nvariables];
            mostCommon = new String[nvariables];

            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < ndatos; i++) {
                String inst = input_arff.getData().get(i);

                totalDist = 0.0;
                if (inst.contains("?")) {
                    //since exists MVs, first we must compute the nearest
                    //neighbours for our instance
                    for (int n = 0; n < nneigh; n++) {
                        Ndist[n] = Double.MAX_VALUE;
                        N[n] = -1;
                    }
                    for (int k = 0; k < ndatos; k++) {
                        neighbor = input_arff.getData().get(k);

                        if (!sameMissingInputAttributes(inst, neighbor)) {
                            dist = distance(inst, neighbor);

                            actual = -1;
                            for (int n = 0; n < nneigh; n++) {
                                if (dist < Ndist[n]) {
                                    if (actual != -1) {
                                        if (Ndist[n] > Ndist[actual]) {
                                            actual = n;
                                        }
                                    } else {
                                        actual = n;
                                    }
                                }
                            }
                            if (actual != -1) {
                                N[actual] = k;
                                Ndist[actual] = dist;
                            }
                        }
                    }
                    String line = "";
                    String attributeValues[] = inst.split(",");
                    for (int j = 0; j < nvariables; j++) {
                        String value = attributeValues[j].trim();
                        String tipo = input_arff.getTipoAtributo().get(j).trim().toUpperCase();

                        if (value.equals("?")) {

                            allNull = true;
                            timesSeen[j] = new FreqList();
                            if (tipo.equals("NUMERIC") || tipo.equals("REAL") || tipo.equals("INTEGER")) {
                                mean = 0.0;
                                totalDist = 0;
                                for (int m = 0; m < nneigh; m++) {
                                    if (N[m] != -1) {
                                        String inst2 = input_arff.getData().get(N[m]);
                                        String value2 = inst2.split(",")[j].trim();
                                        if (!value2.equals("?")) {
                                            totalDist += Ndist[m];
                                        }
                                    }
                                }
                                for (int m = 0; m < nneigh; m++) {
                                    if (N[m] != -1) {
                                        String inst2 = input_arff.getData().get(N[m]);
                                        String value2 = inst2.split(",")[j].trim();
                                        if (!value2.equals("?")) {
                                            mean += Double.parseDouble(value2) * (Ndist[m] / totalDist);
                                            allNull = false;
                                        }
                                    }
                                }
                                if (!allNull) {
                                    if (tipo.equals("INTEGER")) {
                                        mean = new Double(mean + 0.5).intValue();
                                    }
                                    value = String.valueOf(mean);
                                } else //if no option left, lets take the nearest neighbor with a valid attribute value
                                {
                                    value = nearestValidNeighbor(inst, j).split(",")[j].trim();
                                }
                            } else {
                                for (int m = 0; m < nneigh; m++) {
                                    String inst2 = input_arff.getData().get(N[m]);
                                    String value2 = inst2.split(",")[j].trim();

                                    if (N[m] != -1 && !value2.equals("?")) {
                                        timesSeen[j].AddElement(value2);
                                    }

                                }
                                if (timesSeen[j].totalElems() != 0) {
                                    value = timesSeen[j].mostCommon().getValue(); //replace missing data
                                } else {
                                    value = nearestValidNeighbor(inst, j).split(",")[j].trim();
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
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

}
