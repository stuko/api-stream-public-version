db.getCollection("LOG").remove({})
db.getCollection("LOG").find({topologyId : "TP_13923818435935922", moduleName : "-12.0" , "result.buy_yn" : "Y" })
db.getCollection("LOG").find({result : {buy_yn : "N"} })
db.getCollection("LOG").find({topologyId : "TP_13923818435935922", moduleName : "-13.0" , "parameter.buy_yn" : "Y" })

