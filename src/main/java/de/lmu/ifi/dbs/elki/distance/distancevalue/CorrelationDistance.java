package de.lmu.ifi.dbs.elki.distance.distancevalue;

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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.regex.Pattern;

/**
 * The correlation distance is a special Distance that indicates the
 * dissimilarity between correlation connected objects. The correlation distance
 * between two points is a pair consisting of the correlation dimension of two
 * points and the euclidean distance between the two points.
 * 
 * @author Elke Achtert
 * @param <D> distance type
 */
public abstract class CorrelationDistance<D extends CorrelationDistance<D>> extends AbstractDistance<D> {
  /**
   * The component separator used by correlation distances.
   * 
   * Note: Do NOT use regular expression syntax characters!
   */
  public static final String SEPARATOR = "x";

  /**
   * The pattern used for correlation distances
   */
  public static final Pattern CORRELATION_DISTANCE_PATTERN = Pattern.compile("\\d+" + Pattern.quote(SEPARATOR) + "\\d+(\\.\\d+)?([eE][-]?\\d+)?");

  /**
   * Generated SerialVersionUID.
   */
  private static final long serialVersionUID = 2829135841596857929L;

  /**
   * The correlation dimension.
   */
  protected int correlationValue;

  /**
   * The euclidean distance.
   */
  protected double euclideanValue;

  /**
   * Empty constructor for serialization purposes.
   */
  public CorrelationDistance() {
    // for serialization
  }

  /**
   * Constructs a new CorrelationDistance object consisting of the specified
   * correlation value and euclidean value.
   * 
   * @param correlationValue the correlation dimension to be represented by the
   *        CorrelationDistance
   * @param euclideanValue the euclidean distance to be represented by the
   *        CorrelationDistance
   */
  public CorrelationDistance(int correlationValue, double euclideanValue) {
    this.correlationValue = correlationValue;
    this.euclideanValue = euclideanValue;
  }

  /**
   * Returns a string representation of this CorrelationDistance.
   * 
   * @return the correlation value and the euclidean value separated by blank
   */
  @Override
  public String toString() {
    return Integer.toString(correlationValue) + SEPARATOR + Double.toString(euclideanValue);
  }

  /**
   * Compares this CorrelationDistance with the given CorrelationDistance wrt
   * the represented correlation values. If both values are considered to be
   * equal, the euclidean values are compared. Subclasses may need to overwrite
   * this method if necessary.
   * 
   * @return the value of {@link Integer#compareTo(Integer)}
   *         this.correlationValue.compareTo(other.correlationValue)} if it is a
   *         non zero value, the value of {@link Double#compare(double,double)
   *         Double.compare(this.euclideanValue, other.euclideanValue)}
   *         otherwise
   */
  @Override
  public int compareTo(D other) {
    int compare = (this.correlationValue < other.getCorrelationValue()) ? -1 : (this.correlationValue > other.getCorrelationValue()) ? +1 : 0;
    if (compare != 0) {
      return compare;
    } else {
      return Double.compare(this.euclideanValue, other.getEuclideanValue());
    }
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = correlationValue;
    temp = euclideanValue >= Double.MIN_NORMAL ? Double.doubleToLongBits(euclideanValue) : 0L;
    result = 29 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CorrelationDistance<D> other = (CorrelationDistance<D>) obj;
    if (this.correlationValue != other.correlationValue) {
      return false;
    }
    if (this.euclideanValue != other.euclideanValue) {
      return false;
    }
    return true;
  }

  /**
   * Returns the correlation dimension between the objects.
   * 
   * @return the correlation dimension
   */
  public int getCorrelationValue() {
    return correlationValue;
  }

  /**
   * Returns the euclidean distance between the objects.
   * 
   * @return the euclidean distance
   */
  public double getEuclideanValue() {
    return euclideanValue;
  }

  /**
   * Writes the correlation value and the euclidean value of this
   * CorrelationDistance to the specified stream.
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(correlationValue);
    out.writeDouble(euclideanValue);
  }

  /**
   * Reads the correlation value and the euclidean value of this
   * CorrelationDistance from the specified stream.
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException {
    correlationValue = in.readInt();
    euclideanValue = in.readDouble();
  }

  /**
   * Returns the number of Bytes this distance uses if it is written to an
   * external file.
   * 
   * @return 12 (4 Byte for an integer, 8 Byte for a double value)
   */
  @Override
  public int externalizableSize() {
    return 12;
  }
}
