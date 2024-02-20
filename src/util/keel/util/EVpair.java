package util.keel.util;

/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/**
 * This class implements a pair of eigenvalues and their index, in order to be
 * possible to sort them, when inserted in a Collections structure
 * @author Julián Luengo Martín
 *
 */
public class EVpair implements Comparable {

	/** the eigenvalue*/
	public double eigenValue;
	/** the index that this element has in the original structure*/
	public int evIndex;
	
	/**
	 * Copy constructor
	 * @param newvalue the original eigenvalue
	 * @param newindex the new index
	 */
	public EVpair(double newvalue,int newindex){
		eigenValue = newvalue;
		evIndex = newindex;
	}
	
        @Override
	public int compareTo(Object o){
		EVpair p = (EVpair) o;
		if(this.eigenValue > p.eigenValue)
			return 1;
		if(this.eigenValue < p.eigenValue)
			return -1;
		return 0;
	}
}
