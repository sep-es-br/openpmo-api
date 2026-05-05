WITH 
	$scope AS _scope,
	$baselineId AS _baselineId,
	$monthYear AS _anomesRef,
	$sCurve AS _sCurve

// Pega todas as entregas MASTER não excluídas nem canceladas
MATCH (Wp:Workpack)<-[:IS_IN*1..]-(w:Deliverable)-[:BELONGS_TO]->(p:Plan)-[:IS_ADOPTED_BY]->(o:Office)
WHERE (id(w) = _scope OR id(Wp) = _scope OR id(p) = _scope)
  AND (NOT w.deleted AND NOT w.canceled)

WITH DISTINCT w, _baselineId, _anomesRef, p, _sCurve,
  
  date(p.start).year*100  + date(p.start).month  AS planoStart,
  date(p.finish).year*100 + date(p.finish).month AS planoFinish

// Relacionamentos principais
MATCH 
	(w)<-[:FEATURES]-(sc:Schedule),
	(w)<-[:IS_SNAPSHOT_OF]-(sn_w:Deliverable {category:'SNAPSHOT'})-[:COMPOSES]->(bl:Baseline)<-[pjbl:IS_BASELINED_BY]-(pj:Project),
	(w)-[:IS_IN*]->(pj),
	(sn_w)<-[:FEATURES]-(sn_sc:Schedule)
WHERE (_baselineId IS NULL AND bl.active) OR (id(bl) = _baselineId AND bl.status = 'APPROVED')

// Consolidar MASTER + SNAPSHOT em um único fluxo (cada CALL retornando anomes + métricas)
CALL {
	// MASTER part
	WITH sc, _anomesRef
	MATCH (sc)<-[:COMPOSES]-(st:Step)
	OPTIONAL MATCH (st)-[co:CONSUMES]->(:CostAccount)
	WITH sc, st, co, _anomesRef,
                (date(sc.start)+duration({months: st.periodFromStart})).year * 100 + (date(sc.start)+duration({months: st.periodFromStart})).month AS anomes
	RETURN anomes,
		toFloat(st.plannedWork) AS fisicoreprogramado,
		CASE WHEN anomes <= _anomesRef THEN toFloat(st.actualWork) ELSE 0 END AS fisicorealizado,
		0.0 AS fisicoplanejado,
		toFloat(co.plannedCost) AS custoreprogramado,
		CASE WHEN anomes <= _anomesRef THEN toFloat(co.actualCost) ELSE 0 END AS custorealizado,
		0.0 AS custoplanejado
		
UNION ALL
	// SNAPSHOT part
	WITH sc, sn_sc
	MATCH (sn_sc)<-[:COMPOSES]-(sn_st:Step)
	OPTIONAL MATCH (sn_st)-[sn_co:CONSUMES]->(:CostAccount)
	WITH sn_st, sn_co,
                (date(sn_sc.start) + duration({months: sn_st.periodFromStart})).year*100 + (date(sn_sc.start) + duration({months: sn_st.periodFromStart})).month AS anomes
	RETURN anomes,
		0.0 AS fisicoreprogramado,
		0.0 AS fisicorealizado,
		toFloat(sn_st.plannedWork) AS fisicoplanejado,
		0.0 AS custoreprogramado,
		0.0 AS custorealizado,
		round(toFloat(sn_co.plannedCost),2) AS custoplanejado
}


// Filtra meses fora do plano e agrega por anomes/wId
WITH anomes, id(w) AS wId,planoStart,planoFinish,p, _sCurve,	
  w.completed as wCompleted,_anomesRef,
  coalesce(sum(custoreprogramado),0) AS custoreprogramado,
  coalesce(sum(custoplanejado),0) AS custoplanejado,
  coalesce(sum(custorealizado),0) AS custorealizado,
  coalesce(sum(fisicoreprogramado),0) AS fisicoreprogramado,
  coalesce(sum(fisicoplanejado),0) AS fisicoplanejado,
  coalesce(sum(fisicorealizado),0) AS fisicorealizado,
  case when sc.start < p.start then p.start else sc.start end AS masterStart,
  case when sc.end > p.finish then p.finish else sc.end end AS masterEnd,
  case when sn_sc.start < p.start then p.start else sn_sc.start end AS snapshotStart,
  case when sn_sc.end > p.finish then p.finish else sn_sc.end end AS snapshotEnd
