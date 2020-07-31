SELECT
	a.ini_semana,  
	a.fin_semana,
	a.ultima_fecha,
	(
		select 
			sum(minutos) as minutos 
		from 
			cdactividades  
		where 
			fec_comienzo >= (to_date(a.ini_semana ,'DD/MM/YYYY HH24:mi') - 7 ) 
			and fec_fin <= (to_date(a.fin_semana,'DD/MM/YYYY HH24:mi')  - 7)
			and tpactividad = 4
			and idconductor = ? 
			AND numreq = ? 
	) cond_acumulada,
					
	(
		select 
			sum(minutos)as minutos 
		from 
			cdactividades
		where 
			fec_comienzo >= (to_date(a.ini_semana ,'DD/MM/YYYY HH24:mi')-14) 
			and fec_fin <= (to_date(a.fin_semana ,'DD/MM/YYYY HH24:mi') -14)
			and tpactividad = 4
			and idconductor = ? 
			AND numreq = ? 
	) cond_acumulada2sem,
	( 
		SELECT 
			TO_CHAR(MAX(fecfin), 'DD/MM/YYYY HH:mi')  AS ULTIMO_DESC
		FROM 
			CDDESCANSOS_TEMP 
		WHERE 
			TIPO = 'DS' 
			AND fecfin < a.ultima_fecha
		and idconductor = ? 
	) ultimoDesc, 
	(
		SELECT 
			TO_CHAR(MAX(fecfin) + 7, 'DD/MM/YYYY HH:mi')  AS PLAZO_MAXINO
		FROM 
			CDDESCANSOS_TEMP 
		WHERE 
			TIPO = 'DS' AND fecfin < SYSDATE
			and idconductor = ? 
	) plazoSigDesc,  
	(
		SELECT  
			(CASE 
				WHEN (CONDSEMANAL.MIN - PARAM15.VALOR) >((CONDBISEMANAL.MIN + CONDSEMANAL.MIN) - PARAM14.VALOR)	THEN 
					(case 
						when ((CONDBISEMANAL.MIN + CONDSEMANAL.MIN) - PARAM14.VALOR)>0 then  
							((CONDBISEMANAL.MIN + CONDSEMANAL.MIN) - PARAM14.VALOR) 
						else 
							0 
					end) 
				ELSE 
					(case 
						when  (CONDSEMANAL.MIN - PARAM15.VALOR)>0 then 
							(CONDSEMANAL.MIN - PARAM15.VALOR) 
						else 
							0 
						end)
				END) AS COND_MAXIMA
		FROM  
			(
				select 
					sum(minutos) AS MIN 
				from 
					cdactividades,   
					(
						SELECT 
							TO_CHAR(TRUNC(max (fec_comienzo) + 7, 'ww'),'DD/MM/YYYY')     AS INI_SEMANA, 
							TO_CHAR(TRUNC(max (fec_comienzo)+ 7 , 'ww') + 6,'DD/MM/YYYY') AS FIN_SEMANA,
							max (fec_comienzo)  as ultima_fecha
						from 
							cdactividades 
						where 
							numreq = ? 
							and idconductor = ? 
					) A 
				where   
					fec_comienzo >= (to_date(A.INI_SEMANA ,'DD/MM/YYYY HH24:mi') - 7 )  
					and fec_fin <= (to_date(A.FIN_SEMANA,'DD/MM/YYYY HH24:mi')  - 7) 
					and tpactividad = 4  
					and idconductor = ? 
					AND numreq = ? 
			) CONDSEMANAL, 
			(
				select 
					sum(minutos) AS MIN
				from 
					cdactividades,
					(
						SELECT 
							TO_CHAR(TRUNC(max (fec_comienzo) + 7, 'ww'),'DD/MM/YYYY') AS INI_SEMANA, 
							TO_CHAR(TRUNC(max (fec_comienzo)+ 7 , 'ww') + 6,'DD/MM/YYYY') AS FIN_SEMANA,
							max (fec_comienzo)  as ultima_fecha
						from 
							cdactividades
						where
							numreq = ? 
							and idconductor = ? 
					) A
				where 
					fec_comienzo >= (to_date(A.INI_SEMANA ,'DD/MM/YYYY HH24:mi') - 14 )
					and fec_fin <= (to_date(A.FIN_SEMANA ,'DD/MM/YYYY HH24:mi') -14) 
					and tpactividad = 4
					and idconductor = ? 
					AND numreq = ? 
			) CONDBISEMANAL, 
			(
				SELECT 
					VALOR 
				FROM 
					CDPARAMETROS 
				WHERE 
					COD = 'P14'
			) PARAM14, 
			(
				SELECT 
					VALOR 
				FROM 
					CDPARAMETROS 
				WHERE COD = 'P15'
			) PARAM15
	) condDisponible ,
	( 
		SELECT   
		--DDR DISPONIBLES HASTA EL PROXIMO DS
			( 3 - COUNT(*)) AS DISPONIBLES
		FROM 
			(
				SELECT 
					MAX(FECFIN)  AS FECHA 
				FROM 
					CDDESCANSOS_TEMP
				WHERE 
					TIPO = 'DS'
					AND idconductor =?
			) ULTIMO_DD,
			CDDESCANSOS_TEMP 
		WHERE  
			TIPODD='3' 
			AND idconductor =?  
			AND CDDESCANSOS_TEMP.FECINI > ULTIMO_DD.FECHA 
			AND CDDESCANSOS_TEMP.FECINI < SYSDATE
	) DDR_DISP_DS,
	------------9
	(
		SELECT   -- CONDUCCIONES DE HASTA 10 HORAS DISPONIBLES HASTA EL PROXIMO DS
			( 3 - COUNT(*)) AS DISPONIBLES 
		FROM 
			(
				SELECT 
					MAX(FECFIN)  AS FECHA 
				FROM 
					CDDESCANSOS_TEMP
				WHERE 
					TIPO = 'DS' 
					AND idconductor = ? 
			) ULTIMO_DD, 
			CDACTIVIDADES_TEMPPRES
		WHERE  
			idconductor = ? 
			AND NUMREQ = ? 
			AND CDACTIVIDADES_TEMPPRES.tpactividad = '4' 
			AND MINUTOS > 600 
			AND CDACTIVIDADES_TEMPPRES.FEC_COMIENZO > ULTIMO_DD.FECHA 
			AND CDACTIVIDADES_TEMPPRES.FEC_FIN < SYSDATE 
	) H10_DS
------------------10
FROM  
	(
		select  
			TO_CHAR(TRUNC(max (fec_comienzo) + 7, 'ww'),'DD/MM/YYYY')     AS INI_SEMANA,
			TO_CHAR(TRUNC(max (fec_comienzo)+ 7 , 'ww') + 6,'DD/MM/YYYY') AS FIN_SEMANA,
			max (fec_comienzo)  as ultima_fecha
		from 
			cdactividades 
		where  
			numreq = ? 
			and idconductor = ? 
	) A