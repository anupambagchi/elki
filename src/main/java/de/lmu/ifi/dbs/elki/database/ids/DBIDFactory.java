package de.lmu.ifi.dbs.elki.database.ids;

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

import de.lmu.ifi.dbs.elki.database.ids.distance.DistanceDBIDPair;
import de.lmu.ifi.dbs.elki.database.ids.distance.DoubleDistanceDBIDPair;
import de.lmu.ifi.dbs.elki.database.ids.distance.DoubleDistanceKNNHeap;
import de.lmu.ifi.dbs.elki.database.ids.distance.KNNHeap;
import de.lmu.ifi.dbs.elki.database.ids.distance.KNNList;
import de.lmu.ifi.dbs.elki.database.ids.integer.TrivialDBIDFactory;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.persistent.ByteBufferSerializer;
import de.lmu.ifi.dbs.elki.persistent.FixedSizeByteBufferSerializer;

/**
 * Factory interface for generating DBIDs. See {@link #FACTORY} for the static
 * instance to use.
 * 
 * @author Erich Schubert
 * 
 * @apiviz.stereotype factory
 * @apiviz.uses DBID oneway - - «create»
 * @apiviz.uses DBIDs oneway - - «create»
 * @apiviz.uses DBIDPair oneway - - «create»
 * @apiviz.uses DBIDRange oneway - - «create»
 * @apiviz.uses ArrayModifiableDBIDs oneway - - «create»
 * @apiviz.uses HashSetModifiableDBIDs oneway - - «create»
 * @apiviz.uses HashSetModifiableDBIDs oneway - - «create»
 * @apiviz.has ByteBufferSerializer oneway - - provides
 */
public interface DBIDFactory {
  /**
   * Static DBID factory to use.
   */
  final static DBIDFactory FACTORY = new TrivialDBIDFactory();

  /**
   * Make a new DBID variable.
   * 
   * @param val Initial value.
   * @return Variable
   */
  DBIDVar newVar(DBIDRef val);

  /**
   * Import and integer as DBID.
   * 
   * Note: this may not be possible for some factories!
   * 
   * @param id Integer ID to import
   * @return DBID
   */
  DBID importInteger(int id);

  /**
   * Assign an integer value to a DBID variable.
   * 
   * Note: this may not be possible for some factories!
   * 
   * @param var Variable
   * @param val Integer value
   */
  void assignVar(DBIDVar var, int val);

  /**
   * Generate a single DBID.
   * 
   * @return A single DBID
   */
  DBID generateSingleDBID();

  /**
   * Return a single DBID for reuse.
   * 
   * @param id DBID to deallocate
   */
  void deallocateSingleDBID(DBIDRef id);

  /**
   * Generate a static DBID range.
   * 
   * @param size Requested size
   * @return DBID range
   */
  DBIDRange generateStaticDBIDRange(int size);

  /**
   * Deallocate a static DBID range.
   * 
   * @param range Range to deallocate
   */
  void deallocateDBIDRange(DBIDRange range);

  /**
   * Make a DBID pair from two existing DBIDs.
   * 
   * @param id1 first DBID
   * @param id2 second DBID
   * 
   * @return new pair.
   */
  DBIDPair newPair(DBIDRef id1, DBIDRef id2);

  /**
   * Make a double-DBID pair.
   * 
   * @param val Double value
   * @param id DBID
   * @return New pair
   */
  DoubleDBIDPair newPair(double val, DBIDRef id);

  /**
   * Make a new distance-DBID pair.
   * 
   * @param val Distance value
   * @param id Object ID
   * @param <D> Distance type
   * @return New pair
   */
  <D extends Distance<D>> DistanceDBIDPair<D> newDistancePair(D val, DBIDRef id);

  /**
   * Make a new distance-DBID pair.
   * 
   * @param val Distance value
   * @param id Object ID
   * @return New pair
   */
  DoubleDistanceDBIDPair newDistancePair(double val, DBIDRef id);

  /**
   * Make a new (modifiable) array of DBIDs.
   * 
   * @return New array
   */
  ArrayModifiableDBIDs newArray();

  /**
   * Make a new (modifiable) hash set of DBIDs.
   * 
   * @return New hash set
   */
  HashSetModifiableDBIDs newHashSet();

  /**
   * Make a new (modifiable) array of DBIDs.
   * 
   * @param size Size hint
   * @return New array
   */
  ArrayModifiableDBIDs newArray(int size);

  /**
   * Make a new (modifiable) hash set of DBIDs.
   * 
   * @param size Size hint
   * @return New hash set
   */
  HashSetModifiableDBIDs newHashSet(int size);

  /**
   * Make a new (modifiable) array of DBIDs.
   * 
   * @param existing existing DBIDs to use
   * @return New array
   */
  ArrayModifiableDBIDs newArray(DBIDs existing);

  /**
   * Make a new (modifiable) hash set of DBIDs.
   * 
   * @param existing existing DBIDs to use
   * @return New hash set
   */
  HashSetModifiableDBIDs newHashSet(DBIDs existing);

  /**
   * Create an appropriate heap for the distance function.
   * 
   * This will use a double heap if appropriate.
   * 
   * @param factory distance prototype
   * @param k K value
   * @param <D> distance type
   * @return New heap of size k, appropriate for this distance type.
   */
  <D extends Distance<D>> KNNHeap<D> newHeap(D factory, int k);

  /**
   * Build a new heap from a given list.
   * 
   * @param exist Existing result
   * @param <D> Distance type
   * @return New heap
   */
  <D extends Distance<D>> KNNHeap<D> newHeap(KNNList<D> exist);

  /**
   * Create an appropriate heap for double distances.
   * 
   * @param k K value
   * @return New heap of size k, appropriate for this distance type.
   */
  DoubleDistanceKNNHeap newDoubleDistanceHeap(int k);

  /**
   * Get a serializer for DBIDs.
   * 
   * @return DBID serializer
   */
  ByteBufferSerializer<DBID> getDBIDSerializer();

  /**
   * Get a serializer for DBIDs with static size.
   * 
   * @return DBID serializer
   */
  FixedSizeByteBufferSerializer<DBID> getDBIDSerializerStatic();

  /**
   * Get type restriction.
   * 
   * @return type restriction for DBIDs
   */
  Class<? extends DBID> getTypeRestriction();

  /**
   * Compare two DBIDs, for sorting.
   * 
   * @param a First
   * @param b Second
   * @return Comparison result
   */
  int compare(DBIDRef a, DBIDRef b);

  /**
   * Compare two DBIDs, for equality testing.
   * 
   * @param a First
   * @param b Second
   * @return Comparison result
   */
  boolean equal(DBIDRef a, DBIDRef b);

  /**
   * Print a DBID as string.
   * 
   * @param id DBID reference
   * @return Formatted ID
   */
  String toString(DBIDRef id);

  /**
   * Get the invalid DBID value, usable as "undefined" placeholder.
   * 
   * @return Invalid value
   */
  DBIDRef invalid();
}
