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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Sample representation of routed events for Tez */
public class Test {

  List<EventTargetTasksPair> allSourceEvents = new ArrayList<EventTargetTasksPair>();

//  // targetIndices.get(eventIndex).
//  List<List<BitSet>> targetIndices;
//
//  static class TargetTaskTargetIndex {
//
//  }

  // 3 references
  static class EventTargetTasksPair {
    String event;
    BitSet targetTasks;
    Map<Integer, BitSet> targetIndices;
  }



  void addEvent(String event, List<TaskAndTargetIndices> targets) {
    EventTargetTasksPair eventTable = new EventTargetTasksPair();
    allSourceEvents.add(eventTable);
    int eventIndex = allSourceEvents.size() - 1;
    eventTable.targetTasks = new BitSet();
    eventTable.targetIndices = new HashMap<Integer, BitSet>();
    for (TaskAndTargetIndices target : targets) {
      eventTable.targetTasks.set(target.taskIndex);
      BitSet indices = new BitSet(1);
      eventTable.targetIndices.put(target.taskIndex, indices);
      for (Integer targetIndex : target.targetIndices) {
        indices.set(targetIndex);
      }
    }
  }


  // TODO - finish and fix these computations.
  /* ScatterGather with CompositeDataMovementEvents
     A=1, M, N, K=1
     Total Events in allSourceEvents = M | 3 references
     Size of each bitset -> N -> eq N/8 bytes -> N/32 references
     10 reference overhead for HashMap
     So far: 13 references + N/32 bytes for the bitset.

     M X N BitSets - 1 reference for the bitSet + 3 references inside bitset + 1 long entry => 4 references + 8 bytes



   */
  static class TaskAndTargetIndices {
    int taskIndex;
    List<Integer> targetIndices;
  }



}
