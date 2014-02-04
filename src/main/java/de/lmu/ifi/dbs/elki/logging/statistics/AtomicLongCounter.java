package de.lmu.ifi.dbs.elki.logging.statistics;

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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Class to count events in a thread-safe counter.
 * 
 * @author Erich Schubert
 */
public class AtomicLongCounter extends AbstractStatistic implements Counter {
  /**
   * The counter to use.
   */
  AtomicLong counter = new AtomicLong(0);

  /**
   * Constructor.
   * 
   * @param key Key to report.
   */
  public AtomicLongCounter(String key) {
    super(key);
  }

  @Override
  public long increment() {
    return counter.incrementAndGet();
  }

  @Override
  public long decrement() {
    return counter.decrementAndGet();
  }

  @Override
  public long increment(long i) {
    return counter.addAndGet(i);
  }

  @Override
  public long getValue() {
    return counter.get();
  }

  @Override
  public String formatValue() {
    return Long.toString(getValue());
  }
}