WHERE anomes >= planoStart AND anomes <= planoFinish

// Agrupa por entrega (wId) os rows mensais existentes
WITH wId, wCompleted, _anomesRef, _sCurve,
	masterStart,
	masterEnd,
	snapshotStart,
	snapshotEnd,
	collect({
		anomes: anomes,
		custoreprogramado: custoreprogramado,
		custoplanejado: custoplanejado,
		custorealizado: custorealizado,
		fisicoreprogramado: fisicoreprogramado,
		fisicoplanejado: fisicoplanejado,
		fisicorealizado: fisicorealizado
	}) AS rows

// Calcula BAC (total por entrega) usando os rows disponíveis (antes de preencher zeros)
WITH 
  wId, wCompleted, _anomesRef, _sCurve,
  masterStart,
  masterEnd,
  snapshotStart,
  snapshotEnd,
  rows,
  round(reduce(total=0.0, r IN rows | total + coalesce(r.custoplanejado,0)),2) AS bacEntrega,
  round(reduce(total=0.0, r IN rows | total + coalesce(r.fisicoplanejado,0)),2) AS fisicoTotal,
  round(reduce(total=0.0, r IN rows | total + coalesce(r.fisicoreprogramado,0)),2) AS fisicoRepTotal

// Volto para o formato tabular, PARA APURAR O PERCENTUAL MENSAL
UNWIND rows AS r
WITH
	wId, wCompleted, bacEntrega, fisicoTotal, fisicoRepTotal, _anomesRef, _sCurve,
	masterStart,
	masterEnd,
	snapshotStart,
	snapshotEnd,
	r.anomes            		AS anomes,
	case 
		when fisicoTotal = 0 
		then 0 
		else r.fisicorealizado/fisicoTotal 
	end 						AS prcFisicoRealizado,
	r.custoreprogramado			AS custoReprogramado,
	r.custorealizado			AS custoRealizado,
	r.custoplanejado      		AS custoPlanejado,
	r.fisicorealizado			AS fisicoRealizado
order by anomes asc

// Agrupa novamente por entrega (wId) os rows mensais existentes, agora para acumular
WITH wId, wCompleted, bacEntrega, fisicoTotal, fisicoRepTotal, _anomesRef, _sCurve,
	masterStart,
	masterEnd,
	snapshotStart,
	snapshotEnd,
	collect({
		anomes: anomes,
		custoReprogramado: custoReprogramado,
		custoPlanejado: custoPlanejado,
		custoRealizado: custoRealizado,
		fisicoRealizado: fisicoRealizado,
		prcFisicoRealizado: prcFisicoRealizado	
	}) AS rows


// Coleta todas as entregas em uma lista para calcular globalAnomes
WITH collect({
  wId: wId,
  _anomesRef: _anomesRef,
  _sCurve: _sCurve,
  wCompleted: wCompleted,
  masterStart: masterStart,
  masterEnd: masterEnd,
  snapshotStart: snapshotStart,
  snapshotEnd: snapshotEnd,
  rows: rows,
  bacEntrega: bacEntrega, 
  fisicoPlanTotal: fisicoTotal, 
  fisicoRepTotal: fisicoRepTotal
}) AS entregas

// Gera o conjunto global de anomes (todos os meses que aparecem em qualquer entrega)
WITH entregas,
  apoc.coll.toSet(apoc.coll.flatten([e IN entregas | [r IN e.rows | r.anomes]])) AS globalAnomes

// Agora para cada entrega, garanta que exista uma linha para cada anomes (preenchendo com zeros)
// e ordene por anomes antes de acumular
UNWIND entregas AS ent
WITH 
	ent.wId AS wId,
	ent._anomesRef AS _anomesRef,
	ent._sCurve AS _sCurve,
	ent.wCompleted as wCompleted,
	ent.masterStart AS masterStart,
	ent.masterEnd AS masterEnd,
	ent.snapshotStart AS snapshotStart,
	ent.snapshotEnd AS snapshotEnd,
	ent.rows AS rows, 
	ent.bacEntrega AS bacEntrega,
	ent.fisicoPlanTotal AS fisicoPlanTotal, 
	ent.fisicoRepTotal AS fisicoRepTotal,
	globalAnomes


