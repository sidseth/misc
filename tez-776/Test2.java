/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/* A more optimal representation of routed events for Tez */
public class Test2 {

  List<EventStorage> allSourceEvents;

  static class TargetIndexStorage {
    int startPos;
    int minIndex;
    int maxIndex;
  }

  static class EventStorage { //5 references
    String event;
    BitSet targetTasks;
    BitSet targetIndicesBitSet;
    int lastPositionInIndexBits = 0;
    List<TargetIndexStorage> targetIndices; // A map may be better.
    // TODO This can be optimized further by encoding ALL_TASKS, ALL_DESTINATIONS
  }


  void addEvent(String event, List<TaskAndTargetIndices> targets) {
    EventStorage eventStorage = new EventStorage();
    allSourceEvents.add(eventStorage);
    eventStorage.targetTasks = new BitSet();
    eventStorage.targetIndicesBitSet = new BitSet();
    eventStorage.targetIndices = new LinkedList<TargetIndexStorage>();
    for (TaskAndTargetIndices target : targets) {
      int targetTask = target.taskIndex;
      eventStorage.targetTasks.set(targetTask);
      int high = Integer.MIN_VALUE;
      int low = Integer.MAX_VALUE;
      for (Integer targetIndex : target.targetIndices) {
        TargetIndexStorage tis = new TargetIndexStorage();
        tis.startPos = eventStorage.lastPositionInIndexBits;
        eventStorage.targetIndicesBitSet.set(eventStorage.lastPositionInIndexBits + targetIndex);
        if (targetIndex > high) {
          high = targetIndex;
          tis.maxIndex = high;
        }
        if (targetIndex < low) {
          low = targetIndex;
          tis.minIndex = low;
        }
      }
      eventStorage.lastPositionInIndexBits = eventStorage.lastPositionInIndexBits + (high - low) + 1;
    }
  }

  /* ScatterGather with CompositeDataMovementEvents
    A=1, M, N, K=1
    Total Events in allSourceEvents = M | 5 references.
    Size of each element
      Size of targetTasks = N -> eq N/8 bytes -> N/32 references + 3 refs for BitSet
      Size of list: N entries:
        Size of each entry -> 1 reference + 3 integers -> 4 references
      Size of targetIndicesBitSet -> M overall input indices, 1 bit used for each of them -> M bits -> M / 32 references + 3 refs for bitset
      Size of targetIndicesBitSet -> MXN -> M X N / 8 bytes -> M X N /32 references  + 3 refs for bitset


      e.g. 10000 X 1000

      10K events -> 5 references each = 20 bytes
      Size of each element: 28b + 16KB + 1.5KB => ~18KB
        targetTasks -> 1000/32 = 31 bytes = 4 references + 3 references for BitSet = 7 references = 28 bytes
        list -> 1000 entries -> 4000 references = 16Kbytes
        targetIndicesBitSet -> 100000 bits -> 12500 bytes . + 3 references = 1.5KB


     10K events - each taking taking ~18KB + 20 bytes ->  180000KB -> 175MB of storage
   */















  static class TaskAndTargetIndices {
    int taskIndex;
    List<Integer> targetIndices;
  }
}
