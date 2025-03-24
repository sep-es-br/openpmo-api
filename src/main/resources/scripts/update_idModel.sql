MATCH (w:Workpack{deleted:false})-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel)
      WHERE id(wm) <> w.idWorkpackModel set w.idWorkpackModel = id(wm) return id(w)