// Para cada anomes do conjunto global, achar row existente ou criar row zero
WITH wId, wCompleted, bacEntrega, masterStart, masterEnd, snapshotStart, snapshotEnd, _anomesRef, _sCurve,
		fisicoPlanTotal, fisicoRepTotal,
     [a IN apoc.coll.toSet(globalAnomes) | 
        COALESCE(
          [x IN rows WHERE x.anomes = a | x][0],
          { 
			anomes: a, 
			custoReprogramado:0.0, 
			custoPlanejado:0.0, 
			custoRealizado:0.0, 
			fisicoRealizado:0.0, 
			prcFisicoRealizado:0.0 }
        )
     ] AS VALORES


// Acumula mês a mês com REDUCE (agora sem faltar meses)
WITH 
  wId, wCompleted, _anomesRef, _sCurve, fisicoPlanTotal, fisicoRepTotal,
  bacEntrega AS bac, masterStart, masterEnd, snapshotStart, snapshotEnd,
  REDUCE(
    s = { crAcum:0.0, cpAcum:0.0, cgAcum:0.0, frAcum:0.0, pcfrAcum:0.0, listAcum: [] },
    item IN VALORES |
    {
      crAcum: s.crAcum + item.custoRealizado,
      cpAcum: s.cpAcum + item.custoPlanejado,
      cgAcum: s.cgAcum + item.custoReprogramado,
      frAcum: s.frAcum + item.fisicoRealizado,
      pcfrAcum: s.pcfrAcum + item.prcFisicoRealizado,
      listAcum: s.listAcum + 
        [{
          anomes: item.anomes,
          crAcum: s.crAcum + item.custoRealizado,
          cpAcum: s.cpAcum + item.custoPlanejado,
          cgAcum: s.cgAcum + item.custoReprogramado,
          frAcum: s.frAcum + item.fisicoRealizado,
          pcfrAcum: s.pcfrAcum + item.prcFisicoRealizado,
		  // VA(Valor Agregado) = %Concluído(pcfrAcum) * VP(cpAcum)
          
          va: 
			CASE WHEN (s.pcfrAcum + item.prcFisicoRealizado) > 1 
				THEN 1 
				ELSE (s.pcfrAcum + item.prcFisicoRealizado)
			END * (s.cpAcum + item.custoPlanejado)
        }]
    }
  ) AS accumResult

// Expande os acumulados por entrega para agregação por mês total do projeto
UNWIND accumResult.listAcum AS list
WITH
  wId, wCompleted, _anomesRef, _sCurve, fisicoPlanTotal, fisicoRepTotal,
  fisicoRepTotal - fisicoPlanTotal 	AS fisicoVariacao,
  bac AS bacEntrega, masterStart, masterEnd, snapshotStart, snapshotEnd,
  list.anomes 						AS anomes,
  case when list.pcfrAcum > 1 
	then 1 
	else list.pcfrAcum 
  end 								as pcFisicoRealizadoAcum,
  list.cgAcum 						AS custoReprogramado_Acum,
  list.crAcum 						AS custoRealizado_Acum,
  list.cpAcum 						AS custoPlanejado_Acum,
  list.frAcum 						AS fisicoRealizado_Acum,
  list.va 							AS va

