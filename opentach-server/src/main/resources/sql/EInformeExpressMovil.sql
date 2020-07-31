 SELECT
 	CDCONDUCTOR_CONT.IDCONDUCTOR, 
	CDCONDUCTOR_CONT.NOMBRE, 
	CDCONDUCTOR_CONT.APELLIDOS, 
	(
		select 
			TO_CHAR(Floor(TCP/60),'FM00') || nvl2(TCP, ':', '') || TO_CHAR((TCP - TRUNC(TCP/60)*60),'FM00')			
		from 
			(select TCP,row_number() over (order by fecini desc) as filternum from CDPERIODOS1_TEMP where TIPO = 'PCS') 
		where 
			filternum = 1 -- la más reciente
	) acum_semanal,
	(
		select 
			TO_CHAR(TRUNC(TCP/60),'FM00') || nvl2(TCP, ':', '') || TO_CHAR((TCP - TRUNC(TCP/60)*60),'FM00')
		from 
			(select TCP,row_number() over (order by fecini desc) as filternum from CDPERIODOS1_TEMP where TIPO = 'PCBS') 
		where 
			filternum = 2 -- la segunda más reciente
	) acum_bisemanal, 
	(
		select 
			TO_CHAR(TRUNC( (cast((select valor from cdparametros where cod='P15') as number)-TCP)/60),'FM00') || nvl2(TCP, ':', '') || TO_CHAR(( (cast((select valor from cdparametros where cod='P15') as number) -TCP) - TRUNC( (cast((select valor from cdparametros where cod='P15') as number) -TCP)/60)*60),'FM00')			
		from 
			(select TCP,row_number() over (order by fecini desc) as filternum from CDPERIODOS1_TEMP where TIPO = 'PCS') 
		where 
			filternum = 1 -- la más reciente
	) disp_semanal,
	(
		select 
			TO_CHAR(TRUNC( (cast((select valor from cdparametros where cod='P14') as number)-TCP)/60),'FM00') || nvl2(TCP, ':', '') || TO_CHAR(( (cast((select valor from cdparametros where cod='P14') as number) -TCP) - TRUNC( (cast((select valor from cdparametros where cod='P14') as number) -TCP)/60)*60),'FM00')			
		from 
			(select TCP,row_number() over (order by fecini desc) as filternum from CDPERIODOS1_TEMP where TIPO = 'PCBS') 
		where 
			filternum = 2 -- la segunda más reciente
	) disp_bisemanal, 
	(
		select 
			TO_CHAR(TRUNC(sum(MINUTOS_A)/60),'FM00') || nvl2(sum(MINUTOS_A), ':', '') || TO_CHAR((sum(MINUTOS_A) - TRUNC(sum(MINUTOS_A)/60)*60),'FM00')			
		FROM
			CDTRAMOS_DEF_TEMP
		WHERE
			TIPO IN (4)
			AND FECINI >= (select max(fecfin) from CDPERIODOS1_TEMP where tipo = 'PDD') AND FECINI >= (select max(fecfin) from CDPERIODOS1_TEMP where tipo = 'PDS')  
	) cond_dia_acum,
	(
		select 
			count(*)
		FROM
			CDPERIODOS1_TEMP
		WHERE
			TIPO = 'PCD'
			AND TIPODD=2
			AND FECINI>= (select max(FECFIN) from CDPERIODOS1_TEMP where tipo = 'PDS')
	) cond_10_realizadas,
	(
		select 
			2-count(*)
		FROM
			CDPERIODOS1_TEMP
		WHERE
			TIPO = 'PCD'
			AND TIPODD=2
			AND FECINI>= (select max(FECFIN) from CDPERIODOS1_TEMP where tipo = 'PDS')
	) cond_10_disponibles,
	(
		select 
			count(*)
		FROM
			CDPERIODOS1_TEMP
		WHERE
			TIPO = 'PDD'
			AND TIPODD=2
			AND FECINI>= (select max(FECFIN) from CDPERIODOS1_TEMP where tipo = 'PDS')
	) descansos_9_realizados,
	(
		select 
			3-count(*)
		FROM
			CDPERIODOS1_TEMP
		WHERE
			TIPO = 'PDD'
			AND TIPODD=2
			AND FECINI>= (select max(FECFIN) from CDPERIODOS1_TEMP where tipo = 'PDS')
	) descansos_9_disponibles,
	(
		select 
			TO_CHAR(fecfin, 'DD/MM/YYYY HH24:mi')
		from 
			(select CDPERIODOS1_TEMP.fecfin,row_number() over (order by fecini desc) as filternum from CDPERIODOS1_TEMP where TIPO = 'PDS') 
		where 
			filternum = 1 -- la más reciente
	) ultimo_desc_sem,
	(
		select '-' from dual
	) horas_desc_sem_comp,
	(
		select 
			TO_CHAR(fecfin+6, 'DD/MM/YYYY HH24:mi')
		from 
			(select CDPERIODOS1_TEMP.fecfin,row_number() over (order by fecini desc) as filternum from CDPERIODOS1_TEMP where TIPO = 'PDS') 
		where 
			filternum = 1 -- la más reciente
	) fec_max_ds
from	
	CDCONDUCTOR_CONT   	   	
WHERE   
	CDCONDUCTOR_CONT.IDCONDUCTOR = ? 
	AND CDCONDUCTOR_CONT.CG_CONTRATO = ?