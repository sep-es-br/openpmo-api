MATCH (w:Workpack)-[:IS_IN]->(parent:Workpack) WHERE  w.idParent IS NOT NULL AND w.idParent <> id(parent)
SET w.idParent = id(parent)
RETURN id(w),  w.idParent, id(parent);
