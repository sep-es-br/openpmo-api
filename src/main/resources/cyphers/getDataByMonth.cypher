WITH 
	$scope AS _scope,
	$baselineId AS _baselineId,
	$monthYear AS _anomesRef

// Pega todas as entregas MASTER não excluídas nem canceladas
MATCH (Wp:Workpack)<-[:IS_IN*1..]-(w:Deliverable)-[:BELONGS_TO]->(p:Plan)-[:IS_ADOPTED_BY]->(o:Office)
WHERE (id(w) = _scope OR id(Wp) = _scope OR id(p) = _scope)
  AND (NOT w.deleted AND NOT w.canceled)

WITH DISTINCT w, _baselineId, _anomesRef, p,
  toInteger(apoc.date.field(datetime(p.start).epochMillis, "year"))*100 +
  toInteger(apoc.date.field(datetime(p.start).epochMillis, "month")) AS planoStart,
  toInteger(apoc.date.field(datetime(p.finish).epochMillis, "year"))*100 +
  toInteger(apoc.date.field(datetime(p.finish).epochMillis, "month")) AS planoFinish

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
		(toInteger((apoc.date.field(datetime(sc.start).epochMillis,"month")-1+st.periodFromStart)/12) + apoc.date.field(datetime(sc.start).epochMillis,"year"))*100 +
		(toInteger((apoc.date.field(datetime(sc.start).epochMillis,"month")-1+st.periodFromStart)%12)+1) AS anomes
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
		(toInteger((apoc.date.field(datetime(sn_sc.start).epochMillis,"month")-1+sn_st.periodFromStart)/12) + apoc.date.field(datetime(sn_sc.start).epochMillis,"year"))*100 +
		(toInteger((apoc.date.field(datetime(sn_sc.start).epochMillis,"month")-1+sn_st.periodFromStart)%12)+1) AS anomes
	RETURN anomes,
		0.0 AS fisicoreprogramado,
		0.0 AS fisicorealizado,
		toFloat(sn_st.plannedWork) AS fisicoplanejado,
		0.0 AS custoreprogramado,
		0.0 AS custorealizado,
		round(toFloat(sn_co.plannedCost),2) AS custoplanejado
}


// Filtra meses fora do plano e agrega por anomes/wId
WITH anomes, id(w) AS wId,planoStart,planoFinish,p,	
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
WITH wId, wCompleted, _anomesRef,
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
  wId, wCompleted, _anomesRef,
  masterStart,
  masterEnd,
  snapshotStart,
  snapshotEnd,
  rows,
  round(reduce(total=0.0, r IN rows | total + coalesce(r.custoplanejado,0)),2) AS bacEntrega,
  round(reduce(total=0.0, r IN rows | total + coalesce(r.fisicoplanejado,0)),2) AS fisicoTotal

// Volto para o formato tabular, PARA APURAR O PERCENTUAL MENSAL
UNWIND rows AS r
WITH
	wId, wCompleted, bacEntrega, fisicoTotal, _anomesRef,
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
	r.custoplanejado      		AS custoPlanejado
order by wId, anomes

// Agrupa novamente por entrega (wId) os rows mensais existentes, agora para acumular
WITH wId, wCompleted, bacEntrega, fisicoTotal, _anomesRef,
	masterStart,
	masterEnd,
	snapshotStart,
	snapshotEnd,
	collect({
		anomes: anomes,
		custoReprogramado: custoReprogramado,
		custoPlanejado: custoPlanejado,
		custoRealizado: custoRealizado,
		prcFisicoRealizado: prcFisicoRealizado	
	}) AS rows


