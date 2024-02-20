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
 * @author Written by Julián Luengo Martín 10/11/2005
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
import util.arff.InformacoesArff;
import java.util.ArrayList;

/**
 * <p>
 * This class delete all instances with at least one missing value from the data
 * set
 * </p>
 */
public class IgnoreMissing {

    int ndatos = 0;
    InformacoesArff input_arff;

    /**
     * Creates a new instance of ignore_missing
     *
     * @param in input arff
     */
    public IgnoreMissing(InformacoesArff in) {
        this.input_arff = in;
    }

    /**
     * <p>
     * Process the training and test files provided in the parameters file to
     * the constructor.
     * </p>
     */
    public ArrayList<String> process() {
        try {

            ndatos = input_arff.getData().size();
            ArrayList<String> data = new ArrayList();
            
            for (int i = 0; i < ndatos; i++) {
                if (!input_arff.getData().get(i).contains("?")) {
                    data.add(input_arff.getData().get(i));
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
