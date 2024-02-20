package util.keel.util;

import util.arff.InformacoesArff;

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
 * @author Written by Julián Luengo Martín 28/11/2006
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
/**
 * <p>
 * This class represents a group of centers (centroids) of a set of clusters
 * </p>
 */
public class gCenter {

    String[][] gravCenters;

    int numCenters;

    int[] centerOf;

    int ndatos;

    int nvariables;

    InformacoesArff input_arff;

    /**
     * Creates a new instance of gCenter
     */
    public gCenter() {
        gravCenters = null;
        centerOf = null;
        numCenters = 0;
        ndatos = 0;
    }

    /**
     * <p>
     * Creates a new instance of gCenter with provided number of centers, number
     * of instances of the data set and number of attributes
     * </p>
     *
     * @param k Fixed number of centroids
     * @param in input arff
     */
    public gCenter(int k, InformacoesArff in) {
        this.ndatos = in.getData().size();
        this.nvariables = in.getNomeAtributo().size();
        gravCenters = new String[k][nvariables];
        numCenters = k;
        centerOf = new int[ndatos];
        this.input_arff = in;
    }

    /**
     * <p>
     * Computes the distance between a instances (without previous
     * normalization) and one clusters (i.e. its centroid).
     * </p>
     *
     * @param i The reference instance
     * @param k The cluster number
     * @return The Euclidean distance between i and k
     */
    public double distance(String i, int k) {
        double dist = 0;
        String tipo;

        String attributeValues[] = i.split(",");
        for (int l = 0; l < nvariables; l++) {
            String value = attributeValues[l].trim();

            tipo = input_arff.getTipoAtributo().get(l).trim().toUpperCase();
            if (!value.equals("?")) {
                if ((tipo.equals("NUMERIC") || tipo.equals("REAL") || tipo.equals("INTEGER")) && gravCenters[k][l].compareTo("<null>") != 0) {
                    // real value, apply euclidean distance
                    dist += Math.sqrt((Double.parseDouble(value) - (Double.parseDouble(gravCenters[k][l])))
                            * (Double.parseDouble(value) - (Double.parseDouble(gravCenters[k][l]))));
                } else {
                    if (!value.equals(gravCenters[k][l])) {
                        dist += 1;
                    }
                }
            }
        }
        return dist;
    }

    /**
     * <p>
     * Computes the nearest cluster to the given instance
     * </p>
     *
     * @param inst The instance we are interested to compare
     * @return The index of the nearest cluster
     */
    public int nearestCenter(String inst) {
        int nearest = 0;
        double minDist = this.distance(inst, 0);
        double distAct;

        for (int k = 1; k < numCenters; k++) {
            distAct = this.distance(inst, k);
            if (distAct < minDist) {
                minDist = distAct;
                nearest = k;
            }
        }
        return nearest;
    }

    /**
     * <p>
     * this function initializes a center with the values of a given instance.
     * </p>
     *
     * @param i the initialization instance
     * @param c the index of the cluster to be initialized
     */
    public void copyCenter(String i, int c) {
        String attributeValues[] = i.split(",");
        for (int l = 0; l < nvariables; l++) {
            String value = attributeValues[l].trim();

            if (value.equals("?")) {
                gravCenters[c][l] = "<null>";
            } else {
                gravCenters[c][l] = value;
            }
        }
    }

    /**
     * <p>
     * Recalculates all the centroids using a given InstanceSet, in order to
     * reduce the total sum of distances for each object to the centroid of the
     * cluster, which the object belongs to
     * </p>
     *
     */
    public void recalculateCenters() {
        int[][] nInst;
        double tmp;
        String i;
        int c;
        String tipo;
        FreqList[][] modes;
        String[][] oldGC;

        modes = new FreqList[numCenters][nvariables];
        nInst = new int[numCenters][nvariables];

        oldGC = gravCenters;
        gravCenters = new String[numCenters][nvariables];

        for (int a = 0; a < numCenters; a++) {
            for (int b = 0; b < nvariables; b++) {
                nInst[a][b] = 0;
                gravCenters[a][b] = "a";
                modes[a][b] = new FreqList();
            }
        }

        for (int m = 0; m < ndatos; m++) {
            i = input_arff.getData().get(m);
            c = this.getClusterOf(m);
            String attributeValues[] = i.split(",");
            for (int l = 0; l < nvariables; l++) {
                String value = attributeValues[l].trim();

                tipo = input_arff.getTipoAtributo().get(l).trim().toUpperCase();
                if (!value.equals("?")) {
                    if (tipo.equals("NUMERIC") || tipo.equals("REAL") || tipo.equals("INTEGER")) {
                        nInst[c][l]++;
                        if (gravCenters[c][l].compareTo("a") == 0) {
                            gravCenters[c][l] = "0";
                        }
                        tmp = Double.parseDouble(gravCenters[c][l]);
                        tmp += Double.parseDouble(value);
                        gravCenters[c][l] = String.valueOf(tmp);

                    } else {
                        modes[c][l].AddElement(value);
                        nInst[c][l]++;
                    }
                }
            }
            for (int l = 0; l < nvariables; l++) {
                tipo = input_arff.getTipoAtributo().get(l).trim().toUpperCase();
                if (!tipo.equals("NUMERIC") && !tipo.equals("REAL") && !tipo.equals("INTEGER")) {
                    if (modes[c][l].numElems() > 0) {
                        gravCenters[c][l] = (modes[c][l].mostCommon()).getValue();
                    } else { //what do we do if no valid value is available among the instances of this cluster for this attribute?
                        //gravCenters[c][l] = new String("<null>");
                        //instead of the previous solution, lets leave the old attribute in the centroid as is
                        gravCenters[c][l] = oldGC[c][l];
                    }
                }
            }
        }
        // compute the means for real attributes
        for (int b = 0; b < nvariables; b++) {
            tipo = input_arff.getTipoAtributo().get(b).trim().toUpperCase();
            if (tipo.equals("NUMERIC") || tipo.equals("REAL") || tipo.equals("INTEGER")) {
                for (int a = 0; a < numCenters; a++) {
                    if (gravCenters[a][b].compareTo("a") != 0) {
                        tmp = Double.parseDouble(gravCenters[a][b]);
                        tmp = tmp / nInst[a][b];
                        gravCenters[a][b] = String.valueOf(tmp);
                    } else {//what do we do if no valid value is available among the instances of this cluster for this attribute?
                        //gravCenters[a][b] = new String("<null>");
                        //instead of the previous solution, lets leave the old attribute in the centroid as is
                        gravCenters[a][b] = oldGC[a][b];
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Updates the cluster membership of the instance to the nearest cluster
     * </p>
     *
     * @param i The considered instance
     * @param orderOf_i The index of the instance i
     */
    public void setClusterOf(String i, int orderOf_i) {
        centerOf[orderOf_i] = this.nearestCenter(i);
    }

    /**
     * <p>
     * Returns the cluster to which the given instance belongs to
     * </p>
     *
     * @param orderOf_i The index of the instance
     * @return The index of the cluster to this isntance belongs to.
     */
    public int getClusterOf(int orderOf_i) {
        return centerOf[orderOf_i];
    }

    /**
     * <p>
     * Get the value of an attribute of the indicated centroid
     * </p>
     *
     * @param cluster The index of the cluster (centroid)
     * @param position The attribute (dimension) to be obtained
     * @return the current value of the dimension of the given cluster
     */
    public String valueAt(int cluster, int position) {
        return gravCenters[cluster][position];
    }
}