// Agora agregue entre entregas para obter os totais mensais do conjunto
WITH  
  anomes, _anomesRef, _sCurve,
  min(wCompleted) as wCompleted,
  sum(fisicoPlanTotal)   		AS fisicoPlanejado_Total,
  sum(fisicoRepTotal) 			AS fisicoReprogramado_Total,
  sum(fisicoVariacao)	 		AS fisicoVariacao_Total,
  
  sum(custoReprogramado_Acum) 	AS custoReprogramado_MensalTotal,
  sum(custoRealizado_Acum)   	AS custoRealizado_MensalTotal,
  sum(custoPlanejado_Acum)   	AS custoPlanejado_MensalTotal,
  sum(fisicoRealizado_Acum)   	AS fisicoRealizado_MensalTotal,

  sum(va) 						AS valorAgregado_MensalTotal,
  avg(pcFisicoRealizadoAcum) 	AS pcFisicoRealizadoAcumMesMedio,
  // a seguir derivamos as métricas usando os valores agregados
  sum(va - custoPlanejado_Acum) AS variacaoDePrazo_MensalTotal,
  sum(va - custoRealizado_Acum) AS variacaoDeCusto_MensalTotal,
  sum(custoRealizado_Acum + bacEntrega - va) AS estimadoNaConclusao,   // observe: bacEntrega não está neste scope; adaptar se precisar usar bac por entrega
  sum(bacEntrega - va) 			AS estimadoParaConclusao,
  min(snapshotStart) 			AS snapshotStart, 
  max(snapshotEnd) 				AS snapshotEnd,
  min(masterStart) 				AS masterStart, 
  max(masterEnd) 				AS masterEnd

WITH 
    anomes																			AS mes,
	_anomesRef																		AS mesRef,
	_sCurve																			AS sCurve,
	max(anomes) as lastMonth,
    round(custoReprogramado_MensalTotal,2) 											AS custoReprogramadoAcumuladoMes,
    round(custoPlanejado_MensalTotal,2) 											AS custoPlanejadoAcumuladoMes,
    round(custoRealizado_MensalTotal,2) 											AS custoRealizadoAcumuladoMes,
    round(fisicoReprogramado_Total,4) 												AS fisicoReprogramado,
    round(fisicoPlanejado_Total,4) 													AS fisicoPlanejado,
    round(fisicoRealizado_MensalTotal,4) 											AS fisicoRealizadoAcumuladoMes,
	round(fisicoVariacao_Total,4)													AS fisicoVariacao,
	round(pcFisicoRealizadoAcumMesMedio, 4)											AS pcFisicoRealizadoAcumMesMedio,
    round(valorAgregado_MensalTotal,2) 												AS valorAgregado,
    round(variacaoDePrazo_MensalTotal,2) 											AS variacaoPrazo,
    round(variacaoDeCusto_MensalTotal,2) 											AS variacaoCusto,
    round(estimadoNaConclusao,2) 													AS estimadoNaConclusao,
    round(estimadoParaConclusao,2) 													AS estimadoParaConclusao,
    CASE WHEN custoRealizado_MensalTotal = 0.0 
		THEN NULL 
		ELSE round(valorAgregado_MensalTotal / custoRealizado_MensalTotal,4) 
	END 																			AS idc,
    CASE WHEN custoPlanejado_MensalTotal = 0.0 
		THEN NULL 
		ELSE round(valorAgregado_MensalTotal / custoPlanejado_MensalTotal,4) 
	END AS idp,
	snapshotStart as plannedStartDate,
	masterEnd as plannedEndDate, 
	masterStart as actualStartDate,

	masterStart as reprogStartDate,

    case when masterEnd > toString(date()) 
    then toString(date())
    else 
        case when wCompleted
        then masterEnd
        else toString(date())
        end
    end as actualEndDate,
   
	masterEnd as reprogEndDate

