MATCH (master:Step)<-[:IS_SNAPSHOT_OF]-(s:Step) WHERE s.periodFromStart IS NULL
SET s.periodFromStart = master.periodFromStart
RETURN id(s), s.periodFromStart,master.periodFromStart
