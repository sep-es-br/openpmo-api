MATCH (wm:MilestoneModel)-[isb:IS_SORTED_BY]->(pm:PropertyModel{name:'Data'})
set wm.sortByField = 'date'
return wm, isb, pm;

MATCH (wm:WorkpackModel)-[isb:IS_SORTED_BY]->(pm:PropertyModel{name:'name'})
set wm.sortByField = 'name'
return wm, isb, pm;

MATCH (wm:WorkpackModel)-[isb:IS_SORTED_BY]->(pm:PropertyModel{name:'fullName'})
set wm.sortByField = 'fullName'
return wm, isb, pm;

MATCH (s:Workpack)<-[f1:FEATURES]-(ps:Property)-[i:IS_SNAPSHOT_OF]->(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'name'})
set s.name = ps.value
return s,f1,ps;

MATCH (s:Workpack)<-[f1:FEATURES]-(ps:Property)-[i:IS_SNAPSHOT_OF]->(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'fullName'})
set s.fullName = ps.value
return s,f1,ps;

MATCH (s:Workpack)<-[f1:FEATURES]-(ps:Property)-[i:IS_SNAPSHOT_OF]->(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'Data'})
set s.date = ps.value
return s,f1,ps;

MATCH (s:Workpack)<-[f1:FEATURES]-(ps:Property)-[i:IS_SNAPSHOT_OF]->(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'name'})
detach delete ps;

MATCH (s:Workpack)<-[f1:FEATURES]-(ps:Property)-[i:IS_SNAPSHOT_OF]->(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'fullName'})
detach delete ps;

MATCH (s:Workpack)<-[f1:FEATURES]-(ps:Property)-[i:IS_SNAPSHOT_OF]->(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'Data'})
detach delete ps;


MATCH (w:Workpack)<-[f1:FEATURES]-(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'name'})
set w.name = p.value
return w,f1,p,d1,pm;


MATCH (w:Workpack)<-[f1:FEATURES]-(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'fullName'})
set w.fullName = p.value
return w,f1,p,d1,pm;


MATCH (w:Workpack)<-[f1:FEATURES]-(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'Data'})
set w.date = p.value
return w,f1,p,d1,pm;


MATCH (w:Workpack)<-[f1:FEATURES]-(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'name'})
detach delete p,pm;

MATCH (w:Workpack)<-[f1:FEATURES]-(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'fullName'})
detach delete p,pm;

MATCH (w:Workpack)<-[f1:FEATURES]-(p:Property)-[d1:IS_DRIVEN_BY]->(pm:PropertyModel{name:'Data'})
detach delete p,pm;

match (wm:WorkpackModel)<-[f:FEATURES]-(pm:PropertyModel{name:'name'}) detach delete pm

match (wm:WorkpackModel)<-[f:FEATURES]-(pm:PropertyModel{name:'fullName'}) detach delete pm

match (wm:MilestoneModel)<-[f:FEATURES]-(pm:PropertyModel{name:'Data'}) detach delete pm