WITH mes, max(mes) as lastMonth, mesRef, custoReprogramadoAcumuladoMes, custoPlanejadoAcumuladoMes, custoRealizadoAcumuladoMes, 
	fisicoReprogramado, fisicoPlanejado, max([fisicoReprogramado, fisicoPlanejado]) as maxFisicoRepPlan, fisicoRealizadoAcumuladoMes, fisicoVariacao,
	pcFisicoRealizadoAcumMesMedio, valorAgregado, variacaoPrazo, variacaoCusto, estimadoNaConclusao, 
	estimadoParaConclusao, idc, idp, plannedStartDate,plannedEndDate, actualStartDate, reprogStartDate, 
	actualEndDate, reprogEndDate, sCurve,
	CASE
		WHEN actualStartDate IS NOT NULL AND actualEndDate IS NOT NULL THEN
		  CASE
			WHEN (duration.inMonths(date(actualStartDate), date(actualEndDate)).months + 1) <= 0 THEN
			  CASE
				WHEN duration.inDays(date(actualStartDate), date(actualEndDate)).days > 0 THEN 1
				ELSE 0
			  END
			ELSE (duration.inMonths(date(actualStartDate), date(actualEndDate)).months + 1)
		  END
		ELSE NULL
	END AS scheduleActualValue,
	CASE
		WHEN plannedStartDate IS NOT NULL AND plannedEndDate IS NOT NULL THEN
			CASE
				WHEN (duration.inMonths(date(plannedStartDate), date(plannedEndDate)).months + 1) <= 0 THEN
					CASE
						WHEN duration.inDays(date(plannedStartDate), date(plannedEndDate)).days > 0 THEN 1
						ELSE 0
					END
				ELSE (duration.inMonths(date(plannedStartDate), date(plannedEndDate)).months + 1)
			END
		ELSE NULL
	END AS schedulePlannedValue,
	CASE
	WHEN reprogStartDate IS NOT NULL AND reprogEndDate IS NOT NULL THEN
		CASE
			WHEN (duration.inMonths(date(reprogStartDate), date(reprogEndDate)).months + 1) <= 0 THEN
				CASE
					WHEN duration.inDays(date(reprogStartDate), date(reprogEndDate)).days > 0 THEN 1
					ELSE 0
				END
			ELSE (duration.inMonths(date(reprogStartDate), date(reprogEndDate)).months + 1)
		END
	ELSE NULL
	END AS scheduleForeseenValue

ORDER BY mes ASC
	
WITH
COLLECT({
	plannedCost: custoPlanejadoAcumuladoMes,
	foreseenCost: custoReprogramadoAcumuladoMes,
	costVariation: custoReprogramadoAcumuladoMes - custoPlanejadoAcumuladoMes,
	actualCost: custoRealizadoAcumuladoMes,
	actualScope: fisicoRealizadoAcumuladoMes,
	earnedValue: valorAgregado,
	anomes: mes,
	
	actualWork: pcFisicoRealizadoAcumMesMedio,
 	
	costVariation: variacaoCusto,
	scheduleVariation: variacaoPrazo,

	estimadoNaConclusao: estimadoNaConclusao, 
	estimadoParaConclusao: estimadoParaConclusao, 
	idc: idc, 
	idp: idp
}) as months,
CASE WHEN max(mesRef) > max(lastMonth) THEN max(lastMonth) ELSE max(mesRef) END AS refDate,
max(lastMonth) AS lastMonth,
max(plannedStartDate) AS schedulePlannedStartDate,
max(plannedEndDate) AS schedulePlannedEndDate,
max(reprogStartDate) AS scheduleForeseenStartDate,
max(reprogEndDate) AS scheduleForeseenEndDate,
max(actualStartDate) AS scheduleActualStartDate,
max(actualEndDate) AS scheduleActualEndDate,
max(schedulePlannedValue) AS schedulePlannedValue,
max(scheduleForeseenValue) AS scheduleForeseenValue,
max(scheduleActualValue) AS scheduleActualValue,
max(fisicoPlanejado) AS fisicoPlanejado,
max(fisicoReprogramado) AS fisicoReprogramado,
max(fisicoVariacao) AS fisicoVariacao,
max(maxFisicoRepPlan) AS maxFisicoRepPlan,
max(sCurve) AS sCurve