// Coleta todas as entregas em uma lista para calcular globalAnomes
WITH collect({
  wId: wId,
  _anomesRef: _anomesRef,
  wCompleted: wCompleted,
  masterStart: masterStart,
  masterEnd: masterEnd,
  snapshotStart: snapshotStart,
  snapshotEnd: snapshotEnd,
  rows: rows,
  bacEntrega: bacEntrega
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
	ent.wCompleted as wCompleted,
	ent.masterStart AS masterStart,
	ent.masterEnd AS masterEnd,
	ent.snapshotStart AS snapshotStart,
	ent.snapshotEnd AS snapshotEnd,
	ent.rows AS rows, 
	ent.bacEntrega AS bacEntrega, 
	globalAnomes


// Para cada anomes do conjunto global, achar row existente ou criar row zero
WITH wId, wCompleted, bacEntrega, masterStart, masterEnd, snapshotStart, snapshotEnd, _anomesRef,
     [a IN apoc.coll.toSet(globalAnomes) | 
        COALESCE(
          [x IN rows WHERE x.anomes = a | x][0],
          { 
			anomes: a, 
			custoReprogramado:0.0, 
			custoPlanejado:0.0, 
			custoRealizado:0.0, 
			prcFisicoRealizado:0.0 }
        )
     ] AS VALORES


// Acumula mês a mês com REDUCE (agora sem faltar meses)
WITH 
  wId, wCompleted, _anomesRef,
  bacEntrega AS bac, masterStart, masterEnd, snapshotStart, snapshotEnd,
  REDUCE(
    s = { crAcum:0.0, cpAcum:0.0, cgAcum:0.0, pcfrAcum:0.0, listAcum: [] },
    item IN VALORES |
    {
      crAcum: s.crAcum + item.custoRealizado,
      cpAcum: s.cpAcum + item.custoPlanejado,
      cgAcum: s.cgAcum + item.custoReprogramado,
      pcfrAcum: s.pcfrAcum + item.prcFisicoRealizado,
      listAcum: s.listAcum + 
        [{
          anomes: item.anomes,
          crAcum: s.crAcum + item.custoRealizado,
          cpAcum: s.cpAcum + item.custoPlanejado,
          cgAcum: s.cgAcum + item.custoReprogramado,
          pcfrAcum: s.pcfrAcum + item.prcFisicoRealizado,
		  // VA(Valor Agregado) = %Concluído(pcfrAcum) * VP(cpAcum)
          va: (s.pcfrAcum + item.prcFisicoRealizado) * (s.cpAcum + item.custoPlanejado)
        }]
    }
  ) AS accumResult

// Expande os acumulados por entrega para agregação por mês total do projeto
UNWIND accumResult.listAcum AS list
WITH
  wId, wCompleted, _anomesRef,
  bac AS bacEntrega, masterStart, masterEnd, snapshotStart, snapshotEnd,
  list.anomes AS anomes,
  list.pcfrAcum as pcFisicoRealizadoAcum,
  list.cgAcum AS custoReprogramado_Acum,
  list.crAcum AS custoRealizado_Acum,
  list.cpAcum AS custoPlanejado_Acum,
  list.va AS va

// Agora agregue entre entregas para obter os totais mensais do conjunto
WITH  
  anomes, _anomesRef,
  min(wCompleted) as wCompleted,
  sum(custoReprogramado_Acum) AS custoReprogramado_MensalTotal,
  sum(custoRealizado_Acum)   AS custoRealizado_MensalTotal,
  sum(custoPlanejado_Acum)   AS custoPlanejado_MensalTotal,
  sum(va) AS valorAgregado_MensalTotal,
  avg(pcFisicoRealizadoAcum) AS pcFisicoRealizadoAcumMesMedio,
  // a seguir derivamos as métricas usando os valores agregados
  sum(va - custoPlanejado_Acum) AS variacaoDePrazo_MensalTotal,
  sum(va - custoRealizado_Acum) AS variacaoDeCusto_MensalTotal,
  sum(custoRealizado_Acum + bacEntrega - va) AS estimadoNaConclusao,   // observe: bacEntrega não está neste scope; adaptar se precisar usar bac por entrega
  sum(bacEntrega - va) 	AS estimadoParaConclusao,
  min(snapshotStart) 	AS snapshotStart, 
  max(snapshotEnd) 		AS snapshotEnd,
  min(masterStart) 		AS masterStart, 
  max(masterEnd) 		AS masterEnd

WITH 
    anomes																			AS mes,
	_anomesRef																		AS mesRef,
	max(anomes) as lastMonth,
    round(custoReprogramado_MensalTotal,2) 											AS custoReprogramadoAcumuladoMes,
    round(custoPlanejado_MensalTotal,2) 											AS custoPlanejadoAcumuladoMes,
    round(custoRealizado_MensalTotal,2) 											AS custoRealizadoAcumuladoMes,
	case 
		when (custoPlanejado_MensalTotal = 0 or valorAgregado_MensalTotal = 0)
		then pcFisicoRealizadoAcumMesMedio 
		else round(valorAgregado_MensalTotal/custoPlanejado_MensalTotal,4) 
	end 																			AS pcFisicoRealizadoAcumMesMedio,
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
	case 
		when masterStart > toString(date()) 
		then '' 
		else masterStart 
	end as actualStartDate,
	masterStart as reprogStartDate,
	case when masterStart > toString(date()) 
	then '' 
	else
		case when masterEnd > toString(date()) 
		then toString(date())
		else 
			case when wCompleted
			then masterEnd
			else toString(date())
			end
		end
	end as actualEndDate,
	masterEnd as reprogEndDate

WITH mes, mesRef, custoReprogramadoAcumuladoMes, custoPlanejadoAcumuladoMes, custoRealizadoAcumuladoMes, 
	pcFisicoRealizadoAcumMesMedio, valorAgregado, variacaoPrazo, variacaoCusto, estimadoNaConclusao, 
	estimadoParaConclusao, idc, idp, plannedStartDate,plannedEndDate, actualStartDate, reprogStartDate, 
	actualEndDate, reprogEndDate,
	CASE
		WHEN actualStartDate IS NOT NULL AND actualEndDate IS NOT NULL THEN
		  CASE
			WHEN (duration.inMonths(date(actualStartDate), date(actualEndDate)).months + 1) = 0 THEN
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
				WHEN (duration.inMonths(date(plannedStartDate), date(plannedEndDate)).months + 1) = 0 THEN
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
			WHEN (duration.inMonths(date(reprogStartDate), date(reprogEndDate)).months + 1) = 0 THEN
				CASE
					WHEN duration.inDays(date(reprogStartDate), date(reprogEndDate)).days > 0 THEN 1
					ELSE 0
				END
			ELSE (duration.inMonths(date(reprogStartDate), date(reprogEndDate)).months + 1)
		END
	ELSE NULL
	END AS scheduleForeseenValue
	
WITH
COLLECT({
	plannedCost: custoPlanejadoAcumuladoMes,
	actualCost: custoRealizadoAcumuladoMes,
	estimatedCost: custoReprogramadoAcumuladoMes,
	earnedValue: valorAgregado,
	anomes: mes,
	
	actualWork: pcFisicoRealizadoAcumMesMedio,
 	
	costVariation: variacaoCusto,
	scheduleVariation: variacaoPrazo,

	estimadoNaConclusao: estimadoNaConclusao, 
	estimadoParaConclusao: estimadoParaConclusao, 
	idc: idc, 
	idp: idc
}) as months,
max(mesRef) AS refDate,
max(mes) as lastMonth,
max(plannedStartDate) AS schedulePlannedStartDate,
max(plannedEndDate) AS schedulePlannedEndDate,
max(reprogStartDate) AS scheduleForeseenStartDate,
max(reprogEndDate) AS scheduleForeseenEndDate,
max(actualStartDate) AS scheduleActualStartDate,
max(actualEndDate) AS scheduleActualEndDate,
max(schedulePlannedValue) AS schedulePlannedValue,
max(scheduleForeseenValue) AS scheduleForeseenValue,
max(scheduleActualValue) AS scheduleActualValue

WITH 
    {
        costPlannedValue: [x IN months WHERE x.anomes = lastMonth][0].plannedCost,
        costActualValue: [x IN months WHERE x.anomes = refDate][0].actualCost,
        schedulePlannedStartDate: schedulePlannedStartDate,
        schedulePlannedEndDate: schedulePlannedEndDate,
        scheduleActualStartDate: scheduleActualStartDate,
        scheduleActualEndDate: scheduleActualEndDate,
        schedulePlannedValue: schedulePlannedValue,
        scheduleActualValue: scheduleActualValue,
        scopeActualVariationPercent: [x IN months WHERE x.anomes = refDate][0].actualWork,
        scopePlannedVariationPercent: 1
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

    [p IN months | {
        date: toString(toInteger(p.anomes) / 100) + '-' + 
              right('0' + toString(p.anomes % 100), 2),
        plannedCost: p.plannedCost, 
        actualCost: p.actualCost,
        estimatedCost: p.estimatedCost,
        earnedValue: p.earnedValue,
        actualWork: p.actualWork
    }] AS EarnedValueByStepDto

RETURN {
    dashboardMonthDto: {
        tripleConstraint: TripleConstraintDto,
        performanceIndex: PerformanceIndexDto
    },
    earnedValueByStepDto: EarnedValueByStepDto
} AS RESULT