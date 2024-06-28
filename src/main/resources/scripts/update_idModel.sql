MATCH (w:Workpack{deleted:false})-[ii:IS_INSTANCE_BY]->(wm:WorkpackModel)
      WHERE id(wm) <> w.idWorkpackModel set w.idWorkpackModel return id(w)