WITH 
    {
        costPlannedValue: [x IN months WHERE x.anomes = lastMonth][0].plannedCost,
        costForeseenValue: [x IN months WHERE x.anomes = lastMonth][0].foreseenCost,
		costVariation: [x IN months WHERE x.anomes = lastMonth][0].costVariation,  // costForseenValue - costPlannedValue
		costActualValue: [x IN months WHERE x.anomes = refDate][0].actualCost,

        schedulePlannedStartDate: schedulePlannedStartDate,
        schedulePlannedEndDate: schedulePlannedEndDate,
		
        scheduleForeseenStartDate: scheduleForeseenStartDate,
        scheduleForeseenEndDate: scheduleForeseenEndDate,
        
		scheduleActualStartDate: scheduleActualStartDate,
        scheduleActualEndDate: scheduleActualEndDate,
        
		schedulePlannedValue: schedulePlannedValue,
        scheduleActualValue: scheduleActualValue,		

		scheduleForeseenValue: scheduleForeseenValue,		
		scheduleVariation: duration.inDays(date(schedulePlannedEndDate), date(scheduleForeseenEndDate)).days,
		
		scopeActualValue: [x IN months WHERE x.anomes = refDate][0].actualScope,
		scopeForeseenValue: fisicoReprogramado,
		scopePlannedValue: fisicoPlanejado,
		
		scopeVariation: fisicoVariacao,
        
		scopeActualVariationPercent: 
			CASE WHEN [x IN months WHERE x.anomes = refDate][0].actualScope = 0
			THEN 0
			ELSE 
				CASE WHEN [x IN months WHERE x.anomes = refDate][0].actualScope > maxFisicoRepPlan
				THEN 1
				ELSE [x IN months WHERE x.anomes = refDate][0].actualScope / maxFisicoRepPlan[0]
				END
			END,
        
		scopePlannedVariationPercent: 
			CASE WHEN fisicoPlanejado = 0
				THEN 0
				ELSE 
					CASE WHEN [x IN months WHERE x.anomes = refDate][0].actualScope > maxFisicoRepPlan AND [x IN months WHERE x.anomes = refDate][0].actualScope > 0
					THEN fisicoPlanejado/[x IN months WHERE x.anomes = refDate][0].actualScope
					ELSE CASE WHEN maxFisicoRepPlan[0] > 0 
						THEN fisicoPlanejado/maxFisicoRepPlan[0]
						ELSE 0
						END
					END
				END,
        scopeForeseenVariationPercent:  
			CASE WHEN fisicoReprogramado = 0
				THEN 0
				ELSE 
					CASE WHEN [x IN months WHERE x.anomes = refDate][0].actualScope > maxFisicoRepPlan
					THEN fisicoReprogramado/[x IN months WHERE x.anomes = refDate][0].actualScope
					ELSE fisicoReprogramado/maxFisicoRepPlan[0]
					END
				END
    } AS TripleConstraintDto,

    {
        costPerformanceIndexValue: [x IN months WHERE x.anomes = refDate][0].idc,
        costPerformanceIndexVariation: [x IN months WHERE x.anomes = refDate][0].costVariation,
        schedulePerformanceIndexValue: [x IN months WHERE x.anomes = refDate][0].idp,
        schedulePerformanceIndexVariation: [x IN months WHERE x.anomes = refDate][0].scheduleVariation,
        estimateToComplete: [x IN months WHERE x.anomes = refDate][0].estimadoParaConclusao,
        estimatesAtCompletion: [x IN months WHERE x.anomes = refDate][0].estimadoNaConclusao,
        earnedValue: [x IN months WHERE x.anomes = refDate][0].earnedValue,
        actualCost: [x IN months WHERE x.anomes = refDate][0].actualCost,
        plannedValue: [x IN months WHERE x.anomes = lastMonth][0].plannedCost,
        plannedValueRefMonth: [x IN months WHERE x.anomes = refDate][0].plannedCost
    } AS PerformanceIndexDto,

	CASE WHEN sCurve IS NULL OR sCurve
	THEN
		[p IN months | {
			date: toString(toInteger(p.anomes) / 100) + '-' + 
				  right('0' + toString(p.anomes % 100), 2),
			plannedCost: p.plannedCost, 
			actualCost: p.actualCost,
			estimatedCost: p.foreseenCost,
			earnedValue: p.earnedValue,
			actualWork: p.actualWork,
			actualScope: p.actualScope
		}] 
	ELSE NULL
	END AS EarnedValueByStepDto

RETURN {
    dashboardMonthDto: {
        tripleConstraint: TripleConstraintDto,
        performanceIndex: PerformanceIndexDto
    },
    earnedValueByStepDto: EarnedValueByStepDto
} AS RESULT
