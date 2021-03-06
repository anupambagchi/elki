package de.lmu.ifi.dbs.elki.utilities.pairs;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2013
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;

/**
 * Pair that can <em>only</em> be compared by it's second component.
 * 
 * @author Erich Schubert
 * 
 * @param <FIRST> first type (comparable)
 * @param <SECOND> second type
 */
public class SCPair<FIRST, SECOND extends Comparable<? super SECOND>> extends Pair<FIRST, SECOND> implements Comparable<SCPair<FIRST, SECOND>> {
  /**
   * Initialize pair
   * 
   * @param first first parameter
   * @param second second parameter
   */
  public SCPair(FIRST first, SECOND second) {
    super(first, second);
  }

  /**
   * Generic derived compare function.
   * 
   * @param other Object to compare to
   * @return comparison result
   */
  @Override
  public int compareTo(SCPair<FIRST, SECOND> other) {
    // try comparing by first
    if(this.second != null) {
      if(other.second == null) {
        return -1;
      }
      int delta1 = this.second.compareTo(other.second);
      if(delta1 != 0) {
        return delta1;
      }
    }
    else if(other.second != null) {
      return +1;
    }
    return 0;
  }

  /**
   * Array constructor for generics
   * 
   * @param <F> First type
   * @param <S> Second type
   * @param size Size of array to be constructed
   * @return New array of requested size
   */
  public static final <F, S extends Comparable<? super S>> SCPair<F, S>[] newArray(int size) {
    Class<SCPair<F,S>> paircls = ClassGenericsUtil.uglyCastIntoSubclass(SCPair.class);    
    return ClassGenericsUtil.newArrayOfNull(size, paircls);
  }